/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *
 * 
  *  </kiit_header>
 */

package kiit.integration.apis

import kiit.apis.Api
import kiit.apis.Action
import kiit.apis.AuthModes
import kiit.apis.Verbs
import kiit.apis.ApiBase
import kiit.context.Context
import kiit.common.Sources

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