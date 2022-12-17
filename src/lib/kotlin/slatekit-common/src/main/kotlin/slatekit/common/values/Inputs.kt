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

package slatekit.common.values

import slatekit.common.convert.Conversions
import slatekit.common.ext.splitToMapOfType


interface InputsUpdateable {
    // Immutable add
    fun add(key: String, value: Any): Inputs
}

/**
 * Interface to support reading inputs from multiple sources:
 * 1. command line arguments
 * 2. config settings
 * 3. http requests
 * 4. in-memory settings
 *
 * NOTE: This allows for abstracting the input source to accommodate
 * Slate Kit protocol independent APIs
 */
interface Inputs : Gets {

    // Get values for core value types, must be implemented in derived classes
    val raw: Any

    fun get(key: String): Any?
    fun containsKey(key: String): Boolean
    fun size(): Int


    // Get list and maps
    /**
     * gets a list of items of the type supplied.
     * NOTE: derived classes should override this. e.g. HttpInputs in kiit.server
     * @param key
     * @param tpe
     * @return
     */
    fun getList(key: String, tpe: Class<*>): List<Any> {
        val converter = Conversions.converterFor(tpe)
        val input = get(key)
        return input?.let { inputVal ->

            val result = when (inputVal) {
                "null" -> listOf()
                "\"\"" -> listOf()
                is String -> inputVal.toString().split(',').toList().map(converter)
                is List<*> -> (input as List<*>).map { it as Any }
                else -> listOf()
            }
            result
        } ?: listOf()
    }

    /**
     * gets a map of items of the type supplied.
     * NOTE: derived classes should override this. e.g. HttpInputs in kiit.server
     * @param key
     * @return
     */
    fun getMap(key: String, tpeKey: Class<*>, tpeVal: Class<*>): Map<*, *> {
        val keyConverter = Conversions.converterFor(tpeKey)
        val valConverter = Conversions.converterFor(tpeVal)
        val input = get(key)
        val emptyMap = mapOf<Any, Any>()
        return input?.let { inputVal ->

            val result = when (inputVal) {
                "null"       -> emptyMap
                "\"\""       -> emptyMap
                is String    -> inputVal.toString().splitToMapOfType(',', true, '=', keyConverter, valConverter)
                is Map<*, *> -> inputVal
                else -> emptyMap
            }
            result
        } ?: mapOf<Any, Any>()
    }

    override fun <T> getOrNull(key: String, fetcher: (String) -> T): T? {
        return if (containsKey(key)) {
            val v = get(key)
            v?.let { fetcher(key) }
        } else {
            null
        }
    }
    override fun <T> getOrElse(key: String, fetcher: (String) -> T, default: T): T = if (containsKey(key)) fetcher(key) else default
}