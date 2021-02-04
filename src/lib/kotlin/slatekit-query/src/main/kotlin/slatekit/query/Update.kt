package slatekit.query

import slatekit.common.data.Value

/**
 * Interface for building an update statement with criteria
 */
abstract class Update(converter: ((String) -> String)? = null,
                    encoder:((String) -> String)? = null)
    : CriteriaBase<Update>(converter, encoder), Stmt {
    private val updates = mutableListOf<Set>()

    /**
     * builds up a set field clause
     *
     * @param field
     * @param fieldValue
     * @return
     */
    fun set(field: String, fieldValue: Any?): Update {
        val finalValue = fieldValue ?: Const.Null
        val col = QueryEncoder.ensureField(field)
        updates.add(Set(col, finalValue))
        return this
    }

    /**
     * builds up a set field clause
     *
     * @param pairs: vararg of Pair representing the field names and values to set
     * @return
     */
    fun set(vararg pairs: Pair<String, Any>): Update {
        pairs.forEach {
            val col = QueryEncoder.ensureField(it.first)
            updates.add(Set(col, it.second))
        }
        return this
    }


    /**
     * builds up a set field clause
     *
     * @param pairs: vararg of Pair representing the field names and values to set
     * @return
     */
    fun set(pairs: List<Value>): Update {
        pairs.forEach {
            val col = QueryEncoder.ensureField(it.name)
            updates.add(Set(col, it.value))
        }
        return this
    }

    /**
     * builds up a set field clause
     *
     * @param pairs: vararg of Pair representing the field names and values to set
     * @return
     */
    fun set(vararg pairs: Value): Update {
        pairs.forEach {
            val col = QueryEncoder.ensureField(it.name)
            updates.add(Set(col, it.value))
        }
        return this
    }
}
