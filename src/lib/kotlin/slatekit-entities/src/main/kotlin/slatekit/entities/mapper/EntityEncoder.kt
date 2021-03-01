package slatekit.entities.mapper

import org.threeten.bp.*
import slatekit.common.DateTime
import slatekit.common.EnumLike
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.common.data.DataType
import slatekit.common.data.Value
import slatekit.common.data.Values
import slatekit.common.ids.UPID
import slatekit.data.core.Meta
import slatekit.data.encoders.Encoders
import slatekit.entities.Consts
import slatekit.meta.kClass
import slatekit.meta.models.Model
import slatekit.meta.models.ModelField
import slatekit.query.QueryEncoder
import kotlin.reflect.full.memberProperties


open class EntityEncoder<TId, T>(val model: Model,
                                 val meta: Meta<TId, T>,
                                 val settings: EntitySettings = EntitySettings(true),
                                 val encryptor: Encryptor? = null,
                                 val encoders: Encoders<TId, T> = Encoders(settings.utcTime)) : Encoder<TId, T> where TId : kotlin.Comparable<TId>, T : Any {

    /**
     * Gets all the column names mapped to the field names
     */
    protected val cols: List<String> by lazy { model.fields.map { it.storedName } }


    /**
     * Primary Key / identity field
     */
    protected val idCol = model.idField!!

    /**
     * Encodes the item into @see[slatekit.common.data.Values] which
     * contains a simple list of key/value pairs
     */
    override fun encode(item: T, action: DataAction, enc: Encryptor?): Values {
        return mapFields(null, item, model, enc)
    }


    /**
     * 1. is optimized for performance of the model to sql mappings
     * 2. is recursive to support embedded objects in a table/model
     * 3. handles the construction of sql for both inserts/updates
     *
     * NOTE: For a simple model, only this 1 function call is required to
     * generate the sql for inserts/updates, allowing 1 record = 1 function call
     */
    private fun mapFields(prefix: String?, item: Any, model: Model, enc: Encryptor? = null): List<Value> {

        val converted = mutableListOf<Value>()
        val len = model.fields.size
        for (ndx in 0 until len) {
            val mapping = model.fields[ndx]
            val isIdCol = mapping == model.idField
            val isFieldMapped = !isIdCol
            if (isFieldMapped) {
                // Column name e.g first = 'first'
                // Also for sub-objects

                // Convert to sql value
                val data = toSql(prefix, mapping, item, enc)

                // Build up list of values
                when (data) {
                    is Value -> converted.add(data)
                    is List<*> -> data.forEach {
                        when (it) {
                            is Value -> converted.add(it)
                            else -> {
                                val col = prefix?.let { meta.encode(composite(it, mapping.storedName)) } ?: meta.encode(mapping.storedName)
                                converted.add(Value(col, DataType.DTString, buildValue(col, it ?: "", false)))
                            }
                        }
                    }
                    else -> {
                        val col = prefix?.let { meta.encode(composite(it, mapping.storedName)) } ?: meta.encode(mapping.storedName)
                        converted.add(Value(col, mapping.dataTpe, buildValue(col, data, false)))
                    }
                }
            }
        }
        return converted.toList()
    }


    /**
     * Builds the value as either "'john'" or "first='john'"
     * which is needed for insert/update
     */
    private inline fun buildValue(col: String, data: Any, useKeyValue: Boolean): String {
        return if (useKeyValue) "$col=$data" else data.toString()
    }


    /**
     * Converts a single model field value into either:
     * 1. a single sql string value
     * 2. a list of sql string value ( used for embedded objects )
     */
    private inline fun toSql(prefix: String?, mapping: ModelField, item: Any, enc: Encryptor?): Any {
        val qualifiedName = prefix?.let { composite(it, mapping.storedName) } ?: mapping.storedName
        val columnName = meta.encode(qualifiedName)
        // =======================================================
        // NOTE: Refactor this to use pattern matching ?
        // Similar to the Mapper class but reversed
        val data = if (mapping.dataTpe == DataType.DTString) {
            val sVal = getValue(item, qualifiedName, mapping) as String?
            val sValEnc = when {
                !mapping.encrypt -> sVal
                sVal == null -> sVal
                enc != null -> enc.encrypt(sVal)
                encryptor != null -> encryptor?.encrypt(sVal)
                else -> sVal
            }
            encoders.strings.convert(columnName, sValEnc)
        } else if (mapping.dataTpe == DataType.DTBool) {
            val raw = getValue(item, qualifiedName, mapping) as Boolean?
            encoders.bools.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTShort) {
            val raw = getValue(item, qualifiedName, mapping) as Short?
            encoders.shorts.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTInt) {
            val raw = getValue(item, qualifiedName, mapping) as Int?
            encoders.ints.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTLong) {
            val raw = getValue(item, qualifiedName, mapping) as Long?
            encoders.longs.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTFloat) {
            val raw = getValue(item, qualifiedName, mapping) as Float?
            encoders.floats.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTDouble) {
            val raw = getValue(item, qualifiedName, mapping) as Double?
            encoders.doubles.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTDateTime) {
            val dt = getDateTime(item, mapping, qualifiedName, columnName)
            encoders.dateTimes.convert(columnName, dt)
        } else if (mapping.dataTpe == DataType.DTLocalDate) {
            val raw = getValue(item, qualifiedName, mapping) as LocalDate?
            encoders.localDates.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTLocalTime) {
            val raw = getValue(item, qualifiedName, mapping) as LocalTime?
            encoders.localTimes.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTLocalDateTime) {
            val raw = getValue(item, qualifiedName, mapping) as LocalDateTime?
            encoders.localDateTimes.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTZonedDateTime) {
            val dt = getDateTime(item, mapping, qualifiedName, columnName)
            encoders.zonedDateTimes.convert(columnName, dt)
        } else if (mapping.dataTpe == DataType.DTInstant) {
            val raw = getValue(item, qualifiedName, mapping) as Instant?
            encoders.instants.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTUUID) {
            val raw = getValue(item, qualifiedName, mapping) as java.util.UUID?
            encoders.uuids.convert(columnName, raw)
        } else if (mapping.dataTpe == DataType.DTUPID) {
            val raw = getValue(item, qualifiedName, mapping) as UPID?
            encoders.upids.convert(columnName, raw)
        } else if (mapping.isEnum) {
            val raw = getValue(item, qualifiedName, mapping) as EnumLike
            encoders.enums.convert(columnName, raw)
        } else if (mapping.model != null) {
            val subObject = getValue(item, qualifiedName, mapping)
            subObject?.let { mapFields(mapping.name, subObject, mapping.model!!, enc) } ?: Consts.NULL
        } else { // other object
            val objVal = getValue(item, qualifiedName, mapping)
            val data = objVal?.toString() ?: ""
            val txtValue = "'" + QueryEncoder.ensureValue(data) + "'"
            Value(columnName, DataType.DTString, objVal, txtValue)
        }
        return data
    }

    protected open fun getValue(inst:Any, qualifiedName:String, mapping: ModelField): Any? {
        return when(val prop = mapping.prop) {
            null -> {
                val item = inst.kClass.memberProperties.find { it.name == mapping.name }
                item?.getter?.call(inst)
            }
            else -> {
                prop.getter.call(inst)
            }
        }
    }

    protected open fun getDateTime(item: Any, mapping: ModelField, qualifiedName: String, columnName:String):DateTime? {
        val dt = getValue(item, qualifiedName, mapping) as DateTime?
        return dt
    }

    private inline fun composite(prefix:String, name:String):String {
        return prefix + "_" + name
    }
}
