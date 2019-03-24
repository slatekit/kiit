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
import slatekit.common.db.Mapper
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.meta.Reflector
import slatekit.common.Record
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
open class ModelMapper(
        protected val _model: Model,
        protected val _settings: ModelMapperSettings = ModelMapperSettings(),
        protected val _encryptor: Encryptor? = null,
        protected val namer: Namer? = null
) : Mapper {

    /**
     * The model associated with this mapper.
     * @return
     */
    fun model(): Model = _model

    /**
     * Creates the entity/model expecting a 0 parameter constructor
     * @return
     */
    open fun createEntity(): Any? =
            _model.dataType?.let { type -> Reflector.create<Any>(type) }

    /**
     * Creates the entity/model with all the supplied constructor parameters (ideal for case classes)
     * @param args
     * @return
     */
    open fun createEntityWithArgs(cls: KClass<*>, args: List<Any?>?): Any =
            Reflector.createWithArgs(cls, args?.toTypedArray() ?: arrayOf())

    fun <T> copyWithId(id: Any, entity: T): T = entity

    /**
     * Maps all the parameters to a class that takes in all parameters in the constructor
     * This is ideally for Case Classes, allowing the representation of models as immutable
     * case classes
     * @param record
     * @return
     */
    override fun <T> mapFrom(record: Record): T? {
        return if (_model.any && _model.dataType != null) {
            _model.dataType.let { tpe ->
                if (Reflector.isDataClass(tpe)) {
                    mapFromToValType<T>(record)
                } else
                    mapFromToVarType<T>(record)
            }
        } else
            null
    }

    /**
     * Maps all the parameters to a class that takes in all parameters in the constructor
     * This is ideally for Case Classes, allowing the representation of models as immutable
     * case classes
     * @param record
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T>  mapFromToValType(record: Record): T? {
        return mapFromToValType(null, record, _model) as T?
    }

    /**
     * Maps all the parameters to a class that supports vars as fields.
     * While this is NOT recommended, it is still supported.
     * case classes
     * @param record
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> mapFromToVarType(record: Record): T? {
        return mapFromToVarType(null, record, _model) as T?
    }

    override fun toString(): String =
            _model.fields.fold("", { s, field -> s + field.toString() + newline })

    companion object {

        /**
         * Builds a schema ( Model ) from the Class/Type supplied.
         * NOTE: The mapper then works off the Model class for to/from mapping of data to model.
         * @param dataType
         * @return
         */
        @JvmStatic
        fun loadSchema(dataType: KClass<*>, idFieldName: String? = null, namer: Namer? = null, table: String? = null): Model {
            val modelName = dataType.simpleName!!
            val modelNameFull = dataType.qualifiedName!!

            // Now add all the fields.
            val matchedFields = Reflector.getAnnotatedProps<Field>(dataType, Field::class)
            // val fields = mutableListOf<ModelField>()

            // Loop through each field
            val withAnnos = matchedFields.filter { it.second != null }
            val fields = withAnnos.map { matchedField ->
                val anno = matchedField.second!!
                val cat = idFieldName?.let { "id" } ?: ""
                val name = if (anno.name.isNullOrEmpty()) matchedField.first.name else anno.name
                val required = anno.required
                val length = anno.length
                val encrypt = anno.encrypt
                val prop = matchedField.first
                val fieldKType = matchedField.first.returnType
                val fieldType = ModelUtils.fieldType(prop)
                val fieldCls = fieldKType.jvmErasure
                val modelField = ModelField.build(
                        prop = prop, name = name,
                        dataType = fieldCls,
                        dataFieldType = fieldType,
                        isRequired = required,
                        isIndexed = anno.indexed,
                        isUnique = anno.unique,
                        isUpdatable = anno.updatable,
                        maxLength = length,
                        encrypt = encrypt,
                        cat = cat,
                        namer = namer
                )

                val finalModelField = if (!modelField.isBasicType()) {
                    val model = loadSchema(modelField.dataCls, namer = namer)
                    modelField.copy(model = model)
                } else modelField
                finalModelField
            }

            return Model(modelName, modelNameFull, dataType, _propList = fields, namer = namer, tableName = table ?: "")
        }
    }

    private fun mapFromToValType(prefix: String?, record: Record, model: Model): Any? {
        return if (model.any) {
            val isUTC = _settings.persisteUTCDate
            val data = model.fields.map { mapping ->
                val dataValue = getDataValue(prefix, mapping, record, isUTC)
                dataValue
            }
            val entity = createEntityWithArgs(model.dataType!!, data)
            entity
        } else
            null
    }

    private fun mapFromToVarType(prefix: String?, record: Record, model: Model): Any? {
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
        } else
            null
    }

    /**
     * @param prefix : Used as the prefix for column names for mapping embedded objects
     * @param mapping: ModelField containing all the meta data
     * @parma record : Record holding the source data available via position/name
     * @param isUTC : Whether to handle dates as UTC
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun getDataValue(prefix: String?, mapping: ModelField, record: Record, isUTC: Boolean): Any? {
        val colName = prefix?.let { prefix + mapping.storedName } ?: mapping.storedName

        val dataValue = when (mapping.dataCls) {
            KTypes.KStringClass        -> getString(record, mapping, colName, _encryptor)
            KTypes.KBoolClass          -> if ( mapping.isRequired ) record.getBool(colName)          else record.getBoolOrNull(colName)
            KTypes.KShortClass         -> if ( mapping.isRequired ) record.getShort(colName)         else record.getShortOrNull(colName)
            KTypes.KIntClass           -> if ( mapping.isRequired ) record.getInt(colName)           else record.getIntOrNull(colName)
            KTypes.KLongClass          -> if ( mapping.isRequired ) record.getLong(colName)          else record.getLongOrNull(colName)
            KTypes.KFloatClass         -> if ( mapping.isRequired ) record.getFloat(colName)         else record.getFloatOrNull(colName)
            KTypes.KDoubleClass        -> if ( mapping.isRequired ) record.getDouble(colName)        else record.getDoubleOrNull(colName)
            KTypes.KLocalDateClass     -> if ( mapping.isRequired ) record.getLocalDate(colName)     else record.getLocalDateOrNull(colName)
            KTypes.KLocalTimeClass     -> if ( mapping.isRequired ) record.getLocalTime(colName)     else record.getLocalTimeOrNull(colName)
            KTypes.KLocalDateTimeClass -> if ( mapping.isRequired ) record.getLocalDateTime(colName) else record.getLocalDateOrNull(colName)
            KTypes.KZonedDateTimeClass -> if ( mapping.isRequired ) record.getZonedDateTime(colName) else record.getZonedDateTimeOrNull(colName)
            KTypes.KDateTimeClass      -> if ( mapping.isRequired ) record.getDateTime(colName)      else record.getDateTimeOrNull(colName)
            KTypes.KInstantClass       -> if ( mapping.isRequired ) record.getInstant(colName)       else record.getInstantOrNull(colName)
            KTypes.KUUIDClass          -> if ( mapping.isRequired ) record.getUUID(colName)          else record.getUUIDOrNull(colName)
            KTypes.KUniqueIdClass      -> if ( mapping.isRequired ) record.getUniqueId(colName)      else record.getUniqueIdOrNull(colName)
            else -> {
                if (mapping.isEnum) {
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

    @Suppress("NOTHING_TO_INLINE")
    inline fun getString(record: Record, mapping: ModelField, colName: String, encryptor: Encryptor?): String? {
        val rawValue = if(mapping.isRequired)
            record.getString(colName)
        else
            record.getStringOrNull(colName)

        return rawValue?.let { raw ->
            if (mapping.encrypt) {
                encryptor?.let { it.decrypt(raw) } ?: raw
            } else {
                raw
            }
        }
    }
}
