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

package kiit.common.conf

import kiit.common.values.Inputs
import kiit.common.Strings
import kiit.common.io.Uri

/**
 * Base class to get config settings with support for :
 * 1. decrypting settings
 * 2. referencing settings in other files
 * 3. mapping settings from objects ( db connection, login, apiCredentials ) via ConfigSupport trait
 *
 * @param encryptor: Optional encryptor for decrypting encrypted config settings.
 */
abstract class Conf(val cls:Class<*>, val uri:Uri, val encryptor: ((String) -> String)?) : Inputs, ConfigSupport {

    /**
     * The origin of the config ( e.g. file name for now )
     * @return
     */
    open fun origin(): String = uri.toString()

    /**
     * Extends the config by supporting decryption via marker tags.
     * e.g.
     *  db.connection = "@{decrypt('8r4AbhQyvlzSeWnKsamowA')}"
     *
     * @param key : key: The name of the config key
     * @return
     */
    override fun getString(key: String): String {
        val value = get(key) ?: ""
        return Strings.decrypt(value.toString(), encryptor)
    }

    /**
     * Loads a new config file from the file path supplied.
     * Derived classes can override this to load configs of their own type.
     *
     * NOTE: An example could be
     *     db
     *     {
     *       location: "user://myapp/conf/db.conf"
     *     }
     * @param file
     * @return
     */
    open fun loadFrom(file: String?): Conf? = null

    /**
     * To support convenience methods
     * @return
     */
    override fun config(): Conf = this
}
