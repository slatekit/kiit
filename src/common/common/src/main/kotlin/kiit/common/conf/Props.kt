package kiit.common.conf

import kiit.common.io.Alias
import kiit.common.io.Uri
import kiit.common.io.Uris
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
     * @param path : name of file e.g. email.conf
     * @return
     */
    fun fromPath(cls:Class<*>, path: String?): Pair<Uri, Properties> {
        return when(path) {
            null, "" -> {
                val name = Confs.CONFIG_DEFAULT_PROPERTIES
                val uri = Uri.jar(name)
                val props = fromJar(cls, name)
                Pair(uri, props)
            }
            else -> {
                val uri = Uris.parse(path)
                val props = fromUri(cls, uri)
                Pair(uri, props)
            }
        }
    }


    /**
     * Loads properties from the Slate Kit Uri supplied which handles aliases
     *
     * ALIASES:
     * abs://usr/local/myapp/settings.conf    -> /usr/local/myapp/settings.conf
     * usr://myapp/settings.conf              -> ~/myapp/settings.conf
     * tmp://myapp/settings.conf              -> ~/temp/myapp/settings.conf
     * cfg://settings.conf                    -> ./conf/settings
     */
    fun fromUri(cls:Class<*>, uri: Uri):Properties {
        val props = when(uri.root) {
            is Alias.Jar   -> fromJar(cls,uri.path ?: Confs.CONFIG_DEFAULT_PROPERTIES)
            is Alias.Other -> fromJar(cls,uri.path ?: Confs.CONFIG_DEFAULT_PROPERTIES)
            else -> fromFileRaw(uri.toFile().absolutePath)
        }
        return props
    }


    /**
     * Loads properties from the file / path supplied with handling for Slate Kit Aliases
     * @param cls: The Class load the resource from
     * @param path: The path to the file e.g. env.conf or /folder1/settings.conf
     */
    fun fromJar(cls:Class<*>, path: String): Properties {
        // This is here to debug loading app conf
        val input = cls.getResourceAsStream("/" + path)
        val conf = Properties()
        conf.load(input)
        return conf
    }

    /**
     * Loads properties from the file / path supplied with handling for Slate Kit Aliases
     *
     * ALIASES:
     * abs://usr/local/myapp/settings.conf    -> /usr/local/myapp/settings.conf
     * usr://myapp/settings.conf              -> ~/myapp/settings.conf
     * tmp://myapp/settings.conf              -> ~/temp/myapp/settings.conf
     * cfg://settings.conf                    -> ./conf/settings
     */
    fun fromFile(fileName: String): Properties {
        val uri = Uris.parse(fileName)
        val path = uri.toFile().absolutePath
        return fromFileRaw(path)
    }

    private fun fromFileRaw(path: String): Properties {
        // This is here to debug loading app conf
        val input = FileInputStream(path)
        val conf = Properties()
        conf.load(input)
        return conf
    }
}