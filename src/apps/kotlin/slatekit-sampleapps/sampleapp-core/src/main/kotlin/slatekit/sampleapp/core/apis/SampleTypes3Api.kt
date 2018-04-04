package slatekit.sampleapp.core.apis

import slatekit.apis.Api
import slatekit.apis.ApiAction
import slatekit.common.encrypt.EncDouble
import slatekit.common.encrypt.EncInt
import slatekit.common.encrypt.EncLong
import slatekit.common.encrypt.EncString
import slatekit.common.types.Email
import slatekit.common.types.PhoneUS


@Api(area = "samples", name = "types3", desc = "sample api to test other features")
class SampleTypes3Api {

    @ApiAction(desc = "accepts an encrypted int that will be decrypted")
    fun getDecInt(id: EncInt): String = "decrypted int : " + id.value


    @ApiAction(desc = "accepts an encrypted long that will be decrypted")
    fun getDecLong(id: EncLong): String = "decrypted long : " + id.value


    @ApiAction(desc = "accepts an encrypted double that will be decrypted")
    fun getDecDouble(id: EncDouble): String = "decrypted double : " + id.value


    @ApiAction(desc = "accepts an encrypted string that will be decrypted")
    fun getDecString(id: EncString): String = "decrypted string : " + id.value


    @ApiAction(desc = "accepts a smart string of phone")
    fun getSmartStringPhone(text: PhoneUS): String = "valid:${text.isValid} - empty:${text.isEmpty} - text:${text.text}"


    @ApiAction(desc = "accepts a smart string of email")
    fun getSmartStringEmail(text: Email): String = "valid:${text.isValid} - empty:${text.isEmpty} - text:${text.text}"
}
