package test.setup

import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.core.Roles
import kiit.common.crypto.EncDouble
import kiit.common.crypto.EncInt
import kiit.common.crypto.EncLong
import kiit.common.crypto.EncString
import kiit.utils.smartvalues.Email
import kiit.utils.smartvalues.PhoneUS


@Api(area = "samples", name = "types3", desc = "sample api to test other features", auth = AuthModes.NONE, roles = [Roles.NONE])
class SampleTypes3Api {

    @Action(desc = "accepts an encrypted int that will be decrypted")
    fun getDecInt(id: EncInt): String = "decrypted int : " + id.value


    @Action(desc = "accepts an encrypted long that will be decrypted")
    fun getDecLong(id: EncLong): String = "decrypted long : " + id.value


    @Action(desc = "accepts an encrypted double that will be decrypted")
    fun getDecDouble(id: EncDouble): String = "decrypted double : " + id.value


    @Action(desc = "accepts an encrypted string that will be decrypted")
    fun getDecString(id: EncString): String = "decrypted string : " + id.value


    @Action(desc = "accepts a smart string of phone")
    fun getSmartStringPhone(text: PhoneUS): String = "${text.value}"


    @Action(desc = "accepts a smart string of email")
    fun getSmartStringEmail(text: Email): String = "${text.value}"


    @Action(desc = "accepts a smart string of email")
    fun getEnum(status: StatusEnum): String = "${status.name}:${status.value}"


    @Action(desc = "accepts a smart string of email")
    fun getEnumValue(status: StatusEnum): StatusEnum = status
}
