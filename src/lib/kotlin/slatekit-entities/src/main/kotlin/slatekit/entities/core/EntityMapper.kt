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

package slatekit.entities.core



import slatekit.common.DateTime
import slatekit.common.encrypt.Encryptor
import slatekit.common.nonEmptyOrDefault
import slatekit.common.query.QueryEncoder
import slatekit.meta.KTypes
import slatekit.meta.Reflector
import slatekit.meta.models.Model
import slatekit.meta.models.ModelMapper
import java.time.*
import java.time.format.DateTimeFormatter


/**
 * Maps an entity to sql and from sql records.
 *
 * @param model
 */
open class EntityMapper(model: Model, persistAsUtc:Boolean = false, encryptor:Encryptor? = null) : ModelMapper(model, _encryptor = encryptor) {

    protected val dateFormat    :DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    protected val timeFormat    :DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    protected val dateTimeFormat:DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    fun mapToSql(item: Any, update: Boolean, fullSql: Boolean = false): String {

        return if (!_model.any)
            ""
        else
            mapFields(item, update, fullSql)
    }


    private fun mapFields(item: Any, update: Boolean, fullSql: Boolean = false): String {
        var dat = ""
        var sql = ""
        var cols = ""

        val len = _model.fields.size
        for (ndx in 0..len - 1) {
            val mapping = _model.fields[ndx]
            val propName = mapping.name
            val colName = getColumnName(mapping.storedName)
            val include = propName != "id"

            if (include) {
                // =======================================================
                // NOTE: Refactor this to use pattern matching ?
                // Similar to the Mapper class but reversed
                val data = if (mapping.dataType == KTypes.KStringClass) {
                    val sVal = Reflector.getFieldValue(item, mapping.name) as String

                    // Only encrypt on create
                    val sValEnc = if(!update && mapping.encrypt) _encryptor?.encrypt(sVal) ?: sVal else sVal
                    val sValFinal = sValEnc.nonEmptyOrDefault("")
                    "'" + QueryEncoder.ensureValue(sValFinal) + "'"
                }
                else if (mapping.dataType == KTypes.KBoolClass) {
                    val bVal = Reflector.getFieldValue(item, mapping.name) as Boolean
                    if (bVal) "1" else "0"
                }
                else if (mapping.dataType == KTypes.KShortClass) {
                    val iVal = Reflector.getFieldValue(item, mapping.name) as Short
                    iVal.toString()
                }
                else if (mapping.dataType == KTypes.KIntClass) {
                    val iVal = Reflector.getFieldValue(item, mapping.name) as Int
                    iVal.toString()
                }
                else if (mapping.dataType == KTypes.KLongClass) {
                    val lVal = Reflector.getFieldValue(item, mapping.name) as Long
                    lVal.toString()
                }
                else if (mapping.dataType == KTypes.KFloatClass) {
                    val dVal = Reflector.getFieldValue(item, mapping.name) as Float
                    dVal.toString()
                }
                else if (mapping.dataType == KTypes.KDoubleClass) {
                    val dVal = Reflector.getFieldValue(item, mapping.name) as Double
                    dVal.toString()
                }
                else if (mapping.dataType == KTypes.KDateTimeClass) {
                    val dtVal = Reflector.getFieldValue(item, mapping.name) as DateTime
                    "'" + dtVal.toStringMySql() + "'"
                }
                else if (mapping.dataType == KTypes.KLocalDateClass) {
                    val raw = Reflector.getFieldValue(item, mapping.name) as LocalDate
                    //val dtVal = java.sql.Date.valueOf(raw)
                    "'" + raw.format(dateFormat) + "'"
                }
                else if (mapping.dataType == KTypes.KLocalTimeClass) {
                    val raw = Reflector.getFieldValue(item, mapping.name) as LocalTime
                    //val dtVal = java.sql.Time.valueOf(raw)
                    "'" + raw.format(timeFormat) + "'"
                }
                else if (mapping.dataType == KTypes.KLocalDateTimeClass) {
                    val raw = Reflector.getFieldValue(item, mapping.name) as LocalDateTime
                    val converted = if(_settings.persisteUTCDate) DateTime.of(raw).atUtc().local() else raw
                    "'" + converted.format(dateTimeFormat) + "'"
                }
                else if (mapping.dataType == KTypes.KZonedDateTimeClass) {
                    val raw = Reflector.getFieldValue(item, mapping.name) as ZonedDateTime
                    val converted = if(_settings.persisteUTCDate) DateTime.of(raw).atUtc().raw else raw
                    "'" + converted.format(dateTimeFormat) + "'"
                }
                else if (mapping.dataType == KTypes.KInstantClass) {
                    val raw = Reflector.getFieldValue(item, mapping.name) as Instant
                    //val dtVal = java.sql.Timestamp.valueOf(raw.toLocalDateTime())
                    "'" + LocalDateTime.ofInstant(raw, ZoneId.systemDefault()).format(dateTimeFormat) + "'"
                }
                else // Object
                {
                    val objVal = Reflector.getFieldValue(item, mapping.name)
                    val data = objVal?.toString() ?: ""
                    "'" + QueryEncoder.ensureValue(data) + "'"
                }

                // Setup the inserts
                val isLastField = ndx == _model.fields.size - 1
                if (!update) {
                    cols += colName
                    dat += data
                    if (!isLastField) {
                        cols += ","
                        dat += ","
                    }
                }
                else {
                    sql += colName + "=" + data
                    if (!isLastField) {
                        sql += ","
                    }
                }
            }
        }

        if (update) {
            sql = " " + sql
        }
        else {
            cols = "(" + cols + ") "
            sql = cols + "VALUES (" + dat + ")"
        }
        val finalSql = if (!fullSql)
            sql
        else if (update)
            "update ${_model.name} set " + sql + ";"
        else
            "insert into ${_model.name} " + sql + ";"
        return finalSql
    }


    fun getColumnName(name: String): String = "`$name`"
}
