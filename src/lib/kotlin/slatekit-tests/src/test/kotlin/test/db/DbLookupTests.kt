/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Kotlin utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */
package test.db

import org.junit.Assert
import org.junit.Test
import slatekit.common.db.DbCon
import slatekit.common.db.DbConString
import slatekit.common.db.DbLookup
import slatekit.common.db.DbLookup.Companion.defaultDb
import slatekit.common.db.DbLookup.Companion.namedDbs

/**
 * Created by kishorereddy on 6/4/17.
 */
class DbLookupTests {

    fun buildDefaultConnection(name: String = "db1"): DbConString =
        DbConString(
            "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/" + name,
            "root", "abcdefghi"
        )


    @Test
    fun can_create_dblookup_with_no_connections() {
        val dbs = DbLookup()
        Assert.assertTrue(dbs.default() == null)
        Assert.assertTrue(dbs.named("") == null)
        Assert.assertTrue(dbs.group("", "") == null)
    }


    @Test
    fun can_create_dblookup_with_default_db() {
        val dbs = defaultDb(buildDefaultConnection())
        ensureDb(dbs, buildDefaultConnection())
        Assert.assertTrue(dbs.named("") == null)
        Assert.assertTrue(dbs.group("", "") == null)
    }


    @Test
    fun can_create_dblookup_with_named_connections() {
        val dbs = namedDbs(
            listOf(
                Pair("users", buildDefaultConnection("u1")),
                Pair("files", buildDefaultConnection("f1"))
            )
        )

        ensureNamedDb(dbs, "users", buildDefaultConnection("u1"))
        ensureNamedDb(dbs, "files", buildDefaultConnection("f1"))
        Assert.assertTrue(dbs.group("", "") === null)
    }


//    @Test fun can_create_dblookup_with_grouped_connections() {
//        val dbs = groupedDbs(mapOf(
//                    "us_east" to listOf(
//                        Pair("e01", buildDefaultConnection("e01")),
//                        Pair("e02", buildDefaultConnection("e02"))
//                    ),
//
//                    "us_west" to listOf(
//                        Pair("w01", buildDefaultConnection("w01")),
//                        Pair("w02", buildDefaultConnection("w02"))
//                    )
//        ))
//
//        ensureGroupedDb( dbs, "us_east", "e01", buildDefaultConnection("e01"))
//        ensureGroupedDb( dbs, "us_east", "e02", buildDefaultConnection("e02"))
//        ensureGroupedDb( dbs, "us_west", "w01", buildDefaultConnection("w01"))
//        ensureGroupedDb( dbs, "us_west", "w02", buildDefaultConnection("w02"))
//    }


    fun ensureDb(dbs: DbLookup, con: DbConString): Unit {
        Assert.assertTrue(dbs.default() != null)
        ensureDb(dbs.default()!!, con)
    }


    fun ensureDb(expected: DbCon, actual: DbConString): Unit {
        Assert.assertTrue(expected.driver == actual.driver)
        Assert.assertTrue(expected.password == actual.password)
        Assert.assertTrue(expected.url == actual.url)
        Assert.assertTrue(expected.user == actual.user)
    }


    fun ensureNamedDb(dbs: DbLookup, name: String, con: DbConString): Unit {
        ensureDb(dbs.named(name)!!, con)
    }


    fun ensureGroupedDb(dbs: DbLookup, group: String, name: String, con: DbConString): Unit {
        ensureDb(dbs.group(group, name)!!, con)
    }
}
