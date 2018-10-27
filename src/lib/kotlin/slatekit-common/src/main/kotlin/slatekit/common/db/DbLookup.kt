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

package slatekit.common.db

/**
  * Lookup for database connections. Supports a default connection
  * 1. default connection: { connectionString }
  * 2. named connection  : "qa1" -> {connectionString}
  * 3. grouped shard     : ( group="usa", shard="01" ) -> { connectionString }
  */
class DbLookup(
    private val defaultCon: DbCon? = null,
    private val names: Map<String, DbCon>? = null,
    private val groups: Map<String, Map<String, DbCon>>? = null
) {

  /**
    * Gets the default database connection
    *
    * @return
    */
  fun default(): DbCon? = defaultCon

  /**
    * Gets a named database connection
    *
    * @param key
    * @return
    */
  fun named(key: String): DbCon? = names?.let { map -> map[key] }

  /**
    * Gets a named database connection
    *
    * @param key
    * @return
    */
  fun group(groupName: String, key: String): DbCon? =
    groups?.let { gs ->
      gs[groupName]?.let { g ->
        g[key]
      }
    }

  companion object {

    /**
     * Creates a database lookup with just the default connection
     *
     * @param con
     * @return
     */
    @JvmStatic
    fun defaultDb(con: DbCon): DbLookup {
      val db = DbLookup(defaultCon = con)
      return db
    }

    /**
     * Creates a database lookup with just named databases
     *
     * @param items
     * @return
     */
    @JvmStatic
    fun namedDbs(items: List<Pair<String, DbCon>>): DbLookup {
      val named = items.map { item -> item.first to item.second }.toMap()
      val db = DbLookup(names = named)
      return db
    }

    /**
     * Creates a database lookup with just named databases
     *
     * @param items
     * @return
     */
    private fun named(items: List<Pair<String, DbCon>>): Map<String, DbCon> {
      val named = items.map { item -> item.first to item.second }.toMap()
      return named
    }
  }
}
