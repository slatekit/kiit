package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.common.smartvalues.Email


class SmartValueTests {

    @Test fun can_ensure_email() {
        ensureEmail("a"                 , false, false, "" , false, 1)
        ensureEmail("@"                 , false, false, "" , false, 1)
        ensureEmail("@abc.com"          , false, false, "" , false, 8)
        ensureEmail("aabc.com"          , false, false, "" , false, 8)
        ensureEmail("a@a.com"           , true , false, "user@abc.com", false, 7)
        ensureEmail("user1@doe.com"     , true , false, "user@abc.com", false, 13)
        ensureEmail("user-1@doe.com"    , true , false, "user@abc.com", true, 14)
        ensureEmail("user_1@doe.com"    , true , false, "user@abc.com", false, 14)
        ensureEmail("user.1@doe.com"    , true , false, "user@abc.com", false, 14)
        ensureEmail("user$1@doe.com"    , true , false, "user@abc.com", false, 14)
        ensureEmail("user_name1@doe.com", true , false, "user@abc.com", false, 18)
    }


    fun ensureEmail(text:String, valid:Boolean, empty:Boolean, matched:String, dashed:Boolean, length:Int) {
        val p1 = Email.outcome(text)
        Assert.assertEquals(p1.success, valid)
        p1.onSuccess {
            Assert.assertEquals(it.value, text)
            Assert.assertEquals(it.metadata, Email.metadata)
        }
    }
}