/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slate.common.databases


import scala.collection.mutable.Map
import slate.common.{Ensure, ListMap}

/**
  * Lookup for database connections. Supports a default connection
  * 1. default connection: { connectionString }
  * 2. named connection  : "qa1" => {connectionString}
  * 3. grouped shard     : ( group="usa", shard="01" ) => { connectionString }
  */
object DbLookup {

  private var _defaultCon:Option[DbConString] = null
  private val _named = Map[String, DbConString]()
  private val _groups = Map[String, ListMap[String,DbConString]]()


  /**
    * sets this connection as the default connection string
    * @param driver   : database driver     ( e.g. com.mysql.jdbc.Driver )
    * @param url      : url of the database ( e.g. jdbc:mysql://localhost/World )
    * @param user     : login user
    * @param password : login password
    */
  def setDefault(driver:String, url:String, user:String, password:String): Unit =
  {
    _defaultCon = Some(new DbConString(driver, url, user, password))
  }


  /**
   * sets this connection as the default connection string
   * @param con: The connection string e..g DbConfig.loadFromUserFolder('.slate', 'db_default.txt')
   */
  def setDefault(con:Option[DbConString]): Unit =
  {
    _defaultCon = con
  }


  /**
    * stores the connection and links it to the key supplied
    * @param key      : lookup key to link this connection ( used for multiple connection strings )
    * @param driver   : database driver     ( e.g. com.mysql.jdbc.Driver )
    * @param url      : url of the database ( e.g. jdbc:mysql://localhost/World )
    * @param user     : login user
    * @param password : login password
    */
  def set(key:String, driver:String, url:String, user:String, password:String): Unit =
  {
    _named(key) = new DbConString(driver, url, user, password)
  }


  /**
   * stores the connection and links it to the key supplied
   * @param key : lookup key to link this connection ( used for multiple connection strings )
   * @param con : The connection string e..g DbConfig.loadFromUserFolder('.slate', 'db_default.txt')
   */
  def set(key:String, con:DbConString): Unit =
  {
    _named(key) = con
  }


  /**
    * stores the connection and links it to the group/shard supplied
    * @param groupName: the name of the group holding multiple shards
    * @param shardName: the name of the shard
    * @param driver   : database driver     ( e.g. com.mysql.jdbc.Driver )
    * @param url      : url of the database ( e.g. jdbc:mysql://localhost/World )
    * @param user     : login user
    * @param password : login password
    */
  def shard(groupName:String, shardName:String, driver:String, url:String,
            user:String, password:String): Unit =
  {
    val shards = getOrCreateArea(groupName)
    shards(shardName) = new DbConString(driver, url, user, password)
  }


  /**
   * stores the connection and links it to the group/shard supplied
   * @param groupName: the name of the group holding multiple shards
   * @param shardName: the name of the shard
   * @param con      : The connection string e..g DbConfig.loadFromUserFolder('.slate',
    *                 'db_default.txt')
   */
  def shard(groupName:String, shardName:String, con:DbConString): Unit =
  {
    val shards = getOrCreateArea(groupName)
    shards(shardName) = con
  }


  /**
    * Gets the default database connection
    * @return
    */
  def getDefault : DbConString = {
    _defaultCon.getOrElse(DbConString.empty)
  }


  /**
    * Gets a named database connection
    * @param key
    * @return
    */
  def getByKey(key:String) : DbConString =
  {
    if(!_named.contains(key))
      throw new IllegalArgumentException(s"database connection not found: $key")

    _named(key)
  }


  /**
    * Gets a named database connection
    * @param key
    * @return
    */
  def getShard(groupName:String, key:String) : DbConString =
  {
    Ensure.isTrue(_groups.contains(groupName), s"Shard group not found: $groupName")
    Ensure.isTrue(_groups(groupName).contains(key), s"Shard group not found: $groupName")

    _groups(groupName)(key)
  }


  private def getOrCreateArea(groupName:String):ListMap[String,DbConString] =
  {
    if(_groups.contains(groupName))
      return _groups(groupName)
    val shards = new ListMap[String,DbConString]()
    _groups(groupName) = shards
    shards
  }
}
