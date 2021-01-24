package slatekit.entities.mapper

import org.threeten.bp.*
import slatekit.common.DateTime
import slatekit.common.EnumLike
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.common.data.Value
import slatekit.common.data.Values
import slatekit.common.ids.UPID
import slatekit.data.core.Meta
import slatekit.data.encoders.Encoders
import slatekit.entities.Consts
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import slatekit.meta.models.Model
import slatekit.meta.models.ModelField
import slatekit.query.QueryEncoder


open class EntityEncoder<TId, T>(val model: Model,
                                 val meta: Meta<TId, T>,
                                 val settings: EntitySettings = EntitySettings(true),
                                 val encryptor: Encryptor? = null,
                                 val encoders: Encoders<TId, T> = Encoders()) : Encoder<TId, T> where TId : kotlin.Comparable<TId>, T : Any {

    /**
     * Gets all the column names mapped to the field names
     */
    private val cols: List<String> by lazy { model.fields.map { it.storedName } }

    /**
     * Primary Key / identity field
     */
    private val idCol = model.idField!!


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
                val col = prefix?.let { meta.encode(composite(it, mapping.storedName)) } ?: meta.encode(mapping.storedName)

                // Convert to sql value
                val data = toSql(mapping, item, enc)

                // Build up list of values
                when (data) {
                    is List<*> -> data.forEach {
                        when (it) {
                            is Value -> converted.add(it)
                            else -> converted.add(Value(col, buildValue(col, it ?: "", false)))
                        }
                    }
                    else -> converted.add(Value(col, buildValue(col, data, false)))
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
    private inline fun toSql(mapping: ModelField, item: Any, enc: Encryptor?): Any {
        // =======================================================
        // NOTE: Refactor this to use pattern matching ?
        // Similar to the Mapper class but reversed
        val data = if (mapping.dataCls == KTypes.KStringClass) {
            val sVal = Reflector.getFieldValue(item, mapping.name) as String?
            val sVanEnc = when {
                !mapping.encrypt -> sVal
                sVal == null -> sVal
                enc != null -> enc.encrypt(sVal)
                encryptor != null -> encryptor?.encrypt(sVal)
                else -> sVal
            }
            encoders.strings.encode(sVanEnc)
        } else if (mapping.dataCls == KTypes.KBoolClass) {
            val bVal = Reflector.getFieldValue(item, mapping.name) as Boolean?
            encoders.bools.encode(bVal)
        } else if (mapping.dataCls == KTypes.KShortClass) {
            val sVal = Reflector.getFieldValue(item, mapping.name) as Short?
            encoders.shorts.encode(sVal)
        } else if (mapping.dataCls == KTypes.KIntClass) {
            val iVal = Reflector.getFieldValue(item, mapping.name) as Int?
            encoders.ints.encode(iVal)
        } else if (mapping.dataCls == KTypes.KLongClass) {
            val lVal = Reflector.getFieldValue(item, mapping.name) as Long?
            encoders.longs.encode(lVal)
        } else if (mapping.dataCls == KTypes.KFloatClass) {
            val fVal = Reflector.getFieldValue(item, mapping.name) as Float?
            encoders.floats.encode(fVal)
        } else if (mapping.dataCls == KTypes.KDoubleClass) {
            val dVal = Reflector.getFieldValue(item, mapping.name) as Double?
            encoders.doubles.encode(dVal)
        } else if (mapping.dataCls == KTypes.KDateTimeClass) {
            val dtVal = Reflector.getFieldValue(item, mapping.name) as DateTime?
            encoders.dateTimes.toSql(dtVal, settings.utcTime)
        } else if (mapping.dataCls == KTypes.KLocalDateClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as LocalDate?
            encoders.localDates.encode(raw)
        } else if (mapping.dataCls == KTypes.KLocalTimeClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as LocalTime?
            encoders.localTimes.encode(raw)
        } else if (mapping.dataCls == KTypes.KLocalDateTimeClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as LocalDateTime?
            encoders.localDateTimes.encode(raw)
        } else if (mapping.dataCls == KTypes.KZonedDateTimeClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as ZonedDateTime?
            encoders.zonedDateTimes.toSql(raw, settings.utcTime)
        } else if (mapping.dataCls == KTypes.KInstantClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as Instant?
            encoders.instants.encode(raw)
        } else if (mapping.dataCls == KTypes.KUUIDClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as java.util.UUID?
            encoders.uuids.encode(raw)
        } else if (mapping.dataCls == KTypes.KUPIDClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as UPID?
            encoders.upids.encode(raw)
        } else if (mapping.isEnum) {
            val raw = Reflector.getFieldValue(item, mapping.name) as EnumLike
            encoders.enums.encode(raw)
        } else if (mapping.model != null) {
            val subObject = Reflector.getFieldValue(item, mapping.name)
            subObject?.let { mapFields(mapping.name, subObject, mapping.model!!, enc) } ?: Consts.NULL
        } else { // other object
            val objVal = Reflector.getFieldValue(item, mapping.name)
            val data = objVal?.toString() ?: ""
            "'" + QueryEncoder.ensureValue(data) + "'"
        }
        return data
    }


    private inline fun composite(prefix:String, name:String):String {
        return prefix + "_" + name
    }
}
