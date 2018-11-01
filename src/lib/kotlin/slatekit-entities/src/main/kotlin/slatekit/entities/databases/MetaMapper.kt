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

package slatekit.entities.databases

import slatekit.common.*
import slatekit.common.encrypt.Encryptor
import slatekit.common.naming.Namer
import slatekit.common.query.QueryEncoder
import slatekit.common.UniqueId
import slatekit.entities.core.EntityMapper
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import slatekit.meta.models.Model
import slatekit.meta.models.ModelMapper
import java.time.*
import java.time.format.DateTimeFormatter

/**
 * Reflection based Meta mapper to map an entity to/from SQL
 *
 * @param model
 */
open class MetaMapper(model: Model,
                      converter:Converter,
                      table:String? = null,
                      persistAsUtc: Boolean = false,
                      encryptor: Encryptor? = null,
                      namer: Namer? = null,
                      private val encodedChar:Char = '`')
    : EntityMapper(model, table, persistAsUtc, encryptor, namer) {


    override fun mapToSql(item: Any, update: Boolean, fullSql: Boolean): String {

        return if (!_model.any)
            ""
        else
            mapFields(item, update, fullSql)
    }


    override fun mapFields(item: Any, update: Boolean, fullSql: Boolean): String {
        var rawSql = ""
        val result = mapFields(null, item, update, _model)

        if (update) {
            rawSql = " " + result.updates
        } else {
            val cols = "(" + result.cols + ") "
            rawSql = cols + "VALUES (" + result.data + ")"
        }
        val finalSql = if (!fullSql)
            rawSql
        else if (update)
            "update ${buildName(_model.name)} set " + rawSql + ";"
        else
            "insert into ${buildName(_model.name)} " + rawSql + ";"
        return finalSql
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
    override fun mapFields(prefix: String?, item: Any, update: Boolean, model: Model): MappedSql {
        var dat = ""
        var updates = ""
        var cols = ""
        val NULL = "NULL"

        val len = model.fields.size
        for (ndx in 0 until len) {
            val mapping = model.fields[ndx]
            val propName = mapping.name
            val colName = prefix?.let { buildName(it, mapping.storedName) } ?: buildName(mapping.storedName)
            var isSubObject = false
            var subObjectSql: MappedSql? = null

            // =======================================================
            // NOTE: Refactor this to use pattern matching ?
            // Similar to the Mapper class but reversed
            val data = if (mapping.dataCls == KTypes.KStringClass) {
                val sVal = Reflector.getFieldValue(item, mapping.name) as String?
                // TODO
            } else if (mapping.dataCls == KTypes.KBoolClass) {
                val bVal = Reflector.getFieldValue(item, mapping.name) as Boolean?
                // TODO
            } else if (mapping.dataCls == KTypes.KShortClass) {
                val iVal = Reflector.getFieldValue(item, mapping.name) as Short?
                // TODO
            } else if (mapping.dataCls == KTypes.KIntClass) {
                val iVal = Reflector.getFieldValue(item, mapping.name) as Int?
                // TODO
            } else if (mapping.dataCls == KTypes.KLongClass) {
                val lVal = Reflector.getFieldValue(item, mapping.name) as Long?
                // TODO
            } else if (mapping.dataCls == KTypes.KFloatClass) {
                val dVal = Reflector.getFieldValue(item, mapping.name) as Float?
                // TODO
            } else if (mapping.dataCls == KTypes.KDoubleClass) {
                val dVal = Reflector.getFieldValue(item, mapping.name) as Double?
                // TODO
            } else if (mapping.dataCls == KTypes.KDateTimeClass) {
                val dtVal = Reflector.getFieldValue(item, mapping.name) as DateTime?
                // TODO
            } else if (mapping.dataCls == KTypes.KLocalDateClass) {
                val raw = Reflector.getFieldValue(item, mapping.name) as LocalDate?
                // TODO
            } else if (mapping.dataCls == KTypes.KLocalTimeClass) {
                val raw = Reflector.getFieldValue(item, mapping.name) as LocalTime?
                // TODO
            } else if (mapping.dataCls == KTypes.KLocalDateTimeClass) {
                val raw = Reflector.getFieldValue(item, mapping.name) as LocalDateTime?
                // TODO
            } else if (mapping.dataCls == KTypes.KZonedDateTimeClass) {
                val raw = Reflector.getFieldValue(item, mapping.name) as ZonedDateTime?
                // TODO
            } else if (mapping.dataCls == KTypes.KInstantClass) {
                val raw = Reflector.getFieldValue(item, mapping.name) as Instant?
                // TODO
            } else if (mapping.dataCls == KTypes.KUUIDClass) {
                val raw = Reflector.getFieldValue(item, mapping.name) as java.util.UUID?
                // TODO
            } else if (mapping.dataCls == KTypes.KUniqueIdClass) {
                val raw = Reflector.getFieldValue(item, mapping.name) as UniqueId?
                // TODO
            } else if (mapping.isEnum) {
                val raw = Reflector.getFieldValue(item, mapping.name) as EnumLike
                // TODO
            } else if (mapping.model != null) {
                isSubObject = true
                val subObject = Reflector.getFieldValue(item, mapping.name)
                // TODO
            } else // Other object
            {
                val objVal = Reflector.getFieldValue(item, mapping.name)
                val data = objVal?.toString() ?: ""
                "'" + QueryEncoder.ensureValue(data) + "'"
            }
            // Setup the inserts/updates
            val isLastField = ndx == model.fields.size - 1
            if (!update) {

                // Build up the columns as "col1,col2,col3"
                // Build up the data as "1,'abc',true"
                if (isSubObject) {
                    subObjectSql?.let {
                        cols += it.cols
                        dat += it.data
                    }
                } else {
                    cols += colName
                    dat += data
                }
                if (!isLastField) {
                    cols += ","
                    dat += ","
                }
            } else {

                // Build up the updates as "col1=1,col2='abc',col3=true"
                if (isSubObject) {
                    subObjectSql?.let {
                        updates += it.updates
                    }
                } else {
                    updates += "$colName=$data"
                }
                if (!isLastField) {
                    updates += ","
                }
            }
        }
        return MappedSql(cols, dat, updates)
    }
}
