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

package kiit.serialization

import kiit.results.Result
import kiit.results.getOrElse
//import java.time.*
import org.threeten.bp.*
import org.threeten.bp.format.*
import kiit.common.ext.atUtc
import java.util.*

//import java.time.format.DateTimeFormatter

/**
 * Created by kishorereddy on 6/14/17.
 */

/**
 * General purpose simple serializer with templated methods for derived classes
 * to customize the serialization for csv, json, hocon etc.
 *
 * NOTE: This is mostly used for the output in the Shell/CLI console and the
 * serialization of entities/models for the ORM for logging purposes.
 *
 * @param objectSerializer: The function that can handle serialization of an option.
 * This is supplied to remove kotlin reflect as a dependency on kiit.common project.
 * Refer to kiit.meta.serialization.serializeObject for a sample implementation.
 *
 */
open class Serializer(
        val objectSerializer: ((Serializer, Any, Int) -> Unit)? = null,
        val isoDates: Boolean = false
) {

    open val standardizeWidth = false
    open val standardizeResult = false
    protected val indenter = Indenter()
    protected var buff = StringBuilder()
    private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val timeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    private val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME // DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    enum class ParentType {
        ROOT_TYPE, LIST_TYPE, MAP_TYPE, OBJECT_TYPE
    }

    /**
     * serializes an object, factoring in a root item.
     */
    open fun serialize(s: Any?): String {
        buff = StringBuilder()

        // Serialize
        serializeValue(s, 0)

        val text = buff.toString()
        return text
    }

    /**
     * serializes an object, factoring in a root item.
     */
    open fun serializeDocument(s: Any?): String {
        buff = StringBuilder()

        val root = s!!

        // Begin
        onContainerStart(root, ParentType.ROOT_TYPE, 0)

        // Serialize
        val value = serializeValue(root, 0)
        onMapItem(root, 0, 0, root.javaClass.simpleName!!, value)

        // End
        onContainerEnd(root, ParentType.ROOT_TYPE, 0)

        val text = buff.toString()
        return text
    }

    /**
     * Recursive serializer for a value of basic types.
     * Used for printing items to the console
     * in various places and components.
     * e.g. the CLI / Shell
     */
    open fun serializeValue(s: Any?, depth: Int) {
        when (s) {
            null -> buff.append("null")
            is Unit -> buff.append("null")
            is Char -> buff.append(serializeString(s.toString()))
            is String -> buff.append(serializeString(s))
            is Boolean -> buff.append(s.toString().toLowerCase())
            is Short -> buff.append(s.toString())
            is Int -> buff.append(s.toString())
            is Long -> buff.append(s.toString())
            is Float -> buff.append(s.toString())
            is Double -> buff.append(s.toString())
            is UUID -> buff.append(serializeString(s.toString()))
            is LocalDate -> buff.append("\"" + s.format(dateFormat) + "\"")
            is LocalTime -> buff.append("\"" + s.format(timeFormat) + "\"")
            is LocalDateTime -> buff.append("\"" + s.format(dateTimeFormat) + "\"")
            is Instant -> buff.append("\"" + LocalDateTime.ofInstant(s, ZoneId.systemDefault()).format(dateTimeFormat) + "\"")
            is ZonedDateTime -> buff.append("\"" + (if (isoDates) s.atUtc().format(dateTimeFormat) else s.format(dateTimeFormat)) + "\"")
            is Result<*, *> -> serializeResult(s, depth)
            is List<*> -> serializeList(s, depth + 1)
            is Map<*, *> -> serializeMap(s, depth + 1)
            is Exception -> buff.append(serializeString(s.message ?: ""))
            else -> objectSerializer?.invoke(this, s, depth + 1) ?: "null"
        }
    }

    /**
     * recursive serialization for a list
     *
     * @param items: The items to serialize
     * @param serializer: The serializer to serialize a value to a string
     * @param delimiter: The delimiter to use between values
     */
    protected fun serializeList(item: List<*>, depth: Int) {
        // Begin
        onContainerStart(item, ParentType.LIST_TYPE, depth)

        for (ndx in 0..item.size - 1) {
            val value = item[ndx]

            // Entry
            onListItem(item, depth, ndx, value)
        }

        // End
        onContainerEnd(item, ParentType.LIST_TYPE, depth)
    }

    /**
     * recursive serialization for a map.
     *
     * @param item: The map to serialize
     * @param serializer: The serializer to serialize a value to a string
     * @param delimiter: The delimiter to use between key/value pairs
     */
    protected fun serializeMap(item: Map<*, *>, depth: Int) {
        // Begin
        onContainerStart(item, ParentType.MAP_TYPE, depth)

        // Pairs
        item.entries.forEachIndexed { index, entry ->
            val key = entry.key?.let { k -> k.toString() } ?: ""
            val value = entry.value

            // Entry
            onMapItem(item, depth, index, key, value)
        }

        // End
        onContainerEnd(item, ParentType.MAP_TYPE, depth)
    }

    /**
     * recursive serialization for a object.
     *
     * @param item: The object to serialize
     * @param serializer: The serializer to serialize a value to a string
     * @param delimiter: The delimiter to use between key/value pairs
     */
    protected fun serializeResult(item: Result<*, *>, depth: Int) {
        if (standardizeResult) {
            // Begin
            onContainerStart(item, ParentType.OBJECT_TYPE, depth)

            // Entry
            onMapItem(item, depth, 0, "success", item.success)
            onMapItem(item, depth, 1, "code", item.code)
            onMapItem(item, depth, 2, "msg", item.desc)
            onMapItem(item, depth, 3, "value", item.getOrElse { null })

            // End
            onContainerEnd(item, ParentType.OBJECT_TYPE, depth)
        } else {
            serializeValue(item.getOrElse { null }, depth)
        }
    }

    /**
     * serializes a string value handling escape values
     */
    protected open fun serializeString(text: String): String {
        val result = when (text) {
            "" -> "\"\""
            else -> "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
        }
        return result
    }

    /**
     * handler for when a container item has started
     */
    open fun onContainerStart(item: Any, type: ParentType, depth: Int) {
        when (type) {
            ParentType.LIST_TYPE -> buff.append("[")
            ParentType.MAP_TYPE -> buff.append("{")
            ParentType.OBJECT_TYPE -> buff.append("{")
            ParentType.ROOT_TYPE -> buff.append("{")
        }
    }

    /**
     * handle for when a container item has ended
     */
    open fun onContainerEnd(item: Any, type: ParentType, depth: Int) {
        when (type) {
            ParentType.LIST_TYPE -> buff.append("]")
            ParentType.MAP_TYPE -> buff.append("}")
            ParentType.OBJECT_TYPE -> buff.append("}")
            ParentType.ROOT_TYPE -> buff.append("}")
        }
    }

    open fun onMapItem(item: Any, depth: Int, pos: Int, key: String, value: Any?) {
        if (pos > 0) {
            buff.append(", ")
        }
        buff.append("\"$key\" : ")
        serializeValue(value, depth)
    }

    protected open fun onListItem(item: Any, depth: Int, pos: Int, value: Any?) {
        if (pos > 0) {
            buff.append(", ")
        }
        serializeValue(value, depth)
    }
}
