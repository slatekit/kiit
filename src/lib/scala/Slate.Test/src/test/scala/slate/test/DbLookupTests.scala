
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

package slate.test

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}
import slate.common.databases._
import slate.common.databases.DbLookup._


class DbLookupTests  extends FunSuite with BeforeAndAfter with BeforeAndAfterAll {

  private def buildDefaultConnection(name:String = "db1"):DbConString = {
    new DbConString("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/" + name,
      "root", "abcdefghi")
  }


  test("can create db lookup with no connections") {
    val dbs = new DbLookup()
    assert( dbs.default.isEmpty )
    assert( dbs.named("").isEmpty )
    assert( dbs.group("", "").isEmpty )
  }


  test("can create db lookup with default db") {
    val dbs = defaultDb(buildDefaultConnection())
    ensureDb( dbs, buildDefaultConnection())
    assert( dbs.named("").isEmpty )
    assert( dbs.group("", "").isEmpty )
  }


  test("can create db lookup with named connections") {
    val dbs = namedDbs(
      ("users", buildDefaultConnection("u1")),
      ("files", buildDefaultConnection("f1"))
    )

    ensureNamedDb( dbs, "users", buildDefaultConnection("u1"))
    ensureNamedDb( dbs, "files", buildDefaultConnection("f1"))
    assert( dbs.group("", "").isEmpty )
  }


  test("can create db lookup with grouped connections") {
    val dbs = groupedDbs(
      (
        "us_east", List[(String,DbConString)](
          ("e01", buildDefaultConnection("e01")),
          ("e02", buildDefaultConnection("e02"))
        )
      ),
      (
        "us_west", List[(String,DbConString)](
        ("w01", buildDefaultConnection("w01")),
        ("w02", buildDefaultConnection("w02"))
      ))
    )

    ensureGroupedDb( dbs, "us_east", "e01", buildDefaultConnection("e01"))
    ensureGroupedDb( dbs, "us_east", "e02", buildDefaultConnection("e02"))
    ensureGroupedDb( dbs, "us_west", "w01", buildDefaultConnection("w01"))
    ensureGroupedDb( dbs, "us_west", "w02", buildDefaultConnection("w02"))
  }


  def ensureDb(dbs:DbLookup, con:DbConString):Unit = {
    assert( dbs.default.isDefined )
    ensureDb(dbs.default.get, con)
  }


  def ensureDb(expected:DbConString, actual:DbConString):Unit = {
    assert( expected.driver   == actual.driver )
    assert( expected.password == actual.password )
    assert( expected.url      == actual.url )
    assert( expected.user     == actual.user )
  }


  def ensureNamedDb(dbs:DbLookup, name:String, con:DbConString):Unit = {
    ensureDb( dbs.named(name).get, con)
  }


  def ensureGroupedDb(dbs:DbLookup, group:String, name:String, con:DbConString):Unit = {
    ensureDb( dbs.group(group, name).get, con)
  }
}
