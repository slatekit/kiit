/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.databases


import java.sql._
import scala.reflect.runtime.universe._
import scala.collection.mutable.ListBuffer
import slate.common.{DateTime, Model}
import slate.common.mapper.{MapperSourceRecord, Mapper}
import DbUtils._


/**
 * Light-weight database wrapper.
 * @param _dbCon: DbConfig.loadFromUserFolder(".slate", "db.txt")
 *   although tested using mysql, sql-server should be
 * 1. sql-server: driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
 * 2. sql-server: url = "jdbc:sqlserver://<server_name>:<port>;database=<database>;user=<user>;
  * password=<password>;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
 */
class Db(private val _dbCon:DbConString) {

  /**
    * registers the jdbc driver
 *
    * @return
    */
  def open(): Db =
  {
    Class.forName(_dbCon.driver)
    this
  }


  /**
   * creates a table in the database that matches the schema(fields) in the model supplied
 *
   * @param model : The model associated with the table.
   */
  def createTable(model:Model): Unit =
  {
    val builder = new DbBuilder()
    val sql = builder.addTable(model)
    execute(_dbCon, (con, stmt) => stmt.execute(sql), error)
  }


  /**
   * drops the table from the database
 *
   * @param name : The name of the table
   */
  def dropTable(name:String): Unit =
  {
    val builder = new DbBuilder()
    val sql = builder.dropTable(name)
    executeStmt(_dbCon, (con, stmt) => stmt.execute(sql), error)
  }


  /**
    * gets a scalar string value using the sql provided
 *
    * @param sql : The sql text
    * @return
    */
  def getScalar[T](sql:String, typ:Type, inputs:Option[List[Any]]): Option[T]  = {

    val result = executePrepAs[Option[T]](_dbCon, sql, (rs, stmt) => {

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // execute
      val rs = stmt.executeQuery()
      val any =  rs.next()
      val res:Option[T] =
        if(any) {
          DbUtils.getScalar[T](rs, typ)
        }
        else
          None
      res
    }, error)
    result.fold[Option[T]](None)( r => r )
  }


  /**
   * gets a scalar string value using the sql provided
 *
   * @param sql : The sql text
   * @return
   */
  def getScalarString(sql:String, inputs:Option[List[Any]] = None): String  = {
    getScalar[String](sql, typeOf[String], inputs).get
  }


  /**
    * gets a scalar int value using the sql provided
 *
    * @param sql : The sql text
    * @return
    */
  def getScalarInt(sql:String, inputs:Option[List[Any]] = None): Int  = {
    getScalar[Int](sql, typeOf[Int], inputs).get
  }


  /**
    * gets a scalar long value using the sql provided
 *
    * @param sql : The sql text
    * @return
    */
  def getScalarLong(sql:String, inputs:Option[List[Any]] = None): Long  = {
    getScalar[Long](sql, typeOf[Long], inputs).get
  }


  /**
    * gets a scalar double value using the sql provided
 *
    * @param sql : The sql text
    * @return
    */
  def getScalarDouble(sql:String, inputs:Option[List[Any]] = None): Double  = {
    getScalar[Double](sql, typeOf[Double], inputs).get
  }


  /**
    * gets a scalar bool value using the sql provided
 *
    * @param sql : The sql text
    * @return
    */
  def getScalarBool(sql:String, inputs:Option[List[Any]] = None): Boolean  = {
    getScalar[Boolean](sql, typeOf[Boolean], inputs).get
  }


  /**
    * gets a scalar bool value using the sql provided
 *
    * @param sql : The sql text
    * @return
    */
  def getScalarDate(sql:String, inputs:Option[List[Any]] = None): DateTime  = {
    getScalar[DateTime](sql, typeOf[DateTime], inputs).get
  }


  /**
   * maps a single item using the sql supplied
 *
   * @param sql    : The sql
   * @param mapper : THe mapper to map the item of type T
   * @tparam T     : The type of the item
   * @return
   */
  def mapOne[T >: Null](sql:String, mapper:Mapper, inputs:Option[List[Any]] = None): Option[T]  = {
    val res = executeQuery[T](sql, (rs) =>
    {
      val rec = new MapperSourceRecord(rs)
      if(rs.next())
        mapper.mapFrom(rec).asInstanceOf[T]
      else
        null
    }, false, inputs)
    res
  }


  /**
   * maps multiple items using the sql supplied
 *
   * @param sql    : The sql
   * @param mapper : THe mapper to map the item of type T
   * @tparam T     : The type of the item
   * @return
   */
  def mapMany[T](sql:String, mapper:Mapper, inputs:Option[List[Any]] = None): Option[List[T]]  = {
    val res = executeQuery[List[T]](sql, (rs) =>
    {
      val rec = new MapperSourceRecord(rs)
      val buf = new ListBuffer[T]()
      while(rs.next())
      {
        val item = mapper.mapFrom(rec)
        buf.append(item.asInstanceOf[T])
      }
      buf.toList
    }, false, inputs)
    res
  }


