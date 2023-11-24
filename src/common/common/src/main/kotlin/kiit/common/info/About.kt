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

package kiit.common.info

import kiit.common.*
import kiit.common.envs.EnvMode
import kiit.common.ext.orElse

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
    val company: String = "",

    @JvmField
    val area: String = "",

    @JvmField
    val name: String,

    @JvmField
    val desc: String,

    @JvmField
    val region: String = "",

    @JvmField
    val url: String = "",

    @JvmField
    val contact: String = "",

    @JvmField
    val tags: String = "",

    @JvmField
    val examples: String = ""
) {

    @JvmField
    val id:String = "$area.$name"


    fun log(callback: (String, String) -> Unit) {
        callback("company" , company)
        callback("area    ", area)
        callback("name    ", name)
        callback("desc    ", desc)
        callback("region  ", region)
        callback("url     ", url)
        callback("contact ", contact)
        callback("tags    ", tags)
        callback("examples", examples)
    }

    fun toStringProps(): String {
        val text = "" +
                "company  : " + company + newline +
                "area     : " + area + newline +
                "name     : " + name + newline +
                "desc     : " + desc + newline +
                "region   : " + region + newline +
                "url      : " + url + newline +
                "contact  : " + contact + newline +
                "tags     : " + tags + newline +
                "examples : " + examples + newline
        return text
    }

    fun dir(): String = company.orElse(name).replace(" ", "-")


    fun toId(): Identity = Identity.of(company, area, name, Agent.App, EnvMode.Dev)

    companion object {
        @JvmStatic
        val none = About(
                company = "",
                area = "",
                name = "",
                desc = "",
                region = "",
                url = "",
                contact = "",
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
        fun simple(company: String, area:String, name: String, desc: String): About =
                About(company, area, name, desc, "", "", "", "", "")
    }
}
