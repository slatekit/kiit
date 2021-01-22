package slatekit.data.core

/**
 * @param name       : Name of the table e.g. "user"
 * @param encodeChar : Char used to encode the terms ( table/column names ) e.g. mysql "`"
 * @param pkey       : Primary key info ( name, type )
 */
data class Table(val name:String, val encodeChar:Char, val pkey: PKey)
