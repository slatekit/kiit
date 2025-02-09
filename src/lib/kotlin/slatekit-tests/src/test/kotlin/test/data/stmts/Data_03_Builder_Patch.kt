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

package test.data.stmts

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kiit.common.data.*
import kiit.common.data.BuildMode
import kiit.data.sql.Builders
import kiit.data.sql.vendors.MySqlDialect
import kiit.entities.*
import kiit.query.*

class Data_03_Builder_Patch {

    private lateinit var entities:Entities
    private val lookup = mapOf(
            "id" to DataType.DTLong,
            "userId" to DataType.DTLong,
            "email" to DataType.DTString,
            "active" to DataType.DTBool,
            "level" to DataType.DTInt,
            "category" to DataType.DTString
    )
    private fun builder(): Update = Builders.Patch(MySqlDialect,"unit_tests", "user", { name -> lookup[name]!! }, { name -> name })


    @Before
    fun setup(){
        //entities = EntitySetup.realDb()
        //entities.register<Long, User5>(EntityLongId() , vendor = Vendor.MySql) { repo -> UserService(repo) }
    }


    @Test
    fun can_build_empty(){
        val cmd = builder().build(BuildMode.Sql)
        Assert.assertEquals("update `user` set ;", cmd.sql)
        Assert.assertEquals(0, cmd.values.size)
    }


    @Test fun can_build_filter_1_of_id() {
        val builder = builder().set("id", 1L).where("id", Op.Eq, 2L)
        ensure(builder = builder,
                expectSqlRaw  = "update `user` set `id` = 1 where `id` = 2;",
                expectSqlPrep = "update `user` set `id` = ? where `id` = ?;",
                expectPairs = listOf(
                        Value("id", DataType.DTLong, 1L),
                        Value("id", DataType.DTLong, 2L)
                )
        )
    }


    @Test fun can_build_filter_retaining_name() {
        val builder = builder().set("userId", 1L).where("userId", Op.Eq, 2L)
        ensure(builder = builder,
                expectSqlRaw  = "update `user` set `userId` = 1 where `userId` = 2;",
                expectSqlPrep = "update `user` set `userId` = ? where `userId` = ?;",
                expectPairs = listOf(
                        Value("userId", DataType.DTLong, 1L),
                        Value("userId", DataType.DTLong, 2L)
                )
        )
    }


    @Test fun can_build_filter_1_of_type_string() {
        val builder = builder().set("email", "user2@abc.com").where("email", Op.Eq, "user1@abc.com")
        ensure(builder = builder,
                expectSqlRaw  = "update `user` set `email` = 'user2@abc.com' where `email` = 'user1@abc.com';",
                expectSqlPrep = "update `user` set `email` = ? where `email` = ?;",
                expectPairs = listOf(
                        Value("email", DataType.DTString, "user2@abc.com"),
                        Value("email", DataType.DTString, "user1@abc.com")
                )
        )
    }


    @Test fun can_build_filter_1_of_type_int() {
        val builder = builder().set("level", 1).where("level", Op.Gte, 2)
        ensure(builder = builder,
                expectSqlRaw  = "update `user` set `level` = 1 where `level` >= 2;",
                expectSqlPrep = "update `user` set `level` = ? where `level` >= ?;",
                expectPairs = listOf(
                        Value("level", DataType.DTInt, 1),
                        Value("level", DataType.DTInt, 2)
                )
        )
    }


    @Test fun can_build_filter_1_of_type_bool() {
        val builder = builder().set("active", true).where("active", Op.IsNot, false)
        ensure(builder = builder,
                expectSqlRaw  = "update `user` set `active` = 1 where `active` is not 0;",
                expectSqlPrep = "update `user` set `active` = ? where `active` is not ?;",
                expectPairs = listOf(
                        Value("active", DataType.DTBool, true),
                        Value("active", DataType.DTBool, false)
                )
        )
    }


    @Test fun can_build_filter_multiple_conditions() {
        val builder = builder()
                .set("level", 3)
                .where("active", Op.IsNot, false)
                .and("category", Op.Eq, "action")
                .and("level", Op.Gte, 2)

        ensure(builder = builder,
                expectSqlRaw  = "update `user` set `level` = 3 where ((`active` is not 0 and `category` = 'action') and `level` >= 2);",
                expectSqlPrep = "update `user` set `level` = ? where ((`active` is not ? and `category` = ?) and `level` >= ?);",
                expectPairs = listOf(
                        Value("level"   , DataType.DTInt, 3),
                        Value("active"  , DataType.DTBool, false),
                        Value("category", DataType.DTString, "action"),
                        Value("level"   , DataType.DTInt, 2)
                )
        )
    }


    @Test fun can_build_filter_1_with_from_order_limit() {
        val builder = builder()
                .set("level", 2)
                .from("users")
                .where("level", Op.Eq, 1)
                .orderBy("id", Order.Dsc)
                .limit(2)

        ensure(builder = builder,
                expectSqlRaw  = "update `users` set `level` = 2 where `level` = 1 order by `id` desc limit 2;",
                expectSqlPrep = "update `users` set `level` = ? where `level` = ? order by `id` desc limit ?;",
                expectPairs = listOf(
                        Value("level", DataType.DTInt, 2),
                        Value("level", DataType.DTInt, 1),
                        Value("", DataType.DTInt, 2)
                )
        )
    }


    @Test fun can_build_filter_in() {
        val builder = builder().set("level", 2).where("id", Op.In, listOf(1L, 2L, 3L))
        ensure(builder = builder,
                expectSqlRaw  = "update `user` set `level` = 2 where `id` in (1,2,3);",
                expectSqlPrep = "update `user` set `level` = ? where `id` in (?,?,?);",
                expectPairs = listOf(
                        Value("level", DataType.DTInt, 2),
                        Value("id", DataType.DTLong, 1L),
                        Value("id", DataType.DTLong, 2L),
                        Value("id", DataType.DTLong, 3L)
                )
        )
    }


    fun ensure(builder:Update, expectSqlRaw:String, expectSqlPrep:String, expectPairs:Values){
        val cmd1 = builder.build(BuildMode.Sql)
        val cmd2 = builder.build(BuildMode.Prep)
        Assert.assertEquals(expectSqlRaw, cmd1.sql)
        Assert.assertEquals(expectSqlPrep, cmd2.sql)
        expectPairs.forEachIndexed { ndx, pair ->
            val actual = cmd2.pairs[ndx]
            Assert.assertEquals(pair.value, actual.value)
            Assert.assertEquals(pair.tpe, actual.tpe)
        }
    }
}
