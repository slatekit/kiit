package slatekit.results


/**
 * Interface to represent Status Codes which can
 * accurately represent a status with a code and message
 *
 * @sample :
 * { code: 4000, msg: "Invalid request" }
 * { code: 4001, msg: "Unauthorized"    }
 *
 * NOTE: A good example would be Http Status Codes
 */
interface Code {
    val code: Int
    val msg: String
}