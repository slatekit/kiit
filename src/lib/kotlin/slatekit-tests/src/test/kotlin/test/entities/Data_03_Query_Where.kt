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
import slatekit.data.syntax.Builders
import slatekit.entities.*
import slatekit.query.Op
import slatekit.query.Where
import test.setup.User5

class Data_03_Query_Where {

    private lateinit var entities:Entities

    private fun where(): Where = Builders.Where()

    @Before
    fun setup(){
        entities = EntitySetup.realDb()
        entities.register<Long, User5>(EntityLongId() , vendor = Vendor.MySql) { repo -> UserService(repo) }
    }


    @Test
    fun can_build_empty(){
        val cmd = where().build()
        Assert.assertEquals("", cmd.sql)
        Assert.assertEquals(0, cmd.values.size)
    }




    @Test fun can_build_filter_1() {
        val cmd = where().where("api", Op.Eq, "slate kit").toFilter() == "api = 'slate kit'"
        Assert.assertTrue("api = 'slate kit'",  )
    }


//    @Test fun can_build_where_with_1_field_of_type_bool() {
//        Assert.assertTrue(  Query().where("isactive", "=", true).toFilter() == "isactive = 1")
//    }
//
//
//    @Test fun can_build_where_with_1_field_of_type_int() {
//        Assert.assertTrue(  Query().where("status", "=", 3).toFilter() == "status = 3")
//    }
//
//
//    @Test fun can_build_where_with_1_field_of_type_datetime() {
//        Assert.assertTrue(  Query().where("date", "=", DateTimes.of(2016, 10, 16)).toFilter() == "date = '2016-10-16 00:00:00'")
//    }
}
