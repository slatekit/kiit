package slatekit.common.smartvalues

/**
 * Email as a [SmartValue]
 */
data class Email internal constructor(val value:String) : SmartValued(metadata) {

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
            minLength = 6,
            maxLength = 30,
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


/**
 * Phone as a [SmartValue]
 */
data class PhoneUS internal constructor(val value:String) : SmartValued(metadata ) {

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
    companion object : SmartCreation<PhoneUS>() {

        /**
         * Metadata about the [SmartValue]
         */
        override val metadata = SmartMetadata(
                name = "PhoneUS",
                desc = "United States Phone Format",
                required = true,
                minLength = 10,
                maxLength = 14,
                examples = listOf("1234567890", "11234567890", "123-456-7890", "1-234-567-8901"),
                formats = listOf("xxxxxxxxxx", "xxxxxxxxxxx", "xxx-xxx-xxxx", "x-xxx-xxx-xxxx"),
                expressions = listOf(
                        """\d{10}""",
                        """\d{11}""",
                        """\d{3}[-]?\d{3}[-]?\d{4}""",
                        """\d{1}[-]?\d{3}[-]?\d{3}[-]?\d{4}"""
                )
        )

        /**
         * Creates an instance of the [PhoneUS]
         */
        override fun create(text: String): PhoneUS = PhoneUS(text)
    }
}