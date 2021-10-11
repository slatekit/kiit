package test.common

import org.junit.Assert
import org.junit.Test
import slatekit.utils.naming.*


class CaseTests {

    fun ensure(namer: Namer, input:String, expected:String){
        Assert.assertTrue(namer.rename(input) == expected)
    }


    @Test fun can_convert_to_lower_hypen() {
        ensure(LowerHyphenNamer(), "ABCD"     , "abcd")
        ensure(LowerHyphenNamer(), "ABCD_123" , "abcd-123")
        ensure(LowerHyphenNamer(), "ABCD-123" , "abcd-123")
        ensure(LowerHyphenNamer(), "ABCD 123" , "abcd-123")
        ensure(LowerHyphenNamer(), "abcd_123" , "abcd-123")
        ensure(LowerHyphenNamer(), "abcd-123" , "abcd-123")
        ensure(LowerHyphenNamer(), "abcd 123" , "abcd-123")
        ensure(LowerHyphenNamer(), "abCd_123" , "ab-cd-123")
        ensure(LowerHyphenNamer(), "abCd-123" , "ab-cd-123")
        ensure(LowerHyphenNamer(), "abCd 123" , "ab-cd-123")
    }


    @Test fun can_convert_to_upper_hypen() {
        ensure(UpperHyphenNamer(), "ABCD"      , "ABCD")
        ensure(UpperHyphenNamer(), "ABCD_123"  , "ABCD-123")
        ensure(UpperHyphenNamer(), "ABCD-123"  , "ABCD-123")
        ensure(UpperHyphenNamer(), "ABCD 123"  , "ABCD-123")
        ensure(UpperHyphenNamer(), "abcd_123"  , "ABCD-123")
        ensure(UpperHyphenNamer(), "abcd-123"  , "ABCD-123")
        ensure(UpperHyphenNamer(), "abcd 123"  , "ABCD-123")
        ensure(UpperHyphenNamer(), "abCd_123"  , "AB-CD-123")
        ensure(UpperHyphenNamer(), "abCd-123"  , "AB-CD-123")
        ensure(UpperHyphenNamer(), "abCd 123"  , "AB-CD-123")
    }


    @Test fun can_convert_to_lower_underscore() {
        ensure(LowerUnderscoreNamer(), "ABCD"      , "abcd")
        ensure(LowerUnderscoreNamer(), "ABCD_123"  , "abcd_123")
        ensure(LowerUnderscoreNamer(), "ABCD-123"  , "abcd_123")
        ensure(LowerUnderscoreNamer(), "ABCD 123"  , "abcd_123")
        ensure(LowerUnderscoreNamer(), "abcd_123"  , "abcd_123")
        ensure(LowerUnderscoreNamer(), "abcd-123"  , "abcd_123")
        ensure(LowerUnderscoreNamer(), "abcd 123"  , "abcd_123")
        ensure(LowerUnderscoreNamer(), "abCd_123"  , "ab_cd_123")
        ensure(LowerUnderscoreNamer(), "abCd-123"  , "ab_cd_123")
        ensure(LowerUnderscoreNamer(), "abCd 123"  , "ab_cd_123")
    }


    @Test fun can_convert_to_upper_underscore() {
        ensure(UpperUnderscoreNamer(), "ABCD"      , "ABCD")
        ensure(UpperUnderscoreNamer(), "ABCD_123"  , "ABCD_123")
        ensure(UpperUnderscoreNamer(), "ABCD-123"  , "ABCD_123")
        ensure(UpperUnderscoreNamer(), "ABCD 123"  , "ABCD_123")
        ensure(UpperUnderscoreNamer(), "abcd_123"  , "ABCD_123")
        ensure(UpperUnderscoreNamer(), "abcd-123"  , "ABCD_123")
        ensure(UpperUnderscoreNamer(), "abcd 123"  , "ABCD_123")
        ensure(UpperUnderscoreNamer(), "abCd_123"  , "AB_CD_123")
        ensure(UpperUnderscoreNamer(), "abCd-123"  , "AB_CD_123")
        ensure(UpperUnderscoreNamer(), "abCd 123"  , "AB_CD_123")
    }


    @Test fun can_convert_lower_camel() {
        ensure(LowerCamelNamer(), "Abcd"      , "abcd")
        ensure(LowerCamelNamer(), "AbCd_efg"  , "abCdEfg")
        ensure(LowerCamelNamer(), "AbCd-efg"  , "abCdEfg")
        ensure(LowerCamelNamer(), "AbCd efg"  , "abCdEfg")
        ensure(LowerCamelNamer(), "abCd_efg"  , "abCdEfg")
        ensure(LowerCamelNamer(), "abCd-efg"  , "abCdEfg")
        ensure(LowerCamelNamer(), "abCd efg"  , "abCdEfg")
        ensure(LowerCamelNamer(), "abCd_efg"  , "abCdEfg")
        ensure(LowerCamelNamer(), "abCd-efg"  , "abCdEfg")
        ensure(LowerCamelNamer(), "abCd efg"  , "abCdEfg")
    }


    @Test fun can_convert_upper_camel() {
        ensure(UpperCamelNamer(), "Abcd"       , "Abcd")
        ensure(UpperCamelNamer(), "_AbCd_efg"  , "AbCdEfg")
        ensure(UpperCamelNamer(), "-AbCd_efg"  , "AbCdEfg")
        ensure(UpperCamelNamer(), " AbCd_efg"  , "AbCdEfg")
        ensure(UpperCamelNamer(), "AbCd_efg_"  , "AbCdEfg")
        ensure(UpperCamelNamer(), "AbCd_efg-"  , "AbCdEfg")
        ensure(UpperCamelNamer(), "AbCd_efg "  , "AbCdEfg")
        ensure(UpperCamelNamer(), "AbCd_efg"   , "AbCdEfg")
        ensure(UpperCamelNamer(), "AbCd-efg"   , "AbCdEfg")
        ensure(UpperCamelNamer(), "AbCd efg"   , "AbCdEfg")
        ensure(UpperCamelNamer(), "abCd_efg"   , "AbCdEfg")
        ensure(UpperCamelNamer(), "abCd-efg"   , "AbCdEfg")
        ensure(UpperCamelNamer(), "abCd efg"   , "AbCdEfg")
    }
}
