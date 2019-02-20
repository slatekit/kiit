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
package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.DateTime
import slatekit.query.Query
import slatekit.query.QueryEncoder
import slatekit.meta.where
import test.setup.Book
import test.setup.StatusEnum


/**
 * Created by kishorereddy on 6/3/17.
 */
class QueryTests {


    @Test fun can_convert_string_empty() {
        Assert.assertTrue( QueryEncoder.convertVal("") == "''")
    }


    @Test fun can_convert_string_non_empty() {
        Assert.assertTrue( QueryEncoder.convertVal("slate kit") == "'slate kit'")
    }


    @Test fun can_convert_string_with_quote() {
        Assert.assertTrue( QueryEncoder.convertVal("slate kit's dope") == "'slate kit''s dope'")
    }


    @Test fun can_convert_boolean_true() {
        Assert.assertTrue( QueryEncoder.convertVal(true) == "1")
    }


    @Test fun can_convert_boolean_false() {
        Assert.assertTrue( QueryEncoder.convertVal(false) == "0")
    }


    @Test fun can_convert_enum() {
        Assert.assertTrue( QueryEncoder.convertVal(StatusEnum.Active) == "1")
    }


    @Test fun can_convert_boolean_datetime() {
        Assert.assertTrue( QueryEncoder.convertVal(DateTime.of(2016, 10, 16)) == "'2016-10-16 00:00:00'")
    }


    @Test fun can_convert_field() {
        Assert.assertTrue( QueryEncoder.ensureField("a(1)2*3&4b") == "a1234b")
    }

    @Test fun can_build_empty() {
        Assert.assertTrue(  Query().toFilter() == "")
    }


    @Test fun can_build_filter_1() {
        Assert.assertTrue(  Query().where("api", "=", "slate kit").toFilter() == "api = 'slate kit'")
    }


    @Test fun can_build_where_with_1_field_of_type_bool() {
        Assert.assertTrue(  Query().where("isactive", "=", true).toFilter() == "isactive = 1")
    }


    @Test fun can_build_where_with_1_field_of_type_int() {
        Assert.assertTrue(  Query().where("status", "=", 3).toFilter() == "status = 3")
    }


    @Test fun can_build_where_with_1_field_of_type_datetime() {
        Assert.assertTrue(  Query().where("date", "=", DateTime.of(2016, 10, 16)).toFilter() == "date = '2016-10-16 00:00:00'")
    }


    @Test fun can_build_where_with_2_fields() {
        Assert.assertTrue(  Query().where("api", "=", "slate kit's").and("version", "=", 2).toFilter()
                == "api = 'slate kit''s' and version = 2")
    }


    @Test fun can_build_where_typed() {
        Assert.assertTrue(  Query().where(Book::rating, ">", 3.0).toFilter()
                == "rating > 3.0")
    }


    @Test fun can_build_condition_is_null() {
        val filter = Query().where(Book::rating, "=", Query.Null).toFilter()
        Assert.assertTrue(  filter == "rating is null")
    }


    @Test fun can_build_condition_is_null_literal() {
        val filter = Query().where(Book::rating, "=", null).toFilter()
        Assert.assertTrue(  filter == "rating is null")
    }


    @Test fun can_build_condition_is_not_null() {
        val filter = Query().where(Book::rating, "!=", Query.Null).toFilter()
        Assert.assertTrue(  filter == "rating is not null")
    }


    @Test fun can_build_condition_in() {
        val filter = Query().where(Book::id, "in", listOf(1,3,5)).toFilter()
        Assert.assertTrue(  filter == "id in (1,3,5)")
    }


    @Test fun can_build_orderby_asc() {
        val filter = Query().orderBy("id", Query.Asc).toFilter()
        Assert.assertTrue(  filter == " order by id asc")
    }


    @Test fun can_build_orderby_desc() {
        val filter = Query().orderBy("id", Query.Desc).toFilter()
        Assert.assertTrue(  filter == " order by id desc")
    }


    @Test fun can_build_join() {
        val filter = Query().join("users", "users.id", "movies.created_by").toFilter()
        Assert.assertTrue(  filter == "  join users on users.id = movies.created_by ")
    }
}