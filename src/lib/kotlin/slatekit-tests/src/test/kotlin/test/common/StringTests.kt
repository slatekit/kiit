package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.repeatWith
import slatekit.common.toId
import slatekit.common.toIdent


class StringTests {


    @Test fun can_repeat_with() {
        Assert.assertTrue("?,?,?" == "?".repeatWith(",", 3))
        Assert.assertTrue("!,!,!" == "!".repeatWith(",", 3))
        Assert.assertTrue("#;#;#" == "#".repeatWith(";", 3))
    }


    @Test fun can_create_id() {
        Assert.assertTrue("abc_123_$%^" == "abc 123 $%^".toId())
        Assert.assertTrue("abc_123_$%^" == "ABC 123 $%^".toId())
        Assert.assertTrue("ABC_123_$%^" == "ABC 123 $%^".toId(lowerCase = false))
        Assert.assertTrue("abc_123_$%^" == " ABC 123 $%^ ".toId())
        Assert.assertTrue("abc_123_$%^_&*-()_" == " ABC 123 $%^ &*-()_ ".toId())
    }


    @Test fun can_create_ident() {
        Assert.assertTrue("abc_123_" == "abc 123 $%^".toIdent())
        Assert.assertTrue("abc_123_" == "ABC 123 $%^".toIdent())
        Assert.assertTrue("ABC_123_" == "ABC 123 $%^".toIdent(lowerCase = false))
        Assert.assertTrue("abc_123_" == " ABC 123 $%^ ".toIdent())
        Assert.assertTrue("abc_123__-_" == " ABC 123 $%^ &*-()_ ".toIdent())
    }
}