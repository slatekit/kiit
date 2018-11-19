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

import slatekit.common.utils.Props

/**
 * Represents a host such as a cloud server. e.g. ec2
 * @param name : name of the language
 * @param home : home directory of the language
 * @param origin : origin of the language, e.g. for scala -reference to jre
 * @param versionNum : version of the language
 * @param version : addition info about architechture for lang ( e.g. 64 bit )
 * @param ext1 : additional information about the language
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
    val origin: String,

    @JvmField
    val ext1: String
) : Info {

    override fun props():List<Pair<String,String>> = listOf(
            "name"    to name,
            "home"    to home,
            "vendor"  to vendor,
            "version" to version,
            "origin"  to origin,
            "ext1"    to ext1
    )

    companion object {
        @JvmStatic
        val none = Lang(
            name = "none",
            home = "-",
            vendor = "",
            version = "-",
            origin = "local",
            ext1 = "-"
        )

        @JvmStatic
        fun kotlin(): Lang =
            Lang(
                name = "kotlin",
                home = Props.javaHome.replace("\\", "/"),
                version = Props.javaVersion,
                vendor = Props.javaVendor,
                origin = "local",
                ext1 = ""
            )
    }
}
