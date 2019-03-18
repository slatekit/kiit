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
package slatekit.common.db

sealed class DbType(val name: String, val driver: String) {
  object DbTypeMySql  : DbType("mysql", "com.mysql.jdbc.Driver")
  object DbTypePGres  : DbType("pgres", "org.postgresql.Driver")
  object DbTypeSqLite : DbType("sqlite", "org.sqlite.JDBC")
  object DbTypeMemory : DbType("memory", "com.slatekit.entities.repository-in-memory")
}
