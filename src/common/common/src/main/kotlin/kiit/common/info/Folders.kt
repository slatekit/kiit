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

import kiit.common.ext.toId
import kiit.common.io.Files
import java.io.File

/**
 * Represents folder locations for an application
 * Folders locations are organized by company/app and stored in the user home directory.
 * e.g.
 *
 * - user                   ( e.g /usr/kreddy or c:/users/kreddy )
 *   - company              ( ? name of company or name of root folder )
 *     - area               ( ? parent folder for all apps for a certain area/department/group )
 *       - product1.console ( individual apps go here )
 *       - product2.shell
 *       - product3.server
 *         - conf
 *         - cache
 *         - logs
 *         - inputs
 *         - outputs
 *
 * @param home: location of where the folders reside ( e.g. ~/ ( user directory), /apps/, current directory )
 * @param root : optional name of root folder or company name
 * @param area : optional name of group folder that holds all apps
 * @param app : name of the application folder for this app
 * @param cache : name of cache folder for the application
 * @param inputs : name of input folder for the application
 * @param logs : name of logs folder for the application
 * @param outputs : name of output folder for the application
 */

data class Folders private constructor(

        @JvmField
        val home: String,

        @JvmField
        val root: String,

        @JvmField
        val area: String,

        @JvmField
        val app: String,

        @JvmField
        val cache: String,

        @JvmField
        val inputs: String,

        @JvmField
        val logs: String,

        @JvmField
        val outputs: String,

        @JvmField
        val temp: String,

        @JvmField
        val conf: String
) : Meta {

    override fun props(): List<Pair<String, String>> = listOf(
            "root" to root,
            "area" to area,
            "app" to app,
            "cache" to cache,
            "inputs" to app,
            "log" to logs,
            "outputs" to outputs,
            "temp" to temp
    )

    val pathToConf   : String get() = this.pathToApp + File.separator + conf
    val pathToCache  : String get() = this.pathToApp + File.separator + cache
    val pathToInputs : String get() = this.pathToApp + File.separator + inputs
    val pathToLogs   : String get() = this.pathToApp + File.separator + logs
    val pathToOutputs: String get() = this.pathToApp + File.separator + outputs
    val pathToTemp   : String get() = this.pathToApp + File.separator + temp

    fun buildPath(part: String): String {
        val userHome = System.getProperty("user.home")
        val path = userHome + File.separator +
                root + File.separator +
                area + File.separator +
                part.replace(" ", "")
        return path
    }

    val pathToApp: String
        get() {
            val sep = File.separator
            val homePath = home
            val rootPath = homePath + sep + root
            val areaPath = rootPath + sep + area
            val finalPath = areaPath + sep + app
            return finalPath
        }

    fun create() {
        val rootPath = Files.mkDir(home, root)
        val areaPath = Files.mkDir(rootPath, area)
        val appPath = Files.mkDir(areaPath, app)
        Files.mkDir(appPath, cache)
        Files.mkDir(appPath, conf)
        Files.mkDir(appPath, inputs)
        Files.mkDir(appPath, logs)
        Files.mkDir(appPath, outputs)
        Files.mkDir(appPath, temp)
    }

    companion object {

        @JvmStatic
        val none = Folders(
                home = System.getProperty("user.dir"),
                root = "slatekit",
                area = "samples",
                app = "app",
                cache = "cache",
                inputs = "input",
                logs = "log",
                outputs = "output",
                temp = "temp",
                conf = "conf"
        )

        @JvmStatic
        val default = Folders(
                home = System.getProperty("user.dir"),
                root = "slatekit",
                area = "samples",
                app = "app",
                cache = "cache",
                inputs = "input",
                logs = "log",
                outputs = "output",
                temp = "temp",
                conf = "conf"
        )

        @JvmStatic
        fun userDir(about:About) : Folders {
            return create(System.getProperty("user.home"), about.company, about.area, about.name)
        }

        @JvmStatic
        fun userDir(root: String, area: String, name: String) : Folders {
            return create(System.getProperty("user.home"), root, area, name)
        }

        @JvmStatic
        fun installDir(tool:String, about:About) : Folders {
            return create("/usr/local/opt/$tool", about.company, about.area, about.name)
        }

        @JvmStatic
        private fun create(home:String, root: String, area: String, app: String) : Folders {
            // For user home directories ( ~/ ), the root always begins with "." as in ~/.slatekit
            val finalRoot = "." + root.toId()
            val finalArea = area.toId()
            val finalApp = app.toId()
            return Folders(
                    home = home,
                    root = finalRoot,
                    area = finalArea,
                    app = finalApp,
                    cache = "cache",
                    inputs = "input",
                    logs = "log",
                    outputs = "output",
                    temp = "temp",
                    conf = "conf"
            )
        }
    }
}
