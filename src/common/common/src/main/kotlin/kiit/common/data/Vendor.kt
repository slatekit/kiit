/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */
package kiit.common.data

sealed class Vendor(val name: String, val driver: String) {
  object MySql    : Vendor("mysql", "com.mysql.jdbc.Driver")
  object H2       : Vendor("h2", "org.h2.Driver")
  object SqLite   : Vendor("sqlite", "org.sqlite.JDBC")
//  object PGres    : Vendor("pgres", "org.postgresql.Driver")
//  object SqServer : Vendor("sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver")
  object Memory   : Vendor("memory", "com.kiit.entities.repository-in-memory")
}
