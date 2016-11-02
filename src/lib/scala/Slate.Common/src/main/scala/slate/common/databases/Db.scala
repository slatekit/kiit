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


/**
 * Light-weight database wrapper.
 * @param _dbCon: DbConfig.loadFromUserFolder(".slate", "db.txt")
 *   although tested using mysql, sql-server should be
 * 1. sql-server: driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
 * 2. sql-server: url = "jdbc:sqlserver://<server_name>:<port>;database=<database>;user=<user>;
  * password=<password>;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
 */
class Db(private val _dbCon:DbConString) {

  private var _isOpened = false


  /**
    * registers the jdbc driver
    * @return
    */
  def open(): Db =
  {
    if(_isOpened)
      return this

    //STEP 1: Register JDBC driver
    Class.forName(_dbCon.driver)
    _isOpened = true
    this
  }


  /**
    * gets a new jdbc connection via Driver manager
    * @return
    */
  def getConnection() : Connection =
  {
    if(_dbCon.driver == "com.mysql.jdbc.Driver")
    {
      return DriverManager.getConnection(_dbCon.url, _dbCon.user, _dbCon.password)
    }
    DriverManager.getConnection(_dbCon.url)
  }


  /**
   * creates a table in the database that matches the schema(fields) in the model supplied
   * @param model : The model associated with the table.
   */
  def createTable(model:Model): Unit =
  {
    val builder = new DbBuilder()
    val sql = builder.addTable(model)
    executeInternal((con, stmt) => stmt.execute(sql))
    log(sql)
  }


  /**
   * drops the table from the database
   * @param name : The name of the table
   */
  def dropTable(name:String): Unit =
  {
    val builder = new DbBuilder()
    val sql = builder.dropTable(name)
    executeInternal((con, stmt) => stmt.execute(sql))
    log(sql)
  }


  /**
    * gets a scalar string value using the sql provided
    * @param sql : The sql text
    * @return
    */
  def querySingle[T](sql:String, typ:Type, inputs:Option[List[Any]]): Option[T]  = {

    var res:Option[T] = None

    executeInternalProc(sql, (rs, stmt) => {

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // execute
      val rs = stmt.executeQuery()
      val any =  rs.next()
      if(any) {
        res = getResultValue[T](rs, typ, 1)
      }
    })
    res
  }


  /**
    * gets a scalar string value using the sql provided
    * @param sql : The sql text
    * @return
    */
  def getScalarString(sql:String, inputs:Option[List[Any]] = None): String  = {
    querySingle[String](sql, typeOf[String], inputs).get
  }


  /**
    * gets a scalar int value using the sql provided
    * @param sql : The sql text
    * @return
    */
  def getScalarInt(sql:String, inputs:Option[List[Any]] = None): Int  = {
    querySingle[Int](sql, typeOf[Int], inputs).get
  }


  /**
    * gets a scalar long value using the sql provided
    * @param sql : The sql text
    * @return
    */
  def getScalarLong(sql:String, inputs:Option[List[Any]] = None): Long  = {
    querySingle[Long](sql, typeOf[Long], inputs).get
  }


  /**
    * gets a scalar double value using the sql provided
    * @param sql : The sql text
    * @return
    */
  def getScalarDouble(sql:String, inputs:Option[List[Any]] = None): Double  = {
    querySingle[Double](sql, typeOf[Double], inputs).get
  }


  /**
    * gets a scalar bool value using the sql provided
    * @param sql : The sql text
    * @return
    */
  def getScalarBool(sql:String, inputs:Option[List[Any]] = None): Boolean  = {
    querySingle[Boolean](sql, typeOf[Boolean], inputs).get
  }


  /**
    * gets a scalar bool value using the sql provided
    * @param sql : The sql text
    * @return
    */
  def getScalarDate(sql:String, inputs:Option[List[Any]] = None): DateTime  = {
    querySingle[DateTime](sql, typeOf[DateTime], inputs).get
  }


  /**
   * maps a single item using the sql supplied
   * @param sql    : The sql
   * @param mapper : THe mapper to map the item of type T
   * @tparam T     : The type of the item
   * @return
   */
  def mapOne[T >: Null](sql:String, mapper:Mapper, inputs:Option[List[Any]] = None): Option[T]  = {
    var res:Option[Any] = None
    executeQuery(sql, (rs) =>
    {
      val rec = new MapperSourceRecord(rs)
      if(rs.next())
      {
        res = mapper.mapFrom(rec)
      }
    }, false, inputs)

    val finalResult = if( res.isEmpty ) None else Some(res.get.asInstanceOf[T])
    finalResult
  }


  /**
   * maps multiple items using the sql supplied
   * @param sql    : The sql
   * @param mapper : THe mapper to map the item of type T
   * @tparam T     : The type of the item
   * @return
   */
  def mapMany[T](sql:String, mapper:Mapper, inputs:Option[List[Any]] = None): Option[List[T]]  = {
    val res = new ListBuffer[T]()

    executeQuery(sql, (rs) =>
    {
      val rec = new MapperSourceRecord(rs)
      while(rs.next())
      {
        val item = mapper.mapFrom(rec)
        res.append(item.asInstanceOf[T])
      }
    }, false, inputs)
    res.toList

    val finalResult = if(res.size == 0) None else Some(res.toList)
    finalResult
  }


