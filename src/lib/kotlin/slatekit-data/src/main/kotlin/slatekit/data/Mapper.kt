package slatekit.data

import slatekit.common.Record
import slatekit.common.crypto.Encryptor
import slatekit.common.data.DataAction
import slatekit.common.data.DataType
import slatekit.common.data.Values


/**
 * Handles mapping an entity to/from a Record.
 * If using the EntityMapper, clients need to implement the methods here.
 *
 * DESIGN:
 * This is designed to:
 * 1. allow clients to map classes to records WHAT EVER WAY they want.
 * 2. only enforce types on Entity Id/Type
 * 3. support optional schema ( represented as a Model ) for mappings
 *
 * NOTE:
 * We are explicitly avoiding mapping via reflection and allows callers
 * to control how this is done. This also allows for potentially hooking
 * into some other mapping library to handle the heavy / tedious work.
 */
interface Mapper<TId, T> where TId : Comparable<TId> {

    /**
     * Gets the table column name mapped to the field name
     */
    fun column(field:String): String = field

    /**
     * Gets the data type of the field
     */
    fun datatype(field:String): DataType

    /**
     * Gets the encode values for the model to be used for building a sql statement
     */
    fun encode(model:T, action: DataAction, enc: Encryptor?): Values

    /**
     * Decodes the record into the model
     */
    fun decode(record: Record, enc:Encryptor?): T?
}
