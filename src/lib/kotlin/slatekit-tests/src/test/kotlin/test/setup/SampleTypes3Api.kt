package test.setup

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.common.auth.Roles
import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.common.encrypt.EncString
import slatekit.common.smartvalues.Email
import slatekit.common.smartvalues.PhoneUS


@Api(area = "samples", name = "types3", desc = "sample api to test other features", auth = AuthModes.None, roles = [Roles.none])
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
