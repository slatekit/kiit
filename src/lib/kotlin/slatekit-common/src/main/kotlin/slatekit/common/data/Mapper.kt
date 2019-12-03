package slatekit.common.data

import slatekit.common.Record


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

    fun encode(model:T, action: DataAction): Values

    fun decode(record: Record): T?
}