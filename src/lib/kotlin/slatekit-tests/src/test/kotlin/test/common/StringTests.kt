package test.common

import org.junit.Test
import slatekit.common.repeatWith
import slatekit.common.toId
import slatekit.common.toIdent


class StringTests {


    @Test fun can_repeat_with() {
        assert("?,?,?" == "?".repeatWith(",", 3))
        assert("!,!,!" == "!".repeatWith(",", 3))
        assert("#;#;#" == "#".repeatWith(";", 3))
    }


    @Test fun can_create_id() {
        assert("abc_123_$%^" == "abc 123 $%^".toId())
        assert("abc_123_$%^" == "ABC 123 $%^".toId())
        assert("ABC_123_$%^" == "ABC 123 $%^".toId(lowerCase = false))
        assert("abc_123_$%^" == " ABC 123 $%^ ".toId())
        assert("abc_123_$%^_&*-()_" == " ABC 123 $%^ &*-()_ ".toId())
    }


    @Test fun can_create_ident() {
        assert("abc_123_" == "abc 123 $%^".toIdent())
        assert("abc_123_" == "ABC 123 $%^".toIdent())
        assert("ABC_123_" == "ABC 123 $%^".toIdent(lowerCase = false))
        assert("abc_123_" == " ABC 123 $%^ ".toIdent())
        assert("abc_123__-_" == " ABC 123 $%^ &*-()_ ".toIdent())
    }
}