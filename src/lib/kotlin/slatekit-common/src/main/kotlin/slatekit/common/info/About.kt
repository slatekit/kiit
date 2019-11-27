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

package slatekit.common.info

import slatekit.common.*
import slatekit.common.envs.EnvMode
import slatekit.common.ext.orElse

/**
 * represent meta-data about an application
 * @param area : group owning the app
 * @param name : name of app
 * @param desc : desc of app
 * @param company : company the app is associated with
 * @param region : region associated with app
 * @param url : url for more information
 * @param contact : contact person(s) for app
 * @param version : version of the app
 * @param tags : tags describing the app
 */
data class About(

    @JvmField
    val area: String = "",

    @JvmField
    val name: String,

    @JvmField
    val desc: String,

    @JvmField
    val company: String = "",

    @JvmField
    val region: String = "",

    @JvmField
    val url: String = "",

    @JvmField
    val contact: String = "",

    @JvmField
    val version: String = "",

    @JvmField
    val tags: String = "",

    @JvmField
    val examples: String = ""
) {

    @JvmField
    val id:String = "$area.$name"


    fun log(callback: (String, String) -> Unit) {

        callback("area    ", area)
        callback("name    ", name)
        callback("desc    ", desc)
        callback("region  ", region)
        callback("url     ", url)
        callback("contact ", contact)
        callback("version ", version)
        callback("tags    ", tags)
        callback("examples", examples)
    }

    fun toStringProps(): String {
        val text = "" +
                "area     : " + area + newline +
                "name     : " + name + newline +
                "desc     : " + desc + newline +
                "region   : " + region + newline +
                "url      : " + url + newline +
                "contact  : " + contact + newline +
                "version  : " + version + newline +
                "tags     : " + tags + newline +
                "examples : " + examples + newline
        return text
    }

    fun dir(): String = company.orElse(name).replace(" ", "-")


    fun toId(): Identity = SimpleIdentity(area, name, Agent.App, EnvMode.Dev.name)

    companion object {
        @JvmStatic
        val none = About(
                area = "",
                name = "",
                desc = "",
                company = "",
                region = "",
                url = "",
                contact = "",
                version = "",
                tags = "",
                examples = ""
                        )

        /**
         * builds the about object using just the parameters supplied.
         * @param id
         * @param name
         * @param desc
         * @param company
         * @param version
         * @return
         */
        @JvmStatic
        fun simple(area:String, name: String, desc: String, company: String, version: String): About =
                About(area, name, desc, company, "", "", "", version, "", "")
    }
}
