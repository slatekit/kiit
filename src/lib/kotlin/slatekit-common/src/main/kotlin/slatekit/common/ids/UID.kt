package slatekit.common.ids


/**
 * Unique ID ( can be UUID V4, ULID, etc )
 */
interface UID {
    val name : String
    val value: String
}