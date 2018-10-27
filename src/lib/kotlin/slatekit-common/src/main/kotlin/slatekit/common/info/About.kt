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

import slatekit.common.newline
import slatekit.common.nonEmptyOrDefault

/**
 * represent meta-data about an application
 * @param id : id of app
 * @param name : name of app
 * @param desc : desc of app
 * @param company : company the app is associated with
 * @param group : group owning the app
 * @param region : region associated with app
 * @param url : url for more information
 * @param contact : contact person(s) for app
 * @param version : version of the app
 * @param tags : tags describing the app
 */
data class About(
    val id: String,
    val name: String,
    val desc: String,
    val company: String = "",
    val group: String = "",
    val region: String = "",
    val url: String = "",
    val contact: String = "",
    val version: String = "",
    val tags: String = "",
    val examples: String = ""
) {

    fun log(callback: (String, String) -> Unit) {

        callback("name    ", name)
        callback("desc    ", desc)
        callback("group   ", group)
        callback("region  ", region)
        callback("url     ", url)
        callback("contact ", contact)
        callback("version ", version)
        callback("tags    ", tags)
        callback("examples", examples)
    }

    fun toStringProps(): String {
        val text = "" +
                "name     : " + name + newline +
                "desc     : " + desc + newline +
                "group    : " + group + newline +
                "region   : " + region + newline +
                "url      : " + url + newline +
                "contact  : " + contact + newline +
                "version  : " + version + newline +
                "tags     : " + tags + newline +
                "examples : " + examples + newline
        return text
    }

    fun dir(): String = company.nonEmptyOrDefault(name).replace(" ", "-")

    companion object {
        @JvmStatic
        val none = About(
                id = "",
                name = "",
                desc = "",
                company = "",
                group = "",
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
        fun simple(id: String, name: String, desc: String, company: String, version: String): About =
                About(id, name, desc, company, "", "", "", "", version, "", "")
    }
}
