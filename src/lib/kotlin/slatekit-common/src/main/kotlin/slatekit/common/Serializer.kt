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

import slatekit.common.serialization.Indenter
import java.time.*
import java.time.format.DateTimeFormatter

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
 * This is supplied to remove kotlin reflect as a dependency on SlateKit.common project.
 * Refer to slatekit.meta.serialization.serializeObject for a sample implementation.
 *
 */
open class Serializer(val objectSerializer: ((Serializer,Any,Int) -> Unit)? = null,
                      val isoDates:Boolean = false){

    open val standardizeWidth = false
    open val standardizeResult = false
    protected val _indenter = Indenter()
    protected var _buff = StringBuilder()
    protected val dateFormat    : DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    protected val timeFormat    : DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    protected val dateTimeFormat: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME //DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    enum class ParentType {
        ROOT_TYPE, LIST_TYPE, MAP_TYPE, OBJECT_TYPE
    }


    /**
     * serializes an object, factoring in a root item.
     */
    open fun serialize(s: Any?): String {
        _buff = StringBuilder()

        // Serialize
        serializeValue(s, 0)

        val text = _buff.toString()
        return text
    }


    /**
     * serializes an object, factoring in a root item.
     */
    open fun serializeDocument(s: Any?): String {
        _buff = StringBuilder()

        val root = s!!

        // Begin
        onContainerStart(root, ParentType.ROOT_TYPE, 0)

        // Serialize
        val value = serializeValue(root, 0)
        onMapItem(root, 0, 0, root.javaClass.simpleName!!, value)

        // End
        onContainerEnd(root, ParentType.ROOT_TYPE, 0)

        val text = _buff.toString()
        return text
    }


    /**
     * Recursive serializer for a value of basic types.
     * Used for printing items to the console
     * in various places and components.
     * e.g. the CLI / Shell
     */
    protected open fun serializeValue(s: Any?, depth: Int): Unit {
        when (s) {
            null             -> _buff.append("null")
            is Unit          -> _buff.append("null")
            is Char          -> _buff.append(serializeString(s.toString()))
            is String        -> _buff.append(serializeString(s))
            is Boolean       -> _buff.append(s.toString().toLowerCase())
            is Short         -> _buff.append(s.toString())
            is Int           -> _buff.append(s.toString())
            is Long          -> _buff.append(s.toString())
            is Float         -> _buff.append(s.toString())
            is Double        -> _buff.append(s.toString())
            is LocalDate     -> _buff.append("\"" + s.format(dateFormat) + "\"")
            is LocalTime     -> _buff.append("\"" + s.format(timeFormat) + "\"")
            is LocalDateTime -> _buff.append("\"" + s.format(dateTimeFormat) + "\"")
            is ZonedDateTime -> _buff.append("\"" + s.format(dateTimeFormat) + "\"")
            is Instant       -> _buff.append("\"" + LocalDateTime.ofInstant(s, ZoneId.systemDefault()).format(dateTimeFormat) + "\"")
            is DateTime      -> _buff.append("\"" + (if(isoDates) s.atUtc().format(dateTimeFormat) else s.format(dateTimeFormat)) + "\"")
            is Result<*>     -> serializeResult(s, depth)
            is List<*>       -> serializeList(s, depth + 1)
            is Map<*, *>     -> serializeMap(s, depth + 1)
            else             -> objectSerializer?.invoke(this, s, depth + 1) ?: "null"
        }
    }


    /**
     * recursive serialization for a list
     *
     * @param items: The items to serialize
     * @param serializer: The serializer to serialize a value to a string
     * @param delimiter: The delimiter to use between values
     */
    protected fun serializeList(item: List<*>, depth: Int): Unit {
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
    protected fun serializeMap(item: Map<*, *>, depth: Int): Unit {
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
    protected fun serializeResult(item: Result<*>, depth: Int): Unit {
        if (standardizeResult) {
            // Begin
            onContainerStart(item, ParentType.OBJECT_TYPE, depth)

            // Entry
            onMapItem(item, depth, 0, "success", item.success)
            onMapItem(item, depth, 1, "code", item.code)
            onMapItem(item, depth, 2, "msg", item.msg)
            onMapItem(item, depth, 3, "value", item.value)

            // End
            onContainerEnd(item, ParentType.OBJECT_TYPE, depth)
        }
        else {
            serializeValue(item.value, depth)
        }
    }


    /**
     * serializes a string value handling escape values
     */
    protected open fun serializeString(text: String): String {
        val result = when (text) {
            ""   -> "\"\""
            else -> "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
        }
        return result
    }


    /**
     * handler for when a container item has started
     */
    open fun onContainerStart(item: Any, type: ParentType, depth: Int): Unit {
        when (type) {
            ParentType.LIST_TYPE   -> _buff.append("[")
            ParentType.MAP_TYPE    -> _buff.append("{")
            ParentType.OBJECT_TYPE -> _buff.append("{")
            ParentType.ROOT_TYPE   -> _buff.append("{")
        }
    }


    /**
     * handle for when a container item has ended
     */
    open fun onContainerEnd(item: Any, type: ParentType, depth: Int): Unit {
        when (type) {
            ParentType.LIST_TYPE   -> _buff.append("]")
            ParentType.MAP_TYPE    -> _buff.append("}")
            ParentType.OBJECT_TYPE -> _buff.append("}")
            ParentType.ROOT_TYPE   -> _buff.append("}")
        }
    }


    open fun onMapItem(item: Any, depth: Int, pos: Int, key: String, value: Any?): Unit {
        if (pos > 0) {
            _buff.append(", ")
        }
        _buff.append("\"$key\" : ")
        serializeValue(value, depth)
    }


    protected open fun onListItem(item: Any, depth: Int, pos: Int, value: Any?): Unit {
        if (pos > 0) {
            _buff.append(", ")
        }
        serializeValue(value, depth)
    }
}
