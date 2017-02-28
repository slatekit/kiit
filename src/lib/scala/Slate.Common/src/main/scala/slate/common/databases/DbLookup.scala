/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

package slate.common.databases

/**
  * Lookup for database connections. Supports a default connection
  * 1. default connection: { connectionString }
  * 2. named connection  : "qa1" => {connectionString}
  * 3. grouped shard     : ( group="usa", shard="01" ) => { connectionString }
  */
class DbLookup(private val defaultCon:Option[DbCon] = None,
               private val names:Option[Map[String, DbCon]] = None,
               private val groups:Option[Map[String, Map[String,DbCon]]] = None){

  /**
    * Gets the default database connection
    *
    * @return
    */
  def default : Option[DbCon] = defaultCon


  /**
    * Gets a named database connection
    *
    * @param key
    * @return
    */
  def named(key:String) : Option[DbCon] =  {
    names.fold[Option[DbCon]](None)( nameMap => {
      if ( nameMap.contains(key) ) Some(nameMap(key)) else None
    })
  }


  /**
    * Gets a named database connection
    *
    * @param key
    * @return
    */
  def group(groupName:String, key:String) : Option[DbCon] =
  {
    groups.fold[Option[DbCon]](None)( groupMap => {
      if( groupMap.contains(groupName)){
        val group = groupMap(groupName)
          if(group.contains(key)) Some(group(key)) else None
      }
      else None
    })
  }
}


object DbLookup {

  /**
    * Creates a database lookup with just the default connection
    *
    * @param con
    * @return
    */
  def defaultDb( con:DbCon ): DbLookup = {
    val db = new DbLookup(defaultCon = Option(con))
    db
  }


  /**
    * Creates a database lookup with just named databases
    *
    * @param items
    * @return
    */
  def namedDbs( items:(String,DbCon)*): DbLookup = {
    val named = Map[String,DbCon]( items.map( item => item._1 -> item._2 ): _*)
    val db = new DbLookup(names = Some(named))
    db
  }


  /**
    * Creates a database lookup with groups of named databases ( essentially shards )
    *
    * @param items
    * @return
    */
  def groupedDbs( items:(String,List[(String,DbCon)])*): DbLookup = {

    val groups = Map[String, Map[String,DbCon]](
      items.map( group => group._1 -> named( group._2 ) ): _*
    )
    new DbLookup(groups = Some(groups))
  }


  /**
    * Creates a database lookup with just named databases
    *
    * @param items
    * @return
    */
  private def named( items:List[(String,DbCon)]): Map[String,DbCon] = {
    val named = Map[String,DbCon]( items.map( item => item._1 -> item._2 ): _*)
    named
  }
}
