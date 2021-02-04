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
import slatekit.data.sql.Builders
import slatekit.entities.*
import slatekit.query.Op
import slatekit.query.Delete
import test.setup.User5

class Data_03_Builder_Delete {

    private lateinit var entities:Entities

    private fun builder(): Delete = Builders.Delete("user")

    @Before
    fun setup(){
        entities = EntitySetup.realDb()
        entities.register<Long, User5>(EntityLongId() , vendor = Vendor.MySql) { repo -> UserService(repo) }
    }


    @Test
    fun can_build_empty(){
        val cmd = builder().build(BuildMode.Sql)
        Assert.assertEquals("", cmd.sql)
        Assert.assertEquals(0, cmd.values.size)
    }


    @Test fun can_build_filter_1_of_type_string() {
        val builder = builder().where("email", Op.Eq, "user1@abc.com")
        ensure(builder = builder,
                expectSqlRaw  = "delete from `user` where `email` = 'user1@abc.com';",
                expectSqlPrep = "delete from `user` where `email` = ?;",
                expectPairs = listOf(Value("email", DataType.DTString, "user1@abc.com"))
        )
    }


    @Test fun can_build_filter_1_of_type_int() {
        val builder = builder().where("level", Op.Gte, 2)
        ensure(builder = builder,
                expectSqlRaw  = "delete from `user` where `level` > 2;",
                expectSqlPrep = "delete from `user` where `level` > ?;",
                expectPairs = listOf(Value("email", DataType.DTInt, 2))
        )
    }


    @Test fun can_build_filter_1_of_type_bool() {
        val builder = builder().where("active", Op.Neq, false)
        ensure(builder = builder,
                expectSqlRaw  = "delete from `user` where `email` is not 0;",
                expectSqlPrep = "delete from `user` where `email` is not ?;",
                expectPairs = listOf(Value("email", DataType.DTBool, false))
        )
    }


    fun ensure(builder:Delete, expectSqlRaw:String, expectSqlPrep:String, expectPairs:Values){
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
