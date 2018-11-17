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
 *
 * @param name : name of host e.g. srv-001
 * @param ip : ip address
 * @param origin : origin of the server e.g. aws | azure
 * @param arch : architecture of the server e.g. linux | windows
 * @param version : version of the server e.g. linux version or windows version
 * @param ext1 : additional information about the server
 */
data class Host(
    @JvmField
    val name: String,

    @JvmField
    val ip: String,

    @JvmField
    val origin: String,

    @JvmField
    val arch: String,

    @JvmField
    val version: String,

    @JvmField
    val ext1: String
) {

    fun log(callback: (String, String) -> Unit) {
        callback("name", name)
        callback("ip", ip)
        callback("origin", origin)
        callback("arch", arch)
        callback("version", version)
        callback("ext1", ext1)
    }

    companion object {

        @JvmStatic
        val none = Host(
            name = "none",
            ip = "-",
            origin = "local",
            arch = "-",
            version = "-",
            ext1 = "-"
        )

        @JvmStatic
        fun local(): Host =
            Host(
                name = computerName(),
                ip = "",
                origin = Props.osName,
                arch = Props.osArch,
                version = Props.osVersion,
                ext1 = Props.tmpDir
            )

        @JvmStatic
        fun computerName(): String {
            val env = System.getenv()

            val name = if (env.containsKey("COMPUTERNAME"))
                env["COMPUTERNAME"]
            else if (env.containsKey("HOSTNAME"))
                env["HOSTNAME"]
            else
                "Unknown Computer"

            return name.orEmpty()
        }
    }
}
