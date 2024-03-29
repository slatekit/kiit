package kiit.common.utils

/**
 * Represents an exported data set
 * @param version: Version number of the export   e.g. 1.0
 * @param type   : Type of data exported          e.g. "app.core.TodoItem"
 * @param path   : Indicate it was obtained       e.g. getById | getAll
 * @param source : Source/Origin of the data      e.g. client  | server
 * @param tag    : Unique id of this export       e.g. d454be34-3e52-456b-b5d3-fc182abffdc7
 * @param format : Format of the exported data    e.g. json | csv | xml
 * @param size   : Size of the exported items     e.g. 200
 * @param data   : Exported data serialized       e.g. JSON | CSV string
 * @param raw    : Raw typed non-serialized data  e.g. List<TodoItem>
 */
data class Export<out T>(
    @JvmField val version : String,
    @JvmField val type    : String,
    @JvmField val path    : String,
    @JvmField val source  : String,
    @JvmField val tag     : String,
    @JvmField val format  : String,
    @JvmField val size    : Int   ,
    @JvmField val data    : String,
    @JvmField val raw     : T
)