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

sealed class Vendor(val name: String, val driver: String) {
  object MySql  : Vendor("mysql", "com.mysql.jdbc.Driver")
  object PGres  : Vendor("pgres", "org.postgresql.Driver")
  object SqLite : Vendor("sqlite", "org.sqlite.JDBC")
  object Memory : Vendor("memory", "com.slatekit.entities.repository-in-memory")
}
