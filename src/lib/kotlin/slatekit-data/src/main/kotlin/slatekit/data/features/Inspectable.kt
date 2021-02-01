package slatekit.data.features

import slatekit.data.core.Meta

interface Inspectable<TId, T> where TId : Comparable<TId>, T:Any {
    val meta: Meta<TId, T>

    /**
     * Gets the mapped column name for the fiel name supplied
     */
    fun columnName(fieldName:String):String = fieldName
}
