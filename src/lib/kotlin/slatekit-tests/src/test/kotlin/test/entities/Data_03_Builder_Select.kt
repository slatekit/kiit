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

package test.entities

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import slatekit.common.data.*
import slatekit.common.data.BuildMode
import kiit.data.sql.Builders
import kiit.data.sql.vendors.MySqlDialect
import kiit.entities.*
import kiit.query.*
import test.setup.User5

class Data_03_Builder_Select {

    private lateinit var entities:Entities
    private val lookup = mapOf(
            "id" to DataType.DTLong,
            "email" to DataType.DTString,
            "active" to DataType.DTBool,
            "level" to DataType.DTInt,
            "category" to DataType.DTString
    )

    private fun builder(): Select = Builders.Select(MySqlDialect,"user", { name -> lookup[name]!! }, { name -> name })

    @Before
    fun setup(){
        entities = EntitySetup.realDb()
        entities.register<Long, User5>(EntityLongId() , vendor = Vendor.MySql) { repo -> UserService(repo) }
    }


    @Test
    fun can_build_empty(){
        val cmd = builder().build(BuildMode.Sql)
        Assert.assertEquals("select * from `user`;", cmd.sql)
        Assert.assertEquals(0, cmd.values.size)
    }


    @Test fun can_build_count() {
        val builder = builder().agg(MySqlDialect.aggr.count, Const.All).where("id", Op.Eq, 2L)
        ensure(builder = builder,
                expectSqlRaw  = "select count(*) from `user` where `id` = 2;",
                expectSqlPrep = "select count(*) from `user` where `id` = ?;",
                expectPairs = listOf(Value("email", DataType.DTLong, 2L))
        )
    }


    @Test fun can_build_filter_1_of_id() {
        val builder = builder().where("id", Op.Eq, 2L)
        ensure(builder = builder,
                expectSqlRaw  = "select * from `user` where `id` = 2;",
                expectSqlPrep = "select * from `user` where `id` = ?;",
                expectPairs = listOf(Value("email", DataType.DTLong, 2L))
        )
    }


    @Test fun can_build_filter_1_of_type_string() {
        val builder = builder().where("email", Op.Eq, "user1@abc.com")
        ensure(builder = builder,
                expectSqlRaw  = "select * from `user` where `email` = 'user1@abc.com';",
                expectSqlPrep = "select * from `user` where `email` = ?;",
                expectPairs = listOf(Value("email", DataType.DTString, "user1@abc.com"))
        )
    }


    @Test fun can_build_filter_1_of_type_int() {
        val builder = builder().where("level", Op.Gte, 2)
        ensure(builder = builder,
                expectSqlRaw  = "select * from `user` where `level` >= 2;",
                expectSqlPrep = "select * from `user` where `level` >= ?;",
                expectPairs = listOf(Value("email", DataType.DTInt, 2))
        )
    }


    @Test fun can_build_filter_1_of_type_bool() {
        val builder = builder().where("active", Op.IsNot, false)
        ensure(builder = builder,
                expectSqlRaw  = "select * from `user` where `active` is not 0;",
                expectSqlPrep = "select * from `user` where `active` is not ?;",
                expectPairs = listOf(Value("email", DataType.DTBool, false))
        )
    }


    @Test fun can_build_filter_multiple_conditions() {
        val builder = builder()
                .where("active", Op.IsNot, false)
                .and("category", Op.Eq, "action")
                .and("level", Op.Gte, 2)

        ensure(builder = builder,
                expectSqlRaw  = "select * from `user` where ((`active` is not 0 and `category` = 'action') and `level` >= 2);",
                expectSqlPrep = "select * from `user` where ((`active` is not ? and `category` = ?) and `level` >= ?);",
                expectPairs = listOf(
                        Value("active"  , DataType.DTBool, false),
                        Value("category", DataType.DTString, "action"),
                        Value("level"   , DataType.DTInt, 2)
                )
        )
    }


    @Test fun can_build_filter_1_with_from_order_limit() {
        val builder = builder()
                .from("users")
                .where("level", Op.Eq, 1)
                .orderBy("id", Order.Dsc)
                .limit(2)

        ensure(builder = builder,
                expectSqlRaw  = "select * from `users` where `level` = 1 order by `id` desc limit 2;",
                expectSqlPrep = "select * from `users` where `level` = ? order by `id` desc limit ?;",
                expectPairs = listOf(
                        Value("level", DataType.DTInt, 1),
                        Value("", DataType.DTInt, 2)
                )
        )
    }


    @Test fun can_build_filter_in() {
        val builder = builder().where("id", Op.In, listOf(1L, 2L, 3L))
        ensure(builder = builder,
                expectSqlRaw  = "select * from `user` where `id` in (1,2,3);",
                expectSqlPrep = "select * from `user` where `id` in (?,?,?);",
                expectPairs = listOf(
                        Value("id", DataType.DTLong, 1L),
                        Value("id", DataType.DTLong, 2L),
                        Value("id", DataType.DTLong, 3L)
                )
        )
    }


    fun ensure(builder:Select, expectSqlRaw:String, expectSqlPrep:String, expectPairs:Values){
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
