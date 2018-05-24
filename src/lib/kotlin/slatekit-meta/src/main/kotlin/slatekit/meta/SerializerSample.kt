package slatekit.meta

import slatekit.common.DateTime
import slatekit.common.EnumLike
import slatekit.common.Serializer
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType


/**
 * Created by kishorereddy on 6/3/17.
 */
class SerializerSample(objectSerializer: ((Serializer, Any, Int) -> Unit)? = null,
                       isoDates:Boolean = false)
    : Serializer(objectSerializer, isoDates) {

    override val standardizeResult = true


    /**
     * serializes an object, factoring in a root item.
     */
    fun serializeParams(s: List<KParameter>): String {
        _buff = StringBuilder()


        // Begin
        onContainerStart(s, ParentType.MAP_TYPE, 0)

        // Pairs
        s.forEachIndexed { index, entry -> onListItem(s, 0, index, entry)}

        // End
        onContainerEnd(s, ParentType.MAP_TYPE, 0)

        val text = _buff.toString()
        return text
    }


    /**
     * Recursive serializer for a value of basic types.
     * Used for printing items to the console
     * in various places and components.
     * e.g. the CLI / Shell
     */
    override fun serializeValue(s: Any?, depth: Int): Unit {
        when (s) {
            is KParameter   -> serializeParameter(s, depth)
            is KProperty<*> -> serializeProperty(s, depth)
            else            -> super.serializeValue(s, depth)
        }
    }


    fun serializeParameter(parameter: KParameter, depth:Int): Unit {
        val tpe = parameter.type
        _buff.append("\"" + parameter.name + "\" : ")
        serializerType(parameter, tpe, depth)
    }


    fun serializeProperty(property: KProperty<*>, depth:Int): Unit {
        val tpe = property.returnType
        serializerType(property, tpe, depth)
    }


    fun serializerType(parent:Any, tpe: KType, depth:Int): Unit {
        when (tpe) {
            // Basic types
            KTypes.KStringType        -> _buff.append( "\"abc\"")
            KTypes.KBoolType          -> _buff.append( true)
            KTypes.KShortType         -> _buff.append( 0.toShort())
            KTypes.KIntType           -> _buff.append( 10)
            KTypes.KLongType          -> _buff.append( 100L)
            KTypes.KFloatType         -> _buff.append( 10.0.toFloat())
            KTypes.KDoubleType        -> _buff.append( 10.00)
            KTypes.KDateTimeType      -> _buff.append( "\"" + DateTime.of(2017, 8, 20) + "\"")
            KTypes.KLocalDateType     -> _buff.append( "\"" + DateTime.of(2017, 8, 20).local().toLocalDate() + "\"")
            KTypes.KLocalTimeType     -> _buff.append( "\"" + DateTime.of(2017, 8, 20).local().toLocalTime() + "\"")
            KTypes.KLocalDateTimeType -> _buff.append( "\"" + DateTime.of(2017, 8, 20).local() + "\"")
            KTypes.KZonedDateTimeType -> _buff.append( "\"" + DateTime.of(2017, 8, 20).raw + "\"")
            KTypes.KInstantType       -> _buff.append( "\"" + DateTime.of(2017, 8, 20).raw.toInstant() + "\"")
            KTypes.KDocType           -> _buff.append( "\"user://myapp/conf/abc.conf\"")
            KTypes.KVarsType          -> _buff.append( "\"a=1,b=2,c=3\"")
            KTypes.KSmartStringType   -> _buff.append( "\"123-456-7890\"")
            KTypes.KDecStringType     -> _buff.append( "\"ALK342481SFA\"")
            KTypes.KDecIntType        -> _buff.append( "\"ALK342481SFA\"")
            KTypes.KDecLongType       -> _buff.append( "\"ALK342481SFA\"")
            KTypes.KDecDoubleType     -> _buff.append( "\"ALK342481SFA\"")
            else                      -> serializeObject(parent, tpe, depth)
        }
    }


    /**
     * recursive serialization for a object.
     *
     * @param item: The object to serialize
     * @param serializer: The serializer to serialize a value to a string
     * @param delimiter: The delimiter to use between key/value pairs
     */
    fun serializeObject(parent:Any, ktype: KType, depth: Int): Unit {
        // Handle enum
        if(Reflector.isSlateKitEnum(ktype.classifier as KClass<*>)){
            val enumVal = Reflector.getEnumSample(ktype.classifier as KClass<*>)
            //serializerType(parent, KTypes.KIntType, depth)
            _buff.append( enumVal)
            return
        }

        // Begin
        onContainerStart(ktype, ParentType.OBJECT_TYPE, depth)

        // Get fields
        val kcls = ktype.classifier as KClass<*>
        val fields = Reflector.getProperties(kcls)

        // Standardize the display of the props
        val maxLen = if (standardizeWidth) {
            fields.maxBy { it.name.length }?.name?.length ?: 0
        }
        else {
            0
        }

        fields.forEachIndexed { index, field ->
            // Get name/value
            val propName = field.name.trim()

            // Standardized width
            val finalPropName = if (standardizeWidth) {
                propName.padEnd(maxLen)
            }
            else {
                propName
            }
            onMapItem(ktype, depth, index, finalPropName, field)
        }

        // End
        onContainerEnd(ktype, ParentType.OBJECT_TYPE, depth)
    }
}