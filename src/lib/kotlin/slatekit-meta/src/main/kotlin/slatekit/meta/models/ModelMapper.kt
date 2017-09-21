/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.meta.models


import slatekit.common.Field
import slatekit.common.Mapper
import slatekit.common.Types
import slatekit.meta.Reflector
import slatekit.common.newline
import slatekit.common.records.Record
import slatekit.meta.KTypes
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure



/**
 * A mapper that can create a model from a source reader ( which can be a JDBC record set )
 * NOTES:
 * 1. can create a model that is a case class
 * 2. can create a model that is a regular class
 * @param _model
 */
open class ModelMapper(protected val _model: Model, protected val _settings: ModelMapperSettings = ModelMapperSettings()) : Mapper {


    /**
     * The model associated with this mapper.
     * @return
     */
    fun model(): Model = _model


    /**
     * Creates the entity/model expecting a 0 parameter constructor
     * @return
     */
    override fun createEntity(): Any? =
            _model.dataType?.let { type -> Reflector.create<Any>(type) }


    /**
     * Creates the entity/model with all the supplied constructor parameters (ideal for case classes)
     * @param args
     * @return
     */
    override fun createEntityWithArgs(args: List<Any?>?): Any =
            Reflector.createWithArgs(_model.dataType!!, args?.toTypedArray() ?: arrayOf())


    fun <T> copyWithId(id: Long, entity: T): T = entity

    /**
     * Maps all the parameters to a class that takes in all parameters in the constructor
     * This is ideally for Case Classes, allowing the representation of models as immutable
     * case classes
     * @param record
     * @return
     */
    override fun mapFrom(record: Record): Any? {
        return if (_model.any && _model.dataType != null) {
            _model.dataType.let { tpe ->
                if (Reflector.isDataClass(tpe)) {
                    mapFromToValType(record)
                }
                else
                    mapFromToVarType(record)
            }
        }
        else
            null
    }


    /**
     * Maps all the parameters to a class that takes in all parameters in the constructor
     * This is ideally for Case Classes, allowing the representation of models as immutable
     * case classes
     * @param record
     * @return
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun mapFromToValType(record: Record): Any? {
        return if (_model.any) {
            val isUTC = _settings.persisteUTCDate
            val data = _model.fields.map { mapping ->
                val colName = mapping.storedName

                val dataValue = when (mapping.dataType) {
                    KTypes.KStringClass        -> record.getString(colName)
                    KTypes.KBoolClass          -> record.getBool(colName)
                    KTypes.KShortClass         -> record.getShort(colName)
                    KTypes.KIntClass           -> record.getInt(colName)
                    KTypes.KLongClass          -> record.getLong(colName)
                    KTypes.KFloatClass         -> record.getFloat(colName)
                    KTypes.KDoubleClass        -> record.getDouble(colName)
                    KTypes.KLocalDateClass     -> record.getLocalDate(colName)
                    KTypes.KLocalTimeClass     -> record.getLocalTime(colName)
                    KTypes.KLocalDateTimeClass -> if(isUTC) record.getLocalDateTimeFromUTC(colName) else record.getLocalDateTime(colName)
                    KTypes.KZonedDateTimeClass -> if(isUTC) record.getZonedDateTimeLocalFromUTC(colName) else record.getZonedDateTime(colName)
                    KTypes.KDateTimeClass      -> if(isUTC) record.getDateTimeLocalFromUTC(colName)      else record.getDateTime(colName)
                    KTypes.KInstantClass       -> record.getInstant(colName)
                    else                     -> record.getString(colName)
                }
                dataValue
            }
            val entity = createEntityWithArgs(data)
            entity
        }
        else
            null
    }

    /**
     * Maps all the parameters to a class that supports vars as fields.
     * While this is NOT recommended, it is still supported.
     * case classes
     * @param record
     * @return
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun mapFromToVarType(record: Record): Any? {
        return if (_model.any) {

            val isUTC = _settings.persisteUTCDate
            val entity: Any? = createEntity()
            _model.fields.forEach { mapping ->
                val colName = mapping.storedName

                val dataValue = when (mapping.dataType) {
                    KTypes.KStringClass        -> record.getString(colName)
                    KTypes.KBoolClass          -> record.getBool(colName)
                    KTypes.KShortClass         -> record.getShort(colName)
                    KTypes.KIntClass           -> record.getInt(colName)
                    KTypes.KLongClass          -> record.getLong(colName)
                    KTypes.KFloatClass         -> record.getFloat(colName)
                    KTypes.KDoubleClass        -> record.getDouble(colName)
                    KTypes.KLocalDateClass     -> record.getLocalDate(colName)
                    KTypes.KLocalTimeClass     -> record.getLocalTime(colName)
                    KTypes.KLocalDateTimeClass -> if(isUTC) record.getLocalDateTimeFromUTC(colName) else record.getLocalDateTime(colName)
                    KTypes.KZonedDateTimeClass -> if(isUTC) record.getZonedDateTimeLocalFromUTC(colName) else record.getZonedDateTime(colName)
                    KTypes.KDateTimeClass      -> if(isUTC) record.getDateTimeLocalFromUTC(colName)      else record.getDateTime(colName)
                    else                       -> record.getString(colName)
                }
                Reflector.setFieldValue(entity!!, mapping.name, dataValue)
            }
            entity
        }
        else
            null
    }


    override fun toString(): String =
            _model.fields.fold("", { s, field -> s + field.toString() + newline })


    companion object MapperCompanion {

        /**
         * Builds a schema ( Model ) from the Class/Type supplied.
         * NOTE: The mapper then works off the Model class for to/from mapping of data to model.
         * @param dataType
         * @return
         */
        fun loadSchema(dataType: KClass<*>, idFieldName:String? = null): Model {
            val modelName = dataType.simpleName!!
            val modelNameFull = dataType.qualifiedName!!

            // Now add all the fields.
            val matchedFields = Reflector.getAnnotatedProps<Field>(dataType, Field::class)

            val fields = mutableListOf<ModelField>()
            //val fieldId = ModelField.id("id", Long::class)
            //fields.add(fieldId)

            // Loop through each field
            matchedFields.forEach { matchedField ->
                matchedField.second?.let { anno ->
                    //if (anno.name != "id") {
                        val cat = idFieldName?.let { "id" } ?: ""
                        val name = if (anno.name.isNullOrEmpty()) matchedField.first.name else anno.name
                        val required = anno.required
                        val length = anno.length
                        val fieldType = matchedField.first.returnType.jvmErasure
                        fields.add(ModelField.build(name = name, dataType = fieldType, isRequired = required, maxLength = length, cat = cat))
                    //}
                }
            }

            val model = Model(modelName, modelNameFull, dataType, _propList = fields.toList())
            return model
        }
    }
}