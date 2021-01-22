/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.data

import slatekit.common.conf.Conf

/**
  * Lookup for database connections. Supports a default connection
  * 1. default connection: { connectionString }
  * 2. named connection  : "qa1" -> {connectionString}
  * 3. grouped shard     : ( group="usa", shard="01" ) -> { connectionString }
  */
class Connections(
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
    fun of(con: DbCon): Connections {
      val db = Connections(defaultCon = con)
      return db
    }


    @JvmStatic
    fun from(cls:Class<*>, conf: Conf):Connections {
      return of(conf.dbCon("db"))
    }


    /**
     * Creates a database lookup with just named databases
     *
     * @param items
     * @return
     */
    @JvmStatic
    fun named(items: List<Pair<String, DbCon>>): Connections {
      val named = items.map { item -> item.first to item.second }.toMap()
      val db = Connections(names = named)
      return db
    }
  }
}
