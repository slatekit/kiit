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
package test

import org.junit.Test
import slatekit.common.DateTime
import slatekit.common.query.Query
import slatekit.common.query.QueryEncoder


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
        assert( QueryEncoder.convertVal(DateTime(2016, 10, 16)) == "'2016-10-16 12:00:00'")
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
        assert(  Query().where("date", "=", DateTime(2016, 10, 16)).toFilter() == "date = '2016-10-16 12:00:00'")
    }


    @Test fun can_build_where_with_2_fields() {
        assert(  Query().where("api", "=", "slate kit's").and("version", "=", 2).toFilter()
                == "api = 'slate kit''s' and version = 2")
    }
}