package slatekit.common.smartvalues

/**
 * Email as a [SmartValue]
 */
data class Email internal constructor(val value:String) : SmartString<Email>(true, 10, 20, metadata ) {

    /**
     * Construction of this [Email] as a [SmartValue] is safely
     * prevented by relying on the Companion object which must
     * implement the [SmartCreation] interface.
     *
     * Design goals:
     * 1. Provides ways to create the SmartValue safely
     * 2. Provides ways to validate the SmartValue
     * 3. Prevent construction of a SmartValue in the constructor
     * 4. Provide convenient methods for Companion objects
     */
    companion object : SmartCreation<Email>() {

        /**
         * Metadata about the [SmartValue]
         */
        override val metadata = SmartMetadata(
            name = "Email",
            desc = "Email Address",
            required = true,
            examples = listOf("user@abc.com"),
            formats = listOf("xxxx@xxxxxxx.xx"),
            expressions = listOf("""([\w\$\.\-_]+)@([\w\.]+)""")
        )

        /**
         * Creates an instance of the [Email]
         */
        override fun create(text: String): Email = Email(text)
    }
}