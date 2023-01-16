package kiit.utils.smartvalues

import kiit.results.*
import kiit.results.builders.Tries

abstract class SmartCreation<T : SmartValue> {

    /**
     * Metadata about the smart value used for validation/creation
     */
    abstract val metadata: SmartMetadata

    /**
     * Creates an instance of the smart value
     */
    protected abstract fun create(text: String): T

    /**
     * Creates the SmartString if valid, throws an exception otherwise
     * NOTE: This should be avoided in favor of the more functional
     * [outcome] and [attempt] methods below. Those rely on the
     * [kiit.results.Result] component however.
     * This is here for completeness and to allow for various approaches
     */
    fun of(text: String): T {
        val result = validate(text)
        return when (result.first) {
            true -> create(text)
            false -> throw Exception("Invalid value: $text. Example: ${metadata.example}, Format: ${metadata.format}")
        }
    }

    /**
     * Functional version to return an Outcome<T> = kiit.results.Result<T, Err>
     */
    fun outcome(text: String?): Outcome<T> {
        val result = validate(text)
        return when (result.first) {
            true -> Success(create(text ?: ""))
            false -> Failure(Err.of("Invalid value: $text. Example: ${metadata.example}, Format: ${metadata.format}"))
        }
    }

    /**
     * * Functional version to return an Try<T> = kiit.results.Result<T, Exception>
     */
    fun attempt(text: String?): Try<T> {
        return Tries.of {
            val result = validate(text)
            when (result.first) {
                true -> create(text ?: "")
                false -> throw Exception("Invalid value: $text. Example: ${metadata.example}, Format: ${metadata.format}")
            }
        }
    }

    /**
     * Provides a simple check of validity
     */
    fun isValid(text: String?): Boolean {
        return this.validate(text).first
    }

    /**
     * Validates the text, used for construction of the smart string.
     */
    fun validate(text: String?): Pair<Boolean, Int> {
        val required = metadata.required
        val expressions = metadata.expressions
        val isEmpty = text.isNullOrEmpty()
        return if (isEmpty) {
            Pair(!required, -1)
        } else {
            val ndx = expressions.indexOfFirst { pattern -> Regex(pattern).matches(text ?: "") }
            Pair(ndx >= 0, ndx)
        }
    }
}
