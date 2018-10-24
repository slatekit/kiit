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

import slatekit.common.Files
import slatekit.common.app.AppLocation
import slatekit.common.app.LocationUserDir
import java.io.File

/**
 * Represents folder locations for an application
 * Folders locations are organized by company/app and stored in the user home directory.
 * e.g.
 *
 * - user                   ( e.g /usr/kreddy or c:/users/kreddy )
 *   - rootOrCompany        ( ? name of company or name of root folder )
 *                           ( recommended to use root folder containing 1 or more apps )
 *     - group              ( ? parent folder for all apps for group )
 *       - product1.console ( individual apps go here )
 *       - product1.shell
 *       - product1.server
 *         - conf
 *         - cache
 *         - logs
 *         - inputs
 *         - outputs
 *
 * @param location: location of where the folders reside ( local (to app) | programs | user.home )
 * @param root    : optional name of root folder or company name
 * @param group   : optional name of group folder that holds all apps
 * @param app     : name of the application folder for this app
 * @param cache   : name of cache folder for the application
 * @param inputs  : name of input folder for the application
 * @param logs    : name of logs folder for the application
 * @param outputs : name of output folder for the application
 */


data class Folders(
        val location: AppLocation,
        val home: String,
        val root: String?,
        val group: String?,
        val app: String,
        val cache: String,
        val inputs: String,
        val logs: String,
        val outputs: String,
        val temp: String,
        val conf: String
) {

    fun log(callback: (String, String) -> Unit) {
        callback("root", root ?: "")
        callback("group", group ?: "")
        callback("app", app)
        callback("cache", cache)
        callback("inputs", app)
        callback("log", logs)
        callback("outputs", outputs)
        callback("temp", temp)
    }


    val pathToConf: String get() = this.pathToApp + File.separator + conf
    val pathToCache: String get() = this.pathToApp + File.separator + cache
    val pathToInputs: String get() = this.pathToApp + File.separator + inputs
    val pathToLogs: String get() = this.pathToApp + File.separator + logs
    val pathToOutputs: String get() = this.pathToApp + File.separator + outputs
    val pathToTemp: String get() = this.pathToApp + File.separator + temp


    fun buildPath(part: String): String {
        val userHome = System.getProperty("user.home")
        val path = userHome + File.separator +
                root + File.separator +
                group + File.separator +
                part.replace(" ", "")
        return path
    }


    fun getConfFilePath(fileName: String): String = pathToConf + File.separator + fileName
    fun getCacheFilePath(fileName: String): String = pathToCache + File.separator + fileName
    fun getInputsFilePath(fileName: String): String = pathToInputs + File.separator + fileName
    fun getOutputsFilePath(fileName: String): String = pathToOutputs + File.separator + fileName
    fun getLogsFilePath(fileName: String): String = pathToLogs + File.separator + fileName
    fun getTempFilePath(fileName: String): String = pathToTemp + File.separator + fileName


    val pathToApp: String get() {
        val sep = File.separator
        val homePath = home
        val rootPath = root?.let { folder -> homePath + sep + folder } ?: homePath
        val groupPath = group?.let { folder -> rootPath + sep + folder } ?: rootPath
        val finalPath = groupPath + sep + app
        return finalPath
    }


    fun create() {
        val rootPath = Files.mkDir(home, root ?: "")
        val groupPath = Files.mkDir(rootPath, group ?: "")
        val appPath = Files.mkDir(groupPath, app)
        Files.mkDir(appPath, cache)
        Files.mkDir(appPath, conf)
        Files.mkDir(appPath, inputs)
        Files.mkDir(appPath, logs)
        Files.mkDir(appPath, outputs)
        Files.mkDir(appPath, temp)
    }


    companion object Folders {

        @JvmStatic
        val none = Folders(
                location = LocationUserDir,
                home = System.getProperty("user.dir"),
                root = null,
                group = null,
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
                location = LocationUserDir,
                home = System.getProperty("user.dir"),
                root = "slatekit",
                group = "samples",
                app = "app",
                cache = "cache",
                inputs = "input",
                logs = "log",
                outputs = "output",
                temp = "temp",
                conf = "conf"
        )


        @JvmStatic
        fun userDir(root: String, group: String, app: String) =
                Folders(
                        LocationUserDir,
                        System.getProperty("user.home"),
                        root = root,
                        group = group,
                        app = app,
                        cache = "cache",
                        inputs = "input",
                        logs = "log",
                        outputs = "output",
                        temp = "temp",
                        conf = "conf"
                )
    }
}