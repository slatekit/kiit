package test

import org.junit.Test
import slatekit.common.SmartString
import slatekit.common.types.*


class SmartStringTests {


    @Test fun can_ensure_phone() {

        ensurePhone("11122233"       , false, false, "", false, 8)
        ensurePhone("111122233333"   , false, false, "", false, 12)
        ensurePhone("111-222-3333-"  , false, false, "", true, 13)
        ensurePhone("1-111-222-33335", false, false, "", true, 15)
        ensurePhone("1112223333"     , true , false, "1234567890", false, 10)
        ensurePhone("11112223333"    , true , false, "11234567890", false, 11)
        ensurePhone("111-222-3333"   , true , false, "123-456-7890", true, 12)
        ensurePhone("1-111-222-3333" , true , false, "1-234-567-8901", true, 14)
    }


    @Test fun can_ensure_ssn() {
        ensureSSN(""            , false, true, "", false, 0)
        ensureSSN("11122333"    , false, false, "", false, 8)
        ensureSSN("111-222-3333", false, false, "", true, 12)
        ensureSSN("111223333"   , true, false, "123456789", false, 9)
        ensureSSN("111-22-3333" , true, false, "123-45-6789", true, 11)
    }


    @Test fun can_ensure_zip() {
        ensureZip(""          , false, true , ""         , false, 0)
        ensureZip("1"         , false, false, ""         , false, 1)
        ensureZip("1234"      , false, false, ""         , false, 4)
        ensureZip("12345-222" , false, false, ""         , true , 9)
        ensureZip("12345"     , true , false, "12345"    , false, 5)
        ensureZip("12345-6789", true , false, "12345-6789", true , 10)
    }


    @Test fun can_ensure_name() {
        ensureName(""          , false, true , ""          , false, 0)
        ensureName("1"         , false, false, ""          , false, 1)
        ensureName("1234"      , false, false, ""          , false, 4)
        ensureName("Bruce"     , true , false, "Diana"     , false, 5)
        ensureName("Diana"     , true , false, "Diana"     , false, 5)
        ensureName("Superman"  , true , false, "Diana"     , false, 8)
        ensureName(" Superman ", true , false, "Diana"     , false, 8)
        ensureName("spider-man", true , false, "Spider-man", true , 10)
    }


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


    fun ensurePhone(text:String, valid:Boolean, empty:Boolean, matched:String, dashed:Boolean, length:Int): Unit {
        val p1 = PhoneUS(text)
        assert(p1.isValid == valid)
        assert(p1.isEmpty == empty)
        assert(p1.minLength == 10)
        assert(p1.maxLength == 14)
        assert(p1.matched == matched)
        assert(p1.isDashed() == dashed)
        assert(p1.text == text)
        assert(p1.length == length)
        assert(p1.toString() == text)
        assert(p1.name == "PhoneUS")
        assert(p1.desc == "United States Phone Format")
        assert(p1.example() == p1.examples[0])
    }


    fun ensureSSN(text:String, valid:Boolean, empty:Boolean, matched:String, dashed:Boolean, length:Int): Unit {
        val p1 = SSN(text)
        assert(p1.isValid == valid)
        assert(p1.isEmpty == empty)
        assert(p1.minLength == 9)
        assert(p1.maxLength == 11)
        assert(p1.matched == matched)
        assert(p1.isDashed() == dashed)
        assert(p1.text == text)
        assert(p1.length == length)
        assert(p1.toString() == text)
        assert(p1.name == "SSN")
        assert(p1.desc == "Social Security Number")
        assert(p1.example() == p1.examples[0])
    }


    fun ensureEmail(text:String, valid:Boolean, empty:Boolean, matched:String, dashed:Boolean, length:Int): Unit {
        val p1 = Email(text)
        assert(p1.isValid == valid)
        assert(p1.isEmpty == empty)
        assert(p1.minLength == 6)
        assert(p1.maxLength == 30)
        assert(p1.matched == matched)
        assert(p1.isDashed() == dashed)
        assert(p1.text == text)
        assert(p1.length == length)
        assert(p1.toString() == text)
        assert(p1.name == "Email")
        assert(p1.desc == "Email Address")
        assert(p1.example() == p1.examples[0])
    }


    fun ensureZip(text:String, valid:Boolean, empty:Boolean, matched:String, dashed:Boolean, length:Int): Unit {
        val p1 = ZipCode(text)
        assert(p1.isValid == valid)
        assert(p1.isEmpty == empty)
        assert(p1.minLength == 5)
        assert(p1.maxLength == 10)
        assert(p1.matched == matched)
        assert(p1.isDashed() == dashed)
        assert(p1.text == text)
        assert(p1.length == length)
        assert(p1.toString() == text)
        assert(p1.name == "US ZipCode")
        assert(p1.desc == "US ZipCode")
        assert(p1.example() == p1.examples[0])
    }


    fun ensureName(text:String, valid:Boolean, empty:Boolean, matched:String, dashed:Boolean, length:Int): Unit {
        val p1 = Name(text)
        assert(p1.isValid == valid)
        assert(p1.isEmpty == empty)
        assert(p1.minLength == 1)
        assert(p1.maxLength == 20)
        assert(p1.matched == matched)
        assert(p1.isDashed() == dashed)
        assert(p1.text == text.trim())
        assert(p1.length == length)
        assert(p1.toString() == text.trim())
        assert(p1.name == "Name")
        assert(p1.desc == "First, Last or Middle name with only chars and dashes")
        assert(p1.example() == p1.examples[0])
    }
}