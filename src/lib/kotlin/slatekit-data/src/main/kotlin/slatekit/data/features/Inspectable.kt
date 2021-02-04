package slatekit.data.features

import slatekit.data.core.Meta
import slatekit.data.sql.Dialect

interface Inspectable<TId, T> : Criteria<TId, T> where TId : Comparable<TId>, T:Any {
    val meta: Meta<TId, T>

    val dialect:Dialect

    /**
     * Gets the mapped column name for the fiel name supplied
     */
    fun columnName(fieldName:String):String = fieldName
}
