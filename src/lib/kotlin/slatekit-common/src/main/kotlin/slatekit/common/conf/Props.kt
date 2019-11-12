package slatekit.common.conf

import slatekit.common.io.Scheme
import slatekit.common.io.Uris
import java.io.FileInputStream
import java.util.*

object Props {

    /**
     * Loads the typesafe config from the filename can be prefixed with a uri to indicate location,
     * such as:
     * 1. "jars://" to indicate loading from resources directory inside jar
     * 2. "user://" to indicate loading from user.home directory
     * 3. "file://" to indicate loading from file system
     *
     * e.g.
     *  - jars://env.qa.conf
     *  - user://${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
     *  - file://c:/slatekit/${company.dir}/${group.dir}/${app.id}/conf/env.qa.conf
     *  - file://./conf/env.qa.conf
     *
     * @param fileName : name of file e.g. email.conf
     * @return
     */
    fun loadFrom(fileName: String?): Properties {
        return when(fileName) {
            null, "" -> Props.loadFromJar(ConfFuncs.CONFIG_DEFAULT_PROPERTIES)
            else -> {
                val (scheme, path) = Uris.readParts(fileName)
                val props = when(scheme) {
                    null            -> Props.loadFromJar(path)
                    is Scheme.Other -> Props.loadFromJar(path)
                    else -> Props.loadFromPath(Scheme.file(scheme, path).absolutePath)
                }
                props
            }
        }
    }

    private fun loadFromJar(path: String): Properties {
        // This is here to debug loading app conf
        val file = this.javaClass.getResource("/" + path).file
        val input = FileInputStream(file)
        val conf = Properties()
        conf.load(input)
        return conf
    }

    private fun loadFromPath(path: String): Properties {
        // This is here to debug loading app conf
        val input = FileInputStream(path)
        val conf = Properties()
        conf.load(input)
        return conf
    }
}