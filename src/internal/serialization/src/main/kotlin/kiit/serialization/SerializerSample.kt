package kiit.serialization

import kiit.common.DateTimes
import kiit.common.ext.local
import kiit.meta.KTypes
import kiit.meta.Reflector
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType

/**
 * Created by kishorereddy on 6/3/17.
 */
class SerializerSample(
    objectSerializer: ((Serializer, Any, Int) -> Unit)? = null,
    isoDates: Boolean = false
)
    : Serializer(objectSerializer, isoDates) {

    override val standardizeResult = true

    /**
     * serializes an object, factoring in a root item.
     */
    fun serializeParams(s: List<KParameter>): String {
        buff = StringBuilder()

        // Begin
        onContainerStart(s, ParentType.MAP_TYPE, 0)

        // Pairs
        s.forEachIndexed { index, entry -> onListItem(s, 0, index, entry) }

        // End
        onContainerEnd(s, ParentType.MAP_TYPE, 0)

        val text = buff.toString()
        return text
    }

    /**
     * Recursive serializer for a value of basic types.
     * Used for printing items to the console
     * in various places and components.
     * e.g. the CLI / Shell
     */
    override fun serializeValue(s: Any?, depth: Int) {
        when (s) {
            is KParameter -> serializeParameter(s, depth)
            is KProperty<*> -> serializeProperty(s, depth)
            else -> super.serializeValue(s, depth)
        }
    }

    fun serializeParameter(parameter: KParameter, depth: Int) {
        val tpe = parameter.type
        buff.append("\"" + parameter.name + "\" : ")
        serializerType(parameter, tpe, depth)
    }

    fun serializeProperty(property: KProperty<*>, depth: Int) {
        val tpe = property.returnType
        serializerType(property, tpe, depth)
    }

    fun serializerType(parent: Any, tpe: KType, depth: Int) {
        when (tpe) {
            // Basic types
            KTypes.KStringType -> buff.append("\"abc\"")
            KTypes.KBoolType -> buff.append(true)
            KTypes.KShortType -> buff.append(0.toShort())
            KTypes.KIntType -> buff.append(10)
            KTypes.KLongType -> buff.append(100L)
            KTypes.KFloatType -> buff.append(10.0.toFloat())
            KTypes.KDoubleType -> buff.append(10.00)
            KTypes.KDateTimeType -> buff.append("\"" + DateTimes.of(2017, 8, 20) + "\"")
            KTypes.KLocalDateType -> buff.append("\"" + DateTimes.of(2017, 8, 20).local().toLocalDate() + "\"")
            KTypes.KLocalTimeType -> buff.append("\"" + DateTimes.of(2017, 8, 20).local().toLocalTime() + "\"")
            KTypes.KLocalDateTimeType -> buff.append("\"" + DateTimes.of(2017, 8, 20).local() + "\"")
            KTypes.KZonedDateTimeType -> buff.append("\"" + DateTimes.of(2017, 8, 20) + "\"")
            KTypes.KInstantType -> buff.append("\"" + DateTimes.of(2017, 8, 20).toInstant() + "\"")
            KTypes.KDocType -> buff.append("\"user://myapp/conf/abc.conf\"")
            KTypes.KVarsType -> buff.append("\"a=1,b=2,c=3\"")
            KTypes.KSmartValueType -> buff.append("\"123-456-7890\"")
            KTypes.KDecStringType -> buff.append("\"ALK342481SFA\"")
            KTypes.KDecIntType -> buff.append("\"ALK342481SFA\"")
            KTypes.KDecLongType -> buff.append("\"ALK342481SFA\"")
            KTypes.KDecDoubleType -> buff.append("\"ALK342481SFA\"")
            else -> serializeObject(parent, tpe, depth)
        }
    }

    /**
     * recursive serialization for a object.
     *
     * @param item: The object to serialize
     * @param serializer: The serializer to serialize a value to a string
     * @param delimiter: The delimiter to use between key/value pairs
     */
    fun serializeObject(parent: Any, ktype: KType, depth: Int) {
        // Handle enum
        if (Reflector.isSlateKitEnum(ktype.classifier as KClass<*>)) {
            val enumVal = Reflector.getEnumSample(ktype.classifier as KClass<*>)
            // serializerType(parent, KTypes.KIntType, depth)
            buff.append(enumVal)
            return
        }

        // Begin
        onContainerStart(ktype, ParentType.OBJECT_TYPE, depth)

        // Get fields
        val kcls = ktype.classifier as KClass<*>
        val fields = Reflector.getProperties(kcls)

        // Standardize the display of the props
        val maxLen = if (standardizeWidth) {
            fields.maxByOrNull { it.name.length }?.name?.length ?: 0
        } else {
            0
        }

        fields.forEachIndexed { index, field ->
            // Get name/value
            val propName = field.name.trim()

            // Standardized width
            val finalPropName = if (standardizeWidth) {
                propName.padEnd(maxLen)
            } else {
                propName
            }
            onMapItem(ktype, depth, index, finalPropName, field)
        }

        // End
        onContainerEnd(ktype, ParentType.OBJECT_TYPE, depth)
    }
}
