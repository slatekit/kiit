package slatekit.entities.mapper

import slatekit.common.Record
import slatekit.common.crypto.Encryptor
import slatekit.common.newline
import slatekit.data.core.Meta
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import slatekit.meta.models.Model
import slatekit.meta.models.ModelField
import kotlin.reflect.KClass

open class EntityDecoder<TId, T>(val model: Model,
                                 val meta: Meta<TId, T>,
                                 val idType: KClass<*>,
                                 val clsType: KClass<*>,
                                 val settings: EntitySettings,
                                 val encryptor: Encryptor?) : Decoder<TId, T> where TId : kotlin.Comparable<TId>, T : Any {

    override fun decode(record: Record, enc: Encryptor?): T? {
        return if (model.any && model.dataType != null) {
            model.dataType?.let { tpe ->
                if (Reflector.isDataClass(tpe)) {
                    decodeValType<T>(record, enc)
                } else
                    decodeVarType<T>(record, enc)
            }
        } else
            null
    }

    /**
     * Creates the entity/model expecting a 0 parameter constructor
     * @return
     */
    open fun createEntity(): Any? =
        model.dataType?.let { type -> Reflector.create<Any>(type) }

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
    @Suppress("UNCHECKED_CAST")
    fun <T>  decodeValType(record: Record, enc:Encryptor? = null): T? {
        return decodeValType(null, record, model, enc) as T?
    }

    /**
     * Maps all the parameters to a class that supports vars as fields.
     * While this is NOT recommended, it is still supported.
     * case classes
     * @param record
     * @return
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> decodeVarType(record: Record, enc:Encryptor? = null): T? {
        return decodeVarType(null, record, model, enc) as T?
    }

    override fun toString(): String =
        model.fields.fold("", { s, field -> s + field.toString() + newline })



    private fun decodeValType(prefix: String?, record: Record, model: Model, enc:Encryptor? = null): Any? {
        return if (model.any) {
            val isUTC = settings.utcTime
            val data = model.fields.map { mapping ->
                val dataValue = getDataValue(prefix, mapping, record, isUTC, enc)
                dataValue
            }
            val entity = createEntityWithArgs(model.dataType!!, data)
            entity
        } else
            null
    }

    private fun decodeVarType(prefix: String?, record: Record, model: Model, enc:Encryptor? = null): Any? {
        return if (model.any) {

            val isUTC = settings.utcTime
            val entity: Any? = createEntity()
            model.fields.forEach { mapping ->
                val dataValue = getDataValue(prefix, mapping, record, isUTC, enc)
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
    private fun getDataValue(prefix: String?, mapping: ModelField, record: Record, isUTC: Boolean, enc:Encryptor? = null): Any? {
        val colName = prefix?.let { prefix + mapping.storedName } ?: mapping.storedName

        val dataValue = when (mapping.dataCls) {
            KTypes.KStringClass        -> getString(record, mapping, colName, enc ?: encryptor)
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
            KTypes.KUPIDClass          -> if ( mapping.isRequired ) record.getUPID(colName)          else record.getUPIDOrNull(colName)
            else -> {
                if (mapping.isEnum) {
                    val enumInt = record.getInt(colName)
                    val enumValue = Reflector.getEnumValue(mapping.dataCls, enumInt)
                    enumValue
                } else {
                    val model = decodeValType(mapping.name + "_", record, mapping.model!!)
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
