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

package slatekit.common.mapper


import slatekit.common.*
import slatekit.common.models.ModelField
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmErasure


/**
 * A mapper that can create a model from a source reader ( which can be a JDBC record set )
 * NOTES:
 * 1. can create a model that is a case class
 * 2. can create a model that is a regular class
 * @param _model
 */
open class Mapper(protected val _model: Model, protected val _settings:MapperSettings = MapperSettings()) {


    /**
     * The model associated with this mapper.
     * @return
     */
    fun model(): Model = _model


    /**
     * Creates the entity/model expecting a 0 parameter constructor
     * @return
     */
    fun createEntity(): Any? =
            _model.dataType?.let { type -> Reflector.create<Any>(type) }


    /**
     * Creates the entity/model with all the supplied constructor parameters (ideal for case classes)
     * @param args
     * @return
     */
    fun createEntityWithArgs(args: List<Any?>?): Any =
            Reflector.createWithArgs(_model.dataType!!, args?.toTypedArray() ?: arrayOf())


    fun <T> copyWithId(id: Long, entity: T): T = entity

    /**
     * Maps all the parameters to a class that takes in all parameters in the constructor
     * This is ideally for Case Classes, allowing the representation of models as immutable
     * case classes
     * @param record
     * @return
     */
    fun mapFrom(record: MappedSourceReader): Any? {
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
    fun mapFromToValType(record: MappedSourceReader): Any? {
        return if (_model.any) {
            val isUTC = _settings.persisteUTCDate
            val data = _model.fields.map { mapping ->
                val colName = mapping.storedName

                val dataValue = when (mapping.dataType) {
                    Types.StringClass        -> record.getString(colName)
                    Types.BoolClass          -> record.getBool(colName)
                    Types.ShortClass         -> record.getShort(colName)
                    Types.IntClass           -> record.getInt(colName)
                    Types.LongClass          -> record.getLong(colName)
                    Types.FloatClass         -> record.getFloat(colName)
                    Types.DoubleClass        -> record.getDouble(colName)
                    Types.LocalDateClass     -> record.getLocalDate(colName)
                    Types.LocalTimeClass     -> record.getLocalTime(colName)
                    Types.LocalDateTimeClass -> if(isUTC) record.getLocalDateTimeFromUTC(colName) else record.getLocalDateTime(colName)
                    Types.ZonedDateTimeClass -> if(isUTC) record.getZonedDateTimeLocalFromUTC(colName) else record.getZonedDateTime(colName)
                    Types.DateTimeClass      -> if(isUTC) record.getDateTimeLocalFromUTC(colName)      else record.getDateTime(colName)
                    Types.InstantClass       -> record.getInstant(colName)
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
    fun mapFromToVarType(record: MappedSourceReader): Any? {
        return if (_model.any) {

            val isUTC = _settings.persisteUTCDate
            val entity: Any? = createEntity()
            _model.fields.forEach { mapping ->
                val colName = mapping.storedName

                val dataValue = when (mapping.dataType) {
                    Types.StringClass        -> record.getString(colName)
                    Types.BoolClass          -> record.getBool(colName)
                    Types.ShortClass         -> record.getShort(colName)
                    Types.IntClass           -> record.getInt(colName)
                    Types.LongClass          -> record.getLong(colName)
                    Types.FloatClass         -> record.getFloat(colName)
                    Types.DoubleClass        -> record.getDouble(colName)
                    Types.LocalDateClass     -> record.getLocalDate(colName)
                    Types.LocalTimeClass     -> record.getLocalTime(colName)
                    Types.LocalDateTimeClass -> if(isUTC) record.getLocalDateTimeFromUTC(colName) else record.getLocalDateTime(colName)
                    Types.ZonedDateTimeClass -> if(isUTC) record.getZonedDateTimeLocalFromUTC(colName) else record.getZonedDateTime(colName)
                    Types.DateTimeClass      -> if(isUTC) record.getDateTimeLocalFromUTC(colName)      else record.getDateTime(colName)
                    else                     -> record.getString(colName)
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
        fun loadSchema(dataType: KClass<*>): Model {
            val modelName = dataType.simpleName!!
            val modelNameFull = dataType.qualifiedName!!

            // Now add all the fields.
            val matchedFields = Reflector.getAnnotatedProps<Field>(dataType, Field::class)

            TODO.IMPROVE( "Handle other id types")
            val fieldId = ModelField.id("id", Long::class)
            val fields = mutableListOf<ModelField>()
            fields.add(fieldId)

            // Loop through each field
            matchedFields.forEach { matchedField ->
                matchedField.second?.let { anno ->
                    if (anno.name != "id") {

                        val name = if (anno.name.isNullOrEmpty()) matchedField.first.name else anno.name
                        val required = anno.required
                        val length = anno.length
                        val fieldType = matchedField.first.returnType.jvmErasure
                        fields.add(ModelField.build(name = name, dataType = fieldType, isRequired = required, maxLength = length))
                    }
                }
            }

            val model = Model(modelName, modelNameFull, dataType, _propList = fields.toList())
            return model
        }
    }
}