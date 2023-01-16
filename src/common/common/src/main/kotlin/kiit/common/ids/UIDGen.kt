package kiit.common.ids

/**
 * Interface for generating, validation, parsing unique ids
 */
interface UIDGen<T> where T:UID {

    fun create(): T

    fun create(context:String?): T

    fun parse(id: String): T

    fun isValid(id:String):Boolean = try { parse(id); true } catch (ex:Exception) { false }

    fun split(id:String):Array<String>
}