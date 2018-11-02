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

@file:Suppress("NOTHING_TO_INLINE")

package slatekit.entities.core

import slatekit.common.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.common.query.QueryEncoder
import slatekit.common.UniqueId
import slatekit.entities.Consts
import slatekit.entities.Consts.idCol
import slatekit.entities.databases.Converter
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import slatekit.meta.models.Model
import slatekit.meta.models.ModelField
import slatekit.meta.models.ModelMapper
import java.time.*
/**
 * Maps an entity to sql and from sql records.
 *
 * @param model
 */
open class EntityMapper(model: Model,
                        val converter: Converter,
                        val isUtc: Boolean = false,
                        val quoteChar: Char = '`',
                        encryptor: Encryptor? = null,
                        namer: Namer? = null)
    : ModelMapper(model, _encryptor = encryptor, namer = namer) {


    /**
     * Gets all the column names mapped to the field names
     */
    val cols:List<String> by lazy {
        model.fields.map { it.storedName }
    }


    /**
     * Gets all the column names excluding the primary key ("id" for now ) mapped to field names.
     */
    val colsWithoutId:List<String> by lazy {
        model.fields.filter { it.name.toLowerCase() != idCol }.map { it.storedName }
    }


    /**
     * This is intentionally a long method that:
     *
     * 1. is optimized for performance of the model to sql mappings
     * 2. is recursive to support embedded objects in a table/model
     * 3. handles the construction of sql for both inserts/updates
     *
     * NOTE: For a simple model, only this 1 function call is required to
     * generate the sql for inserts/updates, allowing 1 record = 1 function call
     */
    fun mapFields(prefix: String?, item: Any, model: Model, useKeyValue: Boolean): List<Pair<String, String>> {

        val converted = mutableListOf<Pair<String, String>>()
        val len = model.fields.size
        for (ndx in 0 until len) {
            val mapping = model.fields[ndx]

            if(mapping.name.toLowerCase() != idCol) {
                // Column name e.g first = 'first'
                // Also for sub-objects
                val col = prefix?.let { buildName(it, mapping.storedName) } ?: buildName(mapping.storedName)

                // Convert to sql value
                val data = toSql(mapping, item, useKeyValue)

                // Build up list of values
                when (data) {
                    is List<*> -> data.forEach { converted.add(Pair(col, buildValue(col, it ?: "", useKeyValue))) }
                    else -> converted.add(Pair(col, buildValue(col, data, useKeyValue)))
                }
            }
        }
        return converted
    }


    open fun tableName():String = buildName(_model.table)


    open fun buildName(name: String): String {
        val finalName = namer?.rename(name) ?: name
        return "$quoteChar$finalName$quoteChar"
    }

    open fun buildName(prefix: String, name: String): String {
        val finalName = namer?.rename(name) ?: name
        return "$quoteChar${prefix}_$finalName$quoteChar"
    }


    /**
     * Builds the value as either "'john'" or "first='john'"
     * which is needed for insert/update
     */
    private inline fun buildValue(col:String, data:Any, useKeyValue: Boolean):String {
        return if (useKeyValue) "$col=$data" else data.toString()
    }


    /**
     * Converts a single model field value into either:
     * 1. a single sql string value
     * 2. a list of sql string value ( used for embedded objects )
     */
    private inline fun toSql(mapping: ModelField, item: Any, useKeyValue: Boolean): Any {
        // =======================================================
        // NOTE: Refactor this to use pattern matching ?
        // Similar to the Mapper class but reversed
        val data = if (mapping.dataCls == KTypes.KStringClass) {
            val sVal = Reflector.getFieldValue(item, mapping.name) as String?
            converter.strings.toSql(sVal, mapping.encrypt, _encryptor)
        } else if (mapping.dataCls == KTypes.KBoolClass) {
            val bVal = Reflector.getFieldValue(item, mapping.name) as Boolean?
            converter.bools.toSql(bVal)
        } else if (mapping.dataCls == KTypes.KShortClass) {
            val sVal = Reflector.getFieldValue(item, mapping.name) as Short?
            converter.shorts.toSql(sVal)
        } else if (mapping.dataCls == KTypes.KIntClass) {
            val iVal = Reflector.getFieldValue(item, mapping.name) as Int?
            converter.ints.toSql(iVal)
        } else if (mapping.dataCls == KTypes.KLongClass) {
            val lVal = Reflector.getFieldValue(item, mapping.name) as Long?
            converter.longs.toSql(lVal)
        } else if (mapping.dataCls == KTypes.KFloatClass) {
            val fVal = Reflector.getFieldValue(item, mapping.name) as Float?
            converter.floats.toSql(fVal)
        } else if (mapping.dataCls == KTypes.KDoubleClass) {
            val dVal = Reflector.getFieldValue(item, mapping.name) as Double?
            converter.doubles.toSql(dVal)
        } else if (mapping.dataCls == KTypes.KDateTimeClass) {
            val dtVal = Reflector.getFieldValue(item, mapping.name) as DateTime?
            converter.dateTimes.toSql(dtVal, isUtc)
        } else if (mapping.dataCls == KTypes.KLocalDateClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as LocalDate?
            converter.localDates.toSql(raw)
        } else if (mapping.dataCls == KTypes.KLocalTimeClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as LocalTime?
            converter.localTimes.toSql(raw)
        } else if (mapping.dataCls == KTypes.KLocalDateTimeClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as LocalDateTime?
            converter.localDateTimes.toSql(raw)
        } else if (mapping.dataCls == KTypes.KZonedDateTimeClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as ZonedDateTime?
            converter.zonedDateTimes.toSql(raw, isUtc)
        } else if (mapping.dataCls == KTypes.KInstantClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as Instant?
            converter.instants.toSql(raw)
        } else if (mapping.dataCls == KTypes.KUUIDClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as java.util.UUID?
            converter.uuids.toSql(raw)
        } else if (mapping.dataCls == KTypes.KUniqueIdClass) {
            val raw = Reflector.getFieldValue(item, mapping.name) as UniqueId?
            converter.uniqueIds.toSql(raw)
        } else if (mapping.isEnum) {
            val raw = Reflector.getFieldValue(item, mapping.name) as EnumLike
            converter.enums.toSql(raw)
        } else if (mapping.model != null) {
            val subObject = Reflector.getFieldValue(item, mapping.name)
            subObject?.let { mapFields(mapping.name, subObject, mapping.model!!, useKeyValue) } ?: Consts.NULL
        } else { // other object
            val objVal = Reflector.getFieldValue(item, mapping.name)
            val data = objVal?.toString() ?: ""
            "'" + QueryEncoder.ensureValue(data) + "'"
        }
        return data
    }
}
