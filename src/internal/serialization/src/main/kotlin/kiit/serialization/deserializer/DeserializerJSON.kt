package kiit.serialization.deserializer

import kiit.common.crypto.Encryptor
import kiit.common.values.Metadata
import kiit.meta.Conversion
import kiit.meta.JSONTransformer
import kiit.meta.KTypes
import kiit.meta.Reflector
import kiit.requests.InputArgs
import kiit.requests.Request
import kiit.results.Err
import kiit.results.ExceptionErr
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.createType

/**
 * De-serializes data ( as Inputs ) into the parameter types represented by rawParams
 *
 * DESIGN:
 * This custom serializer exists because the use cases are not (At the moment) supported by others such as
 * 1. GSON
 * 2. Jackson
 * 3. Moshi
 *
 * USE-CASES:
 * 1. A context object ( in this case the Request - API, CLI, etc ) is supplied to the deserializers
 * 2. Deserialization is passed the specific parameters ( list ) not a single value to deserialize
 */
open class DeserializerJSON(
    private val req:Request,
    private val enc: Encryptor? = null,
    private val decoders: Map<String, JSONTransformer<*>> = mapOf()
) : Deserializer<JSONObject> {

    private val conversion  = Conversion(this::convert)
    private val typeRequest = Request::class.createType()
    private val typeMeta    = Metadata::class.createType()
    private val deserializers = Deserializers(conversion, enc, decoders)
    private val basicTypes = mapOf(
        KTypes.KStringClass.qualifiedName!! to true,
        KTypes.KBoolClass.qualifiedName!! to true,
        KTypes.KShortClass.qualifiedName!! to true,
        KTypes.KIntClass.qualifiedName!! to true,
        KTypes.KLongClass.qualifiedName!! to true,
        KTypes.KFloatClass.qualifiedName!! to true,
        KTypes.KDoubleClass.qualifiedName!! to true,
        KTypes.KLocalDateClass.qualifiedName!! to true,
        KTypes.KLocalTimeClass.qualifiedName!! to true,
        KTypes.KLocalDateTimeClass.qualifiedName!! to true,
        KTypes.KZonedDateTimeClass.qualifiedName!! to true,
        KTypes.KDateTimeClass.qualifiedName!! to true,
        KTypes.KDecIntClass.qualifiedName!! to true,
        KTypes.KDecLongClass.qualifiedName!! to true,
        KTypes.KDecDoubleClass.qualifiedName!! to true,
        KTypes.KDecStringClass.qualifiedName!! to true,
        KTypes.KVarsClass.qualifiedName!! to true,
        KTypes.KDocClass.qualifiedName!! to true,
        KTypes.KUUIDClass.qualifiedName!! to true
    )


    /**
     * Deserializes the JSON text associated with the parameters supplied
     */
    open fun deserialize(parameters: List<KParameter>, text: String): Array<Any?> {
        val jsonObj = JSONParser().parse(text) as JSONObject
        return deserialize(parameters, jsonObj)
    }

    /**
     * converts the JSON object data into the instances of the parameter types
     * @param parameters: The parameter info to convert
     * @param source : The json object to containing the data
     */
    override fun deserialize(parameters: List<KParameter>, source: JSONObject): Array<Any?> {

        // Check each parameter to api call
        val inputs = (0 until parameters.size).map { index ->
            val parameter = parameters[index]
            deserialize(parameter, source)
        }
        return inputs.toTypedArray()
    }

    /**
     * converts data from the json object as an instance of the parameter type
     */
    override fun deserialize(parameter: KParameter, source: JSONObject): Any? {
        val paramName = parameter.name!!
        val paramType = parameter.type
        val data = source.get(paramName)
        val result = convert(source, data, paramName, paramType)
        return result
    }


    override fun deserialize(parameters: List<KParameter>): Array<Any?> {
        // Check each parameter to api call
        val source = req.data.raw as? JSONObject
        val inputs = mutableListOf<Any?>()
        for (ndx in 0 until parameters.size) {
            // Get each parameter to the method
            val parameter = parameters[ndx]
            val paramName = parameter.name!!
            val paramType = parameter.type
            val paramCls = paramType.classifier as KClass<*>
            val result:Any? = try {
                val isBasicType = basicTypes.containsKey(paramCls.qualifiedName)
                when(isBasicType) {
                    true -> {
                        val paramValue = req.data.get(paramName)
                        deserializers.basic.deserialize(req, inputs, paramValue, paramName, paramType)
                    }
                    false -> {
                        when(paramType) {
                            typeRequest -> req
                            typeMeta -> req.meta
                            else -> {
                                handleComplex(req, parameter, paramType, source, req.data.get(paramName))
                            }
                        }
                    }
                }
            }
            catch(ex:Exception) {
                val errValue = this.req.data.getStringOrNull(paramName)
                val errField = Err.on(paramName, errValue ?: "", "Invalid value", ex)
                val errList = Err.ErrorList(listOf(errField), ex.message ?: "Invalid value")
                throw ExceptionErr("Error while converting parameters", errList)
            }
            inputs.add(result)
        }

        return inputs.toTypedArray()
    }

    /**
     * converts
     */
    private fun convert(parent: Any, paramValue: Any?, paramName:String, paramType: KType): Any? {
        val cls = paramType.classifier as KClass<*>
        val result = when(basicTypes.containsKey(cls.qualifiedName)) {
            true -> deserializers.basic.deserialize(req, parent, paramValue, paramName, paramType)
            else -> handleComplex(parent, paramValue, paramName, paramType)
        }
        return result
    }


    /**
     * Handles building of a list from various source types
     * @param args
     * @param paramName
     * @return
     */
    private fun handleComplex(parent: Any, paramValue: Any?, paramName:String, paramType: KType): Any? {
        val cls = paramType.classifier as KClass<*>
        val fullName = cls.qualifiedName
        return if (cls == List::class) {
            deserializers.lists.deserialize(this.req, parent, paramValue, paramName, paramType)
        }
        // Case 2: Map
        else if (cls == Map::class) {
            deserializers.maps.deserialize(this.req, parent, paramValue, paramName, paramType)
        }
        // Case 3: Custom Decoders ( for custom types )
        else if (decoders.containsKey(fullName)) {
            deserializers.custom.deserialize(this.req, parent, paramValue, paramName, paramType)
        }
        // Case 4: Smart String ( e.g. PhoneUS, Email, SSN, ZipCode )
        else if (cls.supertypes.indexOf(KTypes.KSmartValueType) >= 0) {
            deserializers.smart.deserialize(this.req, parent, paramValue, paramName, paramType)
        }
        // Case 5: Slate Kit Enm
        else if (cls.supertypes.indexOf(KTypes.KEnumLikeType) >= 0) {
            deserializers.enums.deserialize(this.req, parent, paramValue, paramName, paramType)
        }
        // Case 6: Class / Object
        else {
            deserializers.objs.deserialize(this.req, parent, paramValue, paramName, paramType)!!
        }
    }

    /**
     * Handles building of a list from various source types
     * @return
     */
    private fun handleComplex(context: Request, parameter: KParameter, tpe: KType, jsonRaw: JSONObject?, raw: Any?): Any? {
        val data = context.data
        val paramName = parameter.name!!
        val cls = tpe.classifier as KClass<*>

        val result = if (cls.supertypes.indexOf(KTypes.KSmartValueType) >= 0) {
            deserializers.smart.deserialize(this.req, data, raw, paramName, tpe)
        } else if (cls.supertypes.indexOf(KTypes.KSmartValuedType) >= 0) {
            deserializers.smart.deserialize(this.req, data, raw, paramName, tpe)
        } else if (cls.supertypes.indexOf(KTypes.KEnumLikeType) >= 0) {
            val enumVal = data.get(paramName)
            Reflector.getEnumValue(cls, enumVal)
        } else if (jsonRaw == null) {
            // Case 1: List<*>
            if (cls == List::class) {
                val listType = tpe.arguments[0]!!.type!!
                val listCls = KTypes.getClassFromType(listType)
                data.getList(paramName, listCls.java)
            }
            // Case 2: Map<*,*>
            else if (cls == Map::class) {
                val tpeKey = tpe.arguments[0].type!!
                val tpeVal = tpe.arguments[1].type!!
                val clsKey = KTypes.getClassFromType(tpeKey)
                val clsVal = KTypes.getClassFromType(tpeVal)
                val emptyMap = mapOf<Any, Any>()
                data.getMap(paramName, clsKey.java, clsVal.java)
            }
            // Case 3: Smart String ( e.g. PhoneUS, Email, SSN, ZipCode )
            // Refer to kiit.common.types
            else if (cls.supertypes.indexOf(KTypes.KSmartValueType) >= 0) {
                deserializers.smart.deserialize(this.req, data, raw, paramName, tpe)
            }
            // Case 4: Object / Complex type
            else {
                val json = if (jsonRaw == null) {
                    val obj = JSONObject()
                    if (data is InputArgs) {
                        val map = data.map
                        map.entries.forEach { pair ->
                            obj.put(pair.key, pair.value)
                        }
                    }
                    obj
                } else jsonRaw
                deserialize(parameter, json)
            }
        } else {
            deserialize(parameter, jsonRaw!!)
        }
        return result
    }
}