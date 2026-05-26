package kiit.codes

/**
 * Represents a typed status or error code with a numeric value, name, and description.
 * Used by kiit-result for structured success and failure classification.
 */
interface Code {
    val value: Int
    val name: String
    val desc: String
}
