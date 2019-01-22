package slatekit.common.ids

/**
 * Interface for generating, validation, parsing unique ids
 */
interface Ids {

    fun create(): String


    fun parse(id: String): String


    fun isValid(id:String):Boolean


    fun split(id:String):Array<String>
}