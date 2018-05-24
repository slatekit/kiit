package slatekit.common

/**
 * Generic interface for a Enum with both
 * 1. name ( automatically provided by system )
 * 2. a numeric value
 */
interface EnumLike {
    val value:Int
    val name :String
}


/**
 * Provides Dynamic/Reflection based support for checking/parsing Enums at Runtime
 *
 * @example
 * Sample declaration
 *
 *    enum class StatusEnum(override val value:Int) : EnumLike {
 *        Pending(0),
 *        Active (1),
 *        Blocked(2);
 *
 *
 *        companion object : EnumSupport()  {
 *
 *            override fun all(): Array<EnumLike> {
 *                return arrayOf(Pending, Active, Blocked)
 *            }
 *        }
 *    }
 *
 *
 * @see
 * Reflector.getEnumValue(StatusEnum::class, "Active")
 * Reflector.getEnumValue(StatusEnum::class, 1)
 *
 *
 * @notes:
 * This becomes useful when you do not know the class of the Enum at compile time.
 * e.g.
 * 1. Mapping of database records to models
 * 2. Serialization / Deserialization
 */
abstract class EnumSupport  {
    open fun parse(t: String): EnumLike {
        val member = all().find { it.name == t }
        val first = member ?: throw Exception("Unexpected value for Enum : $t")
        return first
    }


    open fun convert(i:Int): EnumLike {
        val member = all().find { it.value == i }
        val first = member ?: throw Exception("Unexpected value for Enum : $i")
        return first
    }


    abstract fun all(): Array<EnumLike>
}