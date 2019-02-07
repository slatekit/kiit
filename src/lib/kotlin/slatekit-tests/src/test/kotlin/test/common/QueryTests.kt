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

import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.query.Query
import slatekit.common.query.QueryEncoder
import slatekit.meta.where
import test.setup.Book


/**
 * Created by kishorereddy on 6/3/17.
 */
class QueryTests {


    @Test fun can_convert_string_empty() {
        assert( QueryEncoder.convertVal("") == "''")
    }


    @Test fun can_convert_string_non_empty() {
        assert( QueryEncoder.convertVal("slate kit") == "'slate kit'")
    }


    @Test fun can_convert_string_with_quote() {
        assert( QueryEncoder.convertVal("slate kit's dope") == "'slate kit''s dope'")
    }


    @Test fun can_convert_boolean_true() {
        assert( QueryEncoder.convertVal(true) == "1")
    }


    @Test fun can_convert_boolean_false() {
        assert( QueryEncoder.convertVal(false) == "0")
    }


    @Test fun can_convert_boolean_datetime() {
        assert( QueryEncoder.convertVal(DateTime.of(2016, 10, 16)) == "'2016-10-16 00:00:00'")
    }


    @Test fun can_convert_field() {
        assert( QueryEncoder.ensureField("a(1)2*3&4b") == "a1234b")
    }

    @Test fun can_build_empty() {
        assert(  Query().toFilter() == "")
    }


    @Test fun can_build_filter_1() {
        assert(  Query().where("api", "=", "slate kit").toFilter() == "api = 'slate kit'")
    }


    @Test fun can_build_where_with_1_field_of_type_bool() {
        assert(  Query().where("isactive", "=", true).toFilter() == "isactive = 1")
    }


    @Test fun can_build_where_with_1_field_of_type_int() {
        assert(  Query().where("status", "=", 3).toFilter() == "status = 3")
    }


    @Test fun can_build_where_with_1_field_of_type_datetime() {
        assert(  Query().where("date", "=", DateTime.of(2016, 10, 16)).toFilter() == "date = '2016-10-16 00:00:00'")
    }


    @Test fun can_build_where_with_2_fields() {
        assert(  Query().where("api", "=", "slate kit's").and("version", "=", 2).toFilter()
                == "api = 'slate kit''s' and version = 2")
    }


    @Test fun can_build_where_typed() {
        assert(  Query().where(Book::rating, ">", 3.0).toFilter()
                == "rating > 3.0")
    }


    @Test fun can_build_condition_is_null() {
        val filter = Query().where(Book::rating, "=", Query.Null).toFilter()
        assert(  filter == "rating is null")
    }


    @Test fun can_build_condition_is_null_literal() {
        val filter = Query().where(Book::rating, "=", null).toFilter()
        assert(  filter == "rating is null")
    }


    @Test fun can_build_condition_is_not_null() {
        val filter = Query().where(Book::rating, "!=", Query.Null).toFilter()
        assert(  filter == "rating is not null")
    }


    @Test fun can_build_condition_in() {
        val filter = Query().where(Book::id, "in", listOf(1,3,5)).toFilter()
        assert(  filter == "id in (1,3,5)")
    }


    @Test fun can_build_orderby_asc() {
        val filter = Query().orderBy("id", Query.Asc).toFilter()
        assert(  filter == " order by id asc")
    }


    @Test fun can_build_orderby_desc() {
        val filter = Query().orderBy("id", Query.Desc).toFilter()
        assert(  filter == " order by id desc")
    }


    @Test fun can_build_join() {
        val filter = Query().join("users", "users.id", "movies.created_by").toFilter()
        assert(  filter == "  join users on users.id = movies.created_by ")
    }
}