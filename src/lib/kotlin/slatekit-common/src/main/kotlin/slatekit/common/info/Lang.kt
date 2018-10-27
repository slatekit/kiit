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

import slatekit.common.Props

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
    val name: String,
    val home: String,
    val version: String,
    val vendor: String,
    val origin: String,
    val ext1: String
) {

    fun log(callback: (String, String) -> Unit) {
        callback("name", name)
        callback("home", home)
        callback("vendor", vendor)
        callback("version", version)
        callback("origin", origin)
        callback("ext1", ext1)
    }

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