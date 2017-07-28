package slatekit.sampleapp.core.apis

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.common.encrypt.DecDouble
import slatekit.common.encrypt.DecInt
import slatekit.common.encrypt.DecLong
import slatekit.common.encrypt.DecString
import slatekit.common.types.Email
import slatekit.common.types.PhoneUS


@Api(area = "samples", name = "types3", desc = "sample api to test other features")
class SampleTypes3Api {

    @ApiAction(desc = "accepts an encrypted int that will be decrypted")
    fun getDecInt(id: DecInt): String = "decrypted int : " + id.value


    @ApiAction(desc = "accepts an encrypted long that will be decrypted")
    fun getDecLong(id: DecLong): String = "decrypted long : " + id.value


    @ApiAction(desc = "accepts an encrypted double that will be decrypted")
    fun getDecDouble(id: DecDouble): String = "decrypted double : " + id.value


    @ApiAction(desc = "accepts an encrypted string that will be decrypted")
    fun getDecString(id: DecString): String = "decrypted string : " + id.value


    @ApiAction(desc = "accepts a smart string of phone")
    fun getSmartStringPhone(text: PhoneUS): String = "${text.isValid} - ${text.isEmpty} - ${text.text}"


    @ApiAction(desc = "accepts a smart string of email")
    fun getSmartStringEmail(text: Email): String = "${text.isValid} - ${text.isEmpty} - ${text.text}"
}
