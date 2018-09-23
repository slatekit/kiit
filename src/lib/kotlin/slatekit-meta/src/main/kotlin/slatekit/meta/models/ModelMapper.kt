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


import slatekit.common.*
import slatekit.common.encrypt.Encryptor
import slatekit.meta.Reflector
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
open class ModelMapper(protected val _model: Model,
                       protected val _settings: ModelMapperSettings = ModelMapperSettings(),
                       protected val _encryptor:Encryptor? = null,
                       protected val namer: Namer? = null) : Mapper {


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
    override fun createEntityWithArgs(cls:KClass<*>, args: List<Any?>?): Any =
            Reflector.createWithArgs(cls, args?.toTypedArray() ?: arrayOf())


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
        return mapFromToValType(null, record, _model)
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
        return mapFromToVarType(null, record, _model)
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
        fun loadSchema(dataType: KClass<*>, idFieldName:String? = null, namer:Namer? = null): Model {
            val modelName = dataType.simpleName!!
            val modelNameFull = dataType.qualifiedName!!

            // Now add all the fields.
            val matchedFields = Reflector.getAnnotatedProps<Field>(dataType, Field::class)
            //val fields = mutableListOf<ModelField>()

            // Loop through each field
            val withAnnos = matchedFields.filter { it.second != null }
            val fields = withAnnos.map { matchedField ->
                val anno = matchedField.second!!
                val cat = idFieldName?.let { "id" } ?: ""
                val name = if (anno.name.isNullOrEmpty()) matchedField.first.name else anno.name
                val required = anno.required
                val length = anno.length
                val encrypt = anno.encrypt
                val prop  = matchedField.first
                val fieldKType = matchedField.first.returnType
                val fieldCls = fieldKType.jvmErasure
                val modelField = ModelField.build(prop = prop, name = name,
                        dataType = fieldCls,
                        dataKType = fieldKType,
                        isRequired = required, maxLength = length, encrypt = encrypt, cat = cat, namer = namer)

                val finalModelField = if(!modelField.isBasicType()) {
                    val model = loadSchema(modelField.dataCls, namer = namer)
                    modelField.copy(model = model)
                } else modelField
                finalModelField
            }

            return Model(modelName, modelNameFull, dataType, _propList = fields, namer = namer)
        }
    }


    private fun mapFromToValType(prefix:String?, record: Record, model:Model): Any? {
        return if (model.any) {
            val isUTC = _settings.persisteUTCDate
            val data = model.fields.map { mapping ->
                val dataValue = getDataValue(prefix, mapping, record, isUTC)
                dataValue
            }
            val entity = createEntityWithArgs(model.dataType!!, data)
            entity
        }
        else
            null
    }


    private fun mapFromToVarType(prefix:String?, record: Record, model:Model ): Any? {
        return if (model.any) {

            val isUTC = _settings.persisteUTCDate
            val entity: Any? = createEntity()
            model.fields.forEach { mapping ->
                val dataValue = getDataValue(prefix, mapping, record, isUTC)
                mapping.prop?.let { prop ->
                     Reflector.setFieldValue(entity, prop, dataValue)
                } ?: Reflector.setFieldValue(model.dataType!!, entity, mapping.name, dataValue)
            }
            entity
        }
        else
            null
    }


    /**
     * @param prefix : Used as the prefix for column names for mapping embedded objects
     * @param mapping: ModelField containing all the meta data
     * @parma record : Record holding the source data available via position/name
     * @param isUTC  : Whether to handle dates as UTC
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun getDataValue(prefix:String?, mapping:ModelField, record:Record, isUTC:Boolean): Any? {
        val colName = prefix?.let { prefix + mapping.storedName } ?: mapping.storedName
        val dataValue = when (mapping.dataCls) {
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
            KTypes.KUUIDClass          -> record.getUUID(colName)
            KTypes.KUniqueIdClass      -> record.getUniqueId(colName)
            else                       -> {
                if(mapping.isEnum){
                    val enumInt = record.getInt(colName)
                    val enumValue = Reflector.getEnumValue(mapping.dataCls, enumInt)
                    enumValue
                } else {
                    val model = mapFromToValType(mapping.name + "_", record, mapping.model!!)
                    model
                }
            }
        }
        return dataValue
    }
}