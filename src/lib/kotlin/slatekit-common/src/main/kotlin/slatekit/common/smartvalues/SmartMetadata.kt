package slatekit.common.smartvalues


/**
 * @param name    : Name for the smart string e.g. "PhoneUS"
 * @param desc    : Description of the smart string e.g. "United States Phone number format"
 * @param example : Single example value
 * @param examples: Examples of smart string e.g. [ "1234567890", "123-456-7890" ]
 * @param formats : Examples of the formats e.g. [ "xxxxxxxxxx", "xxx-xxx-xxxx" ]
 */
data class SmartMetadata(
        val name: String,
        val desc: String,
        val required: Boolean,
        val examples: List<String>,
        val expressions:List<String>,
        val formats: List<String>
) {
    val example:String = examples.first()
    val format:String = formats.first()
}