  /**
   * executes the update sql or stored proc
 *
   * @param sql     : The sql or stored proc
   * @param inputs  : The inputs for the sql or stored proc
   * @return        : The number of affected records
   */
  def update(sql:String, inputs:Option[List[Any]] = None):Int = {
    val result = executePrepAs[Int](_dbCon, sql, (con, stmt) => {

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // update and get number of affected records
      val count = stmt.executeUpdate()
      count
    }, error)
    result.getOrElse(0)
  }


  /**
   * executes an insert using the sql or stored proc and gets the id
 *
   * @param sql : The sql or stored proc
   * @param inputs  : The inputs for the sql or stored proc
   * @return    : The id ( primary key )
   */
  def insert(sql:String, inputs:Option[List[Any]] = None):Long = {
    val res = execute[Long](_dbCon, (con:Connection) => {

      val stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // execute the update
      stmt.executeUpdate()

      // get id.
      val rs = stmt.getGeneratedKeys
      val id = if (rs.next()){
        rs.getLong(1)
      }
      else
        0L
      DbUtils.close(stmt)
      id
    }, error)
    res.getOrElse(0)
  }


  private def executeQuery[T](sql:String,
                           callback: (ResultSet) => T,
                           moveNext:Boolean = true,
                           inputs:Option[List[Any]] = None): Option[T] = {
    val result = executePrepAs[Option[T]](_dbCon, sql, (con, stmt) =>
    {

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // execute
      val rs = stmt.executeQuery()
      
      val any =  if(moveNext) rs.next() else true
      if(any)
        Option(callback(rs))
      else
        None
    }, error)
    result.fold[Option[T]](None)( r => r)
  }


  private def error(ex:Exception): Unit =
  {
    val msg= ex.getMessage
    println(msg)
  }
  /*
  * DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserByEmail`(in email varchar(80))
BEGIN
	select * from `user` where email = email;
END$$
DELIMITER ;
  **/
}


/*
import java.sql._
import slate.common.DateTime
import scala.collection.mutable.ListBuffer
import scala.reflect.runtime.universe._
import DbUtils._


class DbCmd[+A](val sql:String, val inputs:Option[List[Any]], val tpe:Type) {

  val con:DbConString = DbConString.empty


  def run(): Option[A] = {
    var res:Option[A] = None

    // execute sql or proc
    execute(con, sql, (conn, stmt) => {

      // parameters for sql or proc
      fillArgs(stmt, inputs)

      // execute
      val rs = stmt.executeQuery()
      val any =  rs.next()
      if( any ) {
        res = map[A](rs)
      }
    }, error)
    res
  }


  def map[A](resultSet: ResultSet): Option[A] = ???


  def error(ex:Exception): Unit =
  {
    val msg= ex.getMessage
    println(msg)
  }
}



class DbCmdScalar[A:TypeTag](implicit typ:Type, sql:String, inputs:Option[List[Any]])
  extends DbCmd[A](sql, inputs, typ) {

  override def map[T](rs:ResultSet): Option[T] = {
    val pos = 1

    if(typ == typeOf[String] )       Option(rs.getString(pos).asInstanceOf[T])
    else if(typ == typeOf[Short]   ) Option(rs.getShort(pos).asInstanceOf[T])
    else if(typ == typeOf[Int]     ) Option(rs.getInt(pos).asInstanceOf[T])
    else if(typ == typeOf[Long]    ) Option(rs.getLong(pos).asInstanceOf[T])
    else if(typ == typeOf[Double]  ) Option(rs.getDouble(pos).asInstanceOf[T])
    else if(typ == typeOf[Boolean] ) Option(rs.getBoolean(pos).asInstanceOf[T])
    else if(typ == typeOf[DateTime]) Option(new DateTime(rs.getTimestamp(pos)).asInstanceOf[T])
    else None
  }
}



class DbCmdGetOne[A:TypeTag](implicit typ:Type, sql:String, inputs:Option[List[Any]], f:(ResultSet) => Option[A])
  extends DbCmd[A](sql, inputs, typ) {

  override def map[T](rs:ResultSet): Option[T] = {

    val res = if(rs.next()) f(rs) else None
    res.fold[Option[T]](None)( v => Some(v.asInstanceOf[T]))
  }
}



class DbCmdGetMany[A:TypeTag](implicit typ:Type, sql:String, inputs:Option[List[Any]], f:(ResultSet) => Option[A])
  extends DbCmd[A](sql, inputs, typ) {

  override def map[A](rs:ResultSet): Option[scala.List[A]] = {
    val items = new ListBuffer[A]()
    while(rs.next())
    {
      val item = if(rs.next()) f(rs) else None
      items.append( item.get.asInstanceOf[A] )
    }
    Option(items.toList)
  }
}



class DbCmdUpdate(implicit typ:Type, sql:String, inputs:Option[List[Any]])
  extends DbCmd[Int](sql, inputs, typ) {

  override def run(): Option[Int] = {
    var res:Option[Int] = None

    // execute sql or proc
    execute(con, sql, (conn, stmt) => {

      // parameters for sql or proc
      fillArgs(stmt, inputs)

      // execute
      res = Some(stmt.executeUpdate())
    }, error)
    res
  }
}
*/