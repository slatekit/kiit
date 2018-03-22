package test.common

import org.junit.Test
import slatekit.common.*


class CaseTests {

    fun ensure(case: Case, expected:String){
        assert(case.text == expected)
    }


    @Test fun can_convert_to_lower_hypen() {
        ensure(lowerHyphen("ABCD")     , "abcd")
        ensure(lowerHyphen("ABCD_123") , "abcd-123")
        ensure(lowerHyphen("ABCD-123") , "abcd-123")
        ensure(lowerHyphen("ABCD 123") , "abcd-123")
        ensure(lowerHyphen("abcd_123") , "abcd-123")
        ensure(lowerHyphen("abcd-123") , "abcd-123")
        ensure(lowerHyphen("abcd 123") , "abcd-123")
        ensure(lowerHyphen("abCd_123") , "ab-cd-123")
        ensure(lowerHyphen("abCd-123") , "ab-cd-123")
        ensure(lowerHyphen("abCd 123") , "ab-cd-123")
    }


    @Test fun can_convert_to_upper_hypen() {
        ensure(upperHyphen("ABCD")     , "ABCD")
        ensure(upperHyphen("ABCD_123") , "ABCD-123")
        ensure(upperHyphen("ABCD-123") , "ABCD-123")
        ensure(upperHyphen("ABCD 123") , "ABCD-123")
        ensure(upperHyphen("abcd_123") , "ABCD-123")
        ensure(upperHyphen("abcd-123") , "ABCD-123")
        ensure(upperHyphen("abcd 123") , "ABCD-123")
        ensure(upperHyphen("abCd_123") , "AB-CD-123")
        ensure(upperHyphen("abCd-123") , "AB-CD-123")
        ensure(upperHyphen("abCd 123") , "AB-CD-123")
    }


    @Test fun can_convert_to_lower_underscore() {
        ensure(lowerUnderscore("ABCD")     , "abcd")
        ensure(lowerUnderscore("ABCD_123") , "abcd_123")
        ensure(lowerUnderscore("ABCD-123") , "abcd_123")
        ensure(lowerUnderscore("ABCD 123") , "abcd_123")
        ensure(lowerUnderscore("abcd_123") , "abcd_123")
        ensure(lowerUnderscore("abcd-123") , "abcd_123")
        ensure(lowerUnderscore("abcd 123") , "abcd_123")
        ensure(lowerUnderscore("abCd_123") , "ab_cd_123")
        ensure(lowerUnderscore("abCd-123") , "ab_cd_123")
        ensure(lowerUnderscore("abCd 123") , "ab_cd_123")
    }


    @Test fun can_convert_to_upper_underscore() {
        ensure(upperUnderscore("ABCD")     , "ABCD")
        ensure(upperUnderscore("ABCD_123") , "ABCD_123")
        ensure(upperUnderscore("ABCD-123") , "ABCD_123")
        ensure(upperUnderscore("ABCD 123") , "ABCD_123")
        ensure(upperUnderscore("abcd_123") , "ABCD_123")
        ensure(upperUnderscore("abcd-123") , "ABCD_123")
        ensure(upperUnderscore("abcd 123") , "ABCD_123")
        ensure(upperUnderscore("abCd_123") , "AB_CD_123")
        ensure(upperUnderscore("abCd-123") , "AB_CD_123")
        ensure(upperUnderscore("abCd 123") , "AB_CD_123")
    }


    @Test fun can_convert_lower_camel() {
        ensure(lowerCamel("Abcd")     , "abcd")
        ensure(lowerCamel("AbCd_efg") , "abCdEfg")
        ensure(lowerCamel("AbCd-efg") , "abCdEfg")
        ensure(lowerCamel("AbCd efg") , "abCdEfg")
        ensure(lowerCamel("abCd_efg") , "abCdEfg")
        ensure(lowerCamel("abCd-efg") , "abCdEfg")
        ensure(lowerCamel("abCd efg") , "abCdEfg")
        ensure(lowerCamel("abCd_efg") , "abCdEfg")
        ensure(lowerCamel("abCd-efg") , "abCdEfg")
        ensure(lowerCamel("abCd efg") , "abCdEfg")
    }


    @Test fun can_convert_upper_camel() {
        ensure(upperCamel("Abcd")    , "Abcd")

        ensure(upperCamel("_AbCd_efg") , "AbCdEfg")
        ensure(upperCamel("-AbCd_efg") , "AbCdEfg")
        ensure(upperCamel(" AbCd_efg") , "AbCdEfg")
        ensure(upperCamel("AbCd_efg_") , "AbCdEfg")
        ensure(upperCamel("AbCd_efg-") , "AbCdEfg")
        ensure(upperCamel("AbCd_efg ") , "AbCdEfg")
        ensure(upperCamel("AbCd_efg")  , "AbCdEfg")
        ensure(upperCamel("AbCd-efg")  , "AbCdEfg")
        ensure(upperCamel("AbCd efg")  , "AbCdEfg")
        ensure(upperCamel("abCd_efg")  , "AbCdEfg")
        ensure(upperCamel("abCd-efg")  , "AbCdEfg")
        ensure(upperCamel("abCd efg")  , "AbCdEfg")
    }
}