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
package test.entities

import org.junit.Assert
import org.junit.Test
import slatekit.common.DateTimes
import slatekit.common.data.Encoding
import slatekit.query.Const
import slatekit.query.QueryEncoder
import slatekit.query.where
import test.setup.Book
import test.setup.StatusEnum


/**
 * Created by kishorereddy on 6/3/17.
 */
class QueryTests {


    @Test fun can_convert_string_empty() {
        Assert.assertTrue( Encoding.convertVal("") == "''")
    }


    @Test fun can_convert_string_non_empty() {
        Assert.assertTrue( Encoding.convertVal("slate kit") == "'slate kit'")
    }


    @Test fun can_convert_string_with_quote() {
        Assert.assertTrue( Encoding.convertVal("slate kit's dope") == "'slate kit''s dope'")
    }


    @Test fun can_convert_boolean_true() {
        Assert.assertTrue( Encoding.convertVal(true) == "1")
    }


    @Test fun can_convert_boolean_false() {
        Assert.assertTrue( Encoding.convertVal(false) == "0")
    }


    @Test fun can_convert_enum() {
        Assert.assertTrue( Encoding.convertVal(StatusEnum.Active) == "1")
    }


    @Test fun can_convert_boolean_datetime() {
        Assert.assertTrue( Encoding.convertVal(DateTimes.of(2016, 10, 16)) == "'2016-10-16 00:00:00'")
    }


    @Test fun can_convert_field() {
        Assert.assertTrue( Encoding.ensureField("a(1)2*3&4b") == "a1234b")
    }
}