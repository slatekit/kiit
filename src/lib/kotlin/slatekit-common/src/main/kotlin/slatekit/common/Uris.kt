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

package slatekit.common

import java.io.File


object Uris {

    val URI_PREFIX_USER = "user://"
    val URI_PREFIX_TEMP = "temp://"
    val URI_PREFIX_FILE = "file://"
    val URI_PREFIX_JARS = "jars://"
    val URI_PREFIX_RESR = "resr://"


    /**
     * Interprets the path URI to support references to various locations:
     * 1. user dir: user://{folder} ( user home directory for os  )
     * 2. temp dir: temp://{folder} ( temp files directory for os )
     * 3. file dir: file://{path}   ( absolution file location    )
     * @param uri
     * @return
     */
    fun interpret(uri: String): String? {
        val pathParts = Strings.substring(uri, "://")
        val finalPath = pathParts?.let { parts ->
            val prefix = parts.first
            val path = parts.second
            when (prefix) {
                URI_PREFIX_USER -> File(System.getProperty("user.home"), path).toString()
                URI_PREFIX_TEMP -> File(System.getProperty("java.io.tmpdir"), path).toString()
                URI_PREFIX_FILE -> File(path).toString()
                else            -> uri
            }
        } ?: uri
        return finalPath
    }


    /**
     * Reads the text file represented by uri after first interpreting the path.
     * 1. user dir: user://{folder} ( user home directory for os  )
     * 2. temp dir: temp://{folder} ( temp files directory for os )
     * 3. file dir: file://{path}   ( absolution file location    )
     * @param uri
     * @return
     */
    fun readText(uri: String): String? {
        val pathParts = Strings.substring(uri, "://")
        val content = pathParts?.let { parts: Pair<String, String> ->
            val prefix = parts.first
            val path = parts.second
            val userDir = System.getProperty("user.home")
            val tempDir = System.getProperty("java.io.tmpdir")
            when (prefix) {
                URI_PREFIX_USER -> File(userDir, path).readText()
                URI_PREFIX_TEMP -> File(tempDir, path).readText()
                URI_PREFIX_FILE -> File(path).readText()
                else            -> File(path).readText()
            }
        } ?: File(uri).readText()
        return content
    }


    /**
     * Reads the text file represented by uri after first interpreting the path.
     * 1. user dir: user://{folder} ( user home directory for os  )
     * 2. temp dir: temp://{folder} ( temp files directory for os )
     * 3. file dir: file://{path}   ( absolution file location    )
     * @param uri
     * @return
     */
    fun readDoc(uri: String): Doc? {
        val pathParts = Strings.substring(uri, "://")
        val doc = pathParts?.let { parts: Pair<String, String> ->
            val prefix = parts.first
            val path = parts.second
            val userDir = System.getProperty("user.home")
            val tempDir = System.getProperty("java.io.tmpdir")
            when (prefix) {
                URI_PREFIX_USER -> buildDoc(File(userDir, path))
                URI_PREFIX_TEMP -> buildDoc(File(tempDir, path))
                URI_PREFIX_FILE -> buildDoc(File(path))
                else            -> buildDoc(File(path))
            }
        } ?: buildDoc(File(uri))
        return doc
    }


    fun buildDoc(file: File): Doc {
        val content = file.readText()
        return Doc(file.name, content, file.extension, file.totalSpace)
    }
}