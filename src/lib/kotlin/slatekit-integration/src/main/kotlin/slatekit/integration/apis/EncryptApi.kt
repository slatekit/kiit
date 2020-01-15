/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.integration.apis

import slatekit.apis.Api
import slatekit.apis.Action
import slatekit.apis.AuthModes
import slatekit.apis.Verbs
import slatekit.apis.support.ApiBase
import slatekit.context.Context
import slatekit.common.Sources

/**
 * Created by kreddy on 3/23/2016.
 */
@Api(area = "infra", name = "encryption", desc = "api to encryption and decryption",
        auth = AuthModes.KEYED, roles = ["admin"], verb = Verbs.AUTO, sources = [Sources.CLI])
class EncryptApi(context: Context) : ApiBase(context) {

    @Action(desc = "encryptes the text")
    override fun encrypt(text: String): String {
        return context.enc?.encrypt(text) ?: text
    }

    @Action(desc = "decrypts the text")
    override fun decrypt(text: String): String {
        return context.enc?.decrypt(text) ?: text
    }

    @Action(desc = "encrypts all delimited tokens supplied")
    fun encryptKeyValue(key: String, value: String): String {
        return key + ": " + (context.enc?.encrypt(value) ?: value)
    }

    @Action(desc = "encrypts all delimited tokens supplied")
    fun decryptKeyValue(key: String, value: String): String {
        return key + ": " + (context.enc?.decrypt(value) ?: value)
    }
}