  /**
   * executes the update sql or stored proc
   * @param sql     : The sql or stored proc
   * @param inputs  : The inputs for the sql or stored proc
   * @return        : The number of affected records
   */
  def executeUpdate(sql:String, inputs:Option[List[Any]] = None):Int = {
    var res = 0
    executeInternalProc(sql, (con, stmt) => {

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // update and get number of affected records
      res = stmt.executeUpdate()
    })
    res
  }


  /**
   * executes an insert using the sql or stored proc and gets the id
   * @param sql : The sql or stored proc
   * @param inputs  : The inputs for the sql or stored proc
   * @return    : The id ( primary key )
   */
  def executeInsertGetId(sql:String, inputs:Option[List[Any]] = None):Long = {
    var res = 0L
    executeConnection( (con) => {

      val stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // execute the update
      stmt.executeUpdate()

      // get id.
      val rs = stmt.getGeneratedKeys()
      if (rs.next()){
        res = rs.getLong(1)
      }
      DbUtils.close(stmt)
    })
    res
  }


  private def executeQuery(sql:String,
                           callback: (ResultSet) => Unit,
                           moveNext:Boolean = true,
                           inputs:Option[List[Any]] = None) = {
    executeInternalProc(sql, (con, stmt) =>
    {

      // fill all the arguments into the prepared stmt
      fillArgs(stmt, inputs)

      // execute
      val rs = stmt.executeQuery()
      
      val any =  if(moveNext) rs.next() else true
      if(any)
      {
        callback(rs)
      }
    })
  }


  private def executeInternal(callback:(Connection, Statement) => Unit): Unit =
  {
    var conn:Connection = null
    var stmt:Statement = null
    try
    {
      conn = getConnection()
      stmt = conn.createStatement()
      callback(conn, stmt)
    }
    catch
    {
      case ex:Exception =>
      {
        handleException(ex)
      }
    }
    finally
    {
      DbUtils.close(stmt, conn)
    }
  }


  private def executeInternalProc(sql:String, callback:(Connection, PreparedStatement ) => Unit): Unit =
  {
    var conn:Connection = null
    var stmt:CallableStatement = null
    try
    {
      conn = getConnection()
      stmt = conn.prepareCall(sql)
      callback(conn, stmt)
    }
    catch
      {
        case ex:Exception =>
        {
          handleException(ex)
        }
      }
    finally
    {
      DbUtils.close(stmt, conn)
    }
  }


  private def executeConnection(callback:(Connection) => Unit): Unit =
  {
    var conn:Connection = null
    try
    {
      conn = getConnection()
      callback(conn)
    }
    catch
      {
        case ex:Exception =>
        {
          handleException(ex)
        }
      }
    finally
    {
      DbUtils.close(conn)
    }
  }


  private def handleException(ex:Exception): Unit =
  {
    val msg= ex.getMessage
    println(msg)
  }


  private def log(sql:String): Unit =
  {
    println(sql)
  }


  private def fillArgs(stmt:PreparedStatement, inputs:Option[List[Any]]):Unit = {
    inputs.fold(Unit)( all => {
      var pos = 1
      for(arg <- all){
        arg match {
          case s:String         => stmt.setString (pos, arg.toString)
          case s:Int            => stmt.setInt    (pos, arg.asInstanceOf[Int])
          case s:Long           => stmt.setLong   (pos, arg.asInstanceOf[Long])
          case s:Double         => stmt.setDouble (pos, arg.asInstanceOf[Double])
          case s:Boolean        => stmt.setBoolean(pos, arg.asInstanceOf[Boolean])
          case s:DateTime       => "\"" + s.toString() + "\""
        }
        pos += 1
      }
      Unit
    })
  }


  private def getResultValue[T](rs:ResultSet, typ:Type, pos:Int): Option[T] = {
    var res:Option[T] = None
      if(typ == typeOf[String]      ) {
        res = Option(rs.getString(pos)                   .asInstanceOf[T])
      }
      else if(typ == typeOf[Short]  ) {
        res = Option(rs.getShort(pos)                    .asInstanceOf[T])
      }
      else if(typ == typeOf[Int]    ) {
        res = Option(rs.getInt(pos)                      .asInstanceOf[T])
      }
      else if(typ == typeOf[Long]         ) {
        res = Option(rs.getLong(pos)                     .asInstanceOf[T])
      }
      else if(typ == typeOf[Double]         ) {
        res = Option(rs.getDouble(pos)                   .asInstanceOf[T])
      }
      else if(typ == typeOf[Boolean]       ) {
        res = Option(rs.getBoolean(pos)                  .asInstanceOf[T])
      }
      else if(typ == typeOf[DateTime]       ) {
        res = Option(new DateTime(rs.getTimestamp(pos))  .asInstanceOf[T])
      }
      else {
        Some(0)
      }
    res
  }
  /*
  * DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserByEmail`(in email varchar(80))
BEGIN
	select * from `user` where email = email;
END$$
DELIMITER ;
  * */
}