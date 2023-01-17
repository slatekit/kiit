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

import kiit.common.utils.Props

/**
 * Represents a host such as a cloud server. e.g. ec2
 * @param name : name of the language
 * @param home : home directory of the language
 * @param origin : origin of the language, e.g. for kotlin -reference to jre
 * @param versionNum : version of the language
 * @param version : addition info about architechture for lang ( e.g. 64 bit )
 */
data class Lang(

    @JvmField
    val name: String,

    @JvmField
    val home: String,

    @JvmField
    val version: String,

    @JvmField
    val vendor: String,

    @JvmField
    val origin: String
) : Meta {

    override fun props():List<Pair<String,String>> = listOf(
            "name"    to name,
            "home"    to home,
            "vendor"  to vendor,
            "version" to version,
            "origin"  to origin
    )

    companion object {
        @JvmStatic
        val none = Lang(
            name = "none",
            home = "-",
            vendor = "",
            version = "-",
            origin = "local"
        )

        @JvmStatic
        fun kotlin(): Lang =
            Lang(
                name = "kotlin",
                home = Props.javaHome.replace("\\", "/"),
                version = Props.javaVersion,
                vendor = Props.javaVendor,
                origin = "local"
            )
    }
}
