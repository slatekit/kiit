package kiit.apis.routes

import kiit.common.ext.tail
import kiit.common.values.Metadata
import kiit.requests.Request
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType

data class Call(
    val klass: KClass<*>,
    val member: KCallable<*>,
    val instance: Any
) {
    /**
     * All the parameters of the function, this includes:
     *
     * 1. 0th instance parameter for kotlin
     * 2. a possible Request
     * 3. a possible Meta
     * 4. actual parameters for the method
     */
    private val paramsAll = member.parameters

    /**
     * All the function specific parameters WITHOUT references to
     * the following: These are the parameters that can be
     * discovered, documented, validated against
     *
     * 1. 0th instance parameter for kotlin
     */
    val params:List<KParameter> = if (paramsAll.size <= 1) listOf() else paramsAll.tail()

    /**
     * Whether the action has any arguments.
     */
    val hasArgs = !params.isEmpty()


    fun isSingleDefaultedArg(): Boolean {
        return if (!hasArgs || params.size > 1) {
            false
        } else {
            params.isNotEmpty() && params[0].isOptional
        }
    }

    fun isSingleArg(): Boolean = hasArgs && params.size == 1

    companion object {

        @JvmStatic
        val TypeRequest = Request::class.createType()

        @JvmStatic
        val TypeMeta = Metadata::class.createType()
    }
}
