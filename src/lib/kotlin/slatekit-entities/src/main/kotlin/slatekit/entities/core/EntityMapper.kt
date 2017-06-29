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


import slatekit.common.*
import slatekit.common.mapper.Mapper
import slatekit.common.query.QueryEncoder


/**
 * Maps an entity to sql and from sql records.
 *
 * @param model
 */
class EntityMapper(model: Model) : Mapper(model) {

    fun mapToSql(item: Any, update: Boolean, fullSql: Boolean = false): String {

        return if (!_model.any)
            Strings.empty
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
                // Similar to the Mapper.scala class but reversed
                val data = if (mapping.dataType == Types.StringClass) {
                    val sVal = Reflector.getFieldValue(item, mapping.name) as String
                    val sValFinal = Strings.valueOrDefault(sVal, "")
                    "'" + QueryEncoder.ensureValue(sValFinal) + "'"
                }
                else if (mapping.dataType == Types.BoolClass) {
                    val bVal = Reflector.getFieldValue(item, mapping.name) as Boolean
                    if (bVal) "1" else "0"
                }
                else if (mapping.dataType == Types.ShortClass) {
                    val iVal = Reflector.getFieldValue(item, mapping.name) as Short
                    iVal.toString()
                }
                else if (mapping.dataType == Types.IntClass) {
                    val iVal = Reflector.getFieldValue(item, mapping.name) as Int
                    iVal.toString()
                }
                else if (mapping.dataType == Types.LongClass) {
                    val lVal = Reflector.getFieldValue(item, mapping.name) as Long
                    lVal.toString()
                }
                else if (mapping.dataType == Types.FloatClass) {
                    val dVal = Reflector.getFieldValue(item, mapping.name) as Float
                    dVal.toString()
                }
                else if (mapping.dataType == Types.DoubleClass) {
                    val dVal = Reflector.getFieldValue(item, mapping.name) as Double
                    dVal.toString()
                }
                else if (mapping.dataType == Types.DateClass) {
                    val dtVal = Reflector.getFieldValue(item, mapping.name) as DateTime
                    "'" + dtVal.toStringMySql() + "'"
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
