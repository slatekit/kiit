package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.ext.*
import slatekit.common.repeatWith
import slatekit.common.utils.Random
import slatekit.common.utils.StringSearch


class StringTests {


    @Test fun can_create_safe_num() {
        Assert.assertEquals(123456, Random.safeNum("123456"))
        Assert.assertEquals(123456, Random.safeNum("123456", 9))
        Assert.assertEquals(912345, Random.safeNum("012345", 9))
    }


    @Test fun can_repeat_with() {
        Assert.assertTrue("?,?,?" == "?".repeatWith(",", 3))
        Assert.assertTrue("!,!,!" == "!".repeatWith(",", 3))
        Assert.assertTrue("#;#;#" == "#".repeatWith(";", 3))
    }


    @Test fun can_create_id() {
        Assert.assertTrue("abc_123_" == "abc 123 $%^".toId())
        Assert.assertTrue("abc_123_" == "ABC 123 $%^".toId())
        Assert.assertTrue("ABC_123_" == "ABC 123 $%^".toId(lowerCase = false))
        Assert.assertTrue("abc_123_" == " ABC 123 $%^ ".toId())
        Assert.assertTrue("abc_123__-_" == " ABC 123 $%^ &*-()_ ".toId())
    }


    @Test fun can_create_ident() {
        Assert.assertTrue("abc_123_" == "abc 123 $%^".toIdent())
        Assert.assertTrue("abc_123_" == "ABC 123 $%^".toIdent())
        Assert.assertTrue("ABC_123_" == "ABC 123 $%^".toIdent(lowerCase = false))
        Assert.assertTrue("abc_123_" == " ABC 123 $%^ ".toIdent())
        Assert.assertTrue("abc_123__-_" == " ABC 123 $%^ &*-()_ ".toIdent())
    }


    @Test fun can_get_int_after() {
        Assert.assertEquals(1, "action_email_1".intAfter("action_email_"))
    }


    @Test fun can_get_int_after_last() {
        Assert.assertEquals(1, "action_email_1".intAfterLast("_"))
    }


    @Test fun can_convert_to_sentance_case() {
        Assert.assertEquals("K", "k".toSentenceCase())
        Assert.assertEquals("Kay", "kay".toSentenceCase())
    }


    @Test fun can_search_url() {
        val url = "mysite.com/area/page?id=123&name=Something"
        val checks = listOf("http://$url", "https://$url", "www.$url")
        checks.forEach { check ->
            val text = check
            val samples = samples(text)
            val matches = samples.map { StringSearch.url(it) }
            matches.forEach {
                Assert.assertEquals(text, it)
            }
        }
    }


    @Test fun can_search_phone() {
        val checks = listOf("123-456-7890", "123 - 456 - 7890", "1234567890")
        checks.forEach { check ->
            val text = check
            val samples = samples(text)
            val matches = samples.map { StringSearch.phone(it) }
            matches.forEach {
                Assert.assertEquals(text, it)
            }
        }
    }


    @Test fun can_search_email() {
        val checks = listOf("batman@gotham.com", "bat.man@gotham.com", "bat_man@gotham.com", "bat-man@gotham.com")
        checks.forEach { check ->
            val text = check
            val samples = samples(text)
            val matches = samples.map { StringSearch.email(it) }
            matches.forEach {
                Assert.assertEquals(text, it)
            }
        }
    }


    private fun samples(text:String):List<String> {
        return listOf(text, " $text ", "abc $text abc", "abc $text 123")
    }
}