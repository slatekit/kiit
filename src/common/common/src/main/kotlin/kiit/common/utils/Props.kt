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

package kiit.common.utils

import java.io.File

/**
 * Created by kishorereddy on 5/19/17.
 */
object Props {

    // OS
    val osArch get() = getOrEmpty("os.arch")
    val osName get() = getOrEmpty("os.name")
    val osVersion get() = getOrEmpty("os.version")

    // Java
    val javaClassPath get() = getOrEmpty("java.class.path")
    val javaHome get() = getOrEmpty("java.home")
    val javaVendor get() = getOrEmpty("java.vendor")
    val javaVersion get() = getOrEmpty("java.version")
    val javaVmInfo get() = getOrEmpty("java.vm.info")
    val javaVmName get() = getOrEmpty("java.vm.name")
    val javaVmVendor get() = getOrEmpty("java.vm.vendor")
    val javaVmVersion get() = getOrEmpty("java.vm.version")
    val tmpDir get() = getOrEmpty("java.io.tmpdir")
    val pathSeparator get() = File.separatorChar.toString()

    // User
    val userDir get() = getOrEmpty("user.dir")
    val userHome get() = getOrEmpty("user.home")
    val userName get() = getOrEmpty("user.name")

    fun getOrEmpty(name: String): String {
        val pval = System.getProperty(name)
        return pval.orEmpty()
    }
}
