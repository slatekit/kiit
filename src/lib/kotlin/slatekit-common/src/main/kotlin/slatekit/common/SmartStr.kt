package slatekit.common


/**
 * "Smart" string class
 *
 * FEATURES:
 * 1. flag indicated required / not required
 * 2. list of examples
 * 3. list of formats
 * 4. min/max length
 * 5. reg ex expressions
 * 6. validation built-in
 * 7. methods to get example
 *
 * NOTE:
 * 1. This is intended to replace method/function parameters
 * 2. Validity is automatically calculated on initialization
 * 3. This is used to provide additional type-safety on strings
 *
 * USECASES:
 * 1. Strongly-typed strings
 * 2. As parameters for Slate Kit APIs
 *
 * WARNING:
 * 1. It is not suggested to use this excessively, as it a heavy object
 * 2. E.g. Do not make this a property on small objects in lists
 */
abstract class SmartString (
        val text      : String,
        val required  : Boolean,
        val minLength : Int,
        val maxLength : Int,
        val expressions:List<String> = listOf()
)
{
    /**
     * Name for the smart string e.g. "PhoneUS"
     */
    abstract val name:String


    /**
     * Description of the smart string e.g. "United States Phone number format"
     */
    abstract val desc:String


    /**
     * Examples of smart string e.g. [ "1234567890", "123-456-7890" ]
     */
    abstract val examples:List<String>


    /**
     * Examples of the formats e.g. [ "xxxxxxxxxx", "xxx-xxx-xxxx" ]
     */
    abstract val formats:List<String>


    /**
     * Used internally to determine validatity of string on construction
     */
    val result:Pair<Boolean,Int> = validate(text, required, expressions)


    /**
     * whether or not this is valid with respect to its allowed patterns
     */
    val isValid:Boolean get() = result.first


    /**
     * The matched example associated with the value e.g.
     */
    val matched:String  get() = if(result.first) examples[result.second] else ""


    /**
     * whether or not htis is empty
     */
    val isEmpty = text.isNullOrEmpty()


    /**
     * the length of the text
     */
    val length = text.length


    /**
     * gets the char at the index position
     */
    operator fun get(index:Int):Char = text[index]


    /**
     * Compares this text with the one supplied
     */
    operator fun compareTo(other:String):Int = text.compareTo(other)


    /**
     * Gets an example of the string.
     */
    fun example():String = examples.first()


    override fun toString(): String = text


    /**
     * Validates the text supplied.
     * NOTE: While this smart string is auto-validated on construction,
     * an instance of this Smart String can be used as a Prototype to validate
     * other strings.
     */
    fun validate(txt:String):Boolean = validate(txt, required, expressions).first


    companion object {

        /**
         * Validates the text, used for construction of the smart string.
         */
        @JvmStatic
        fun validate(text:String, required:Boolean, expressions:List<String>): Pair<Boolean, Int> {
            val isEmpty = text.isNullOrEmpty()
            return if(isEmpty) {
                Pair(!required,-1)
            }
            else {
                val ndx = expressions.indexOfFirst { pattern -> Regex(pattern).matches(text) }
                Pair(ndx >= 0, ndx)
            }
        }
    }
}