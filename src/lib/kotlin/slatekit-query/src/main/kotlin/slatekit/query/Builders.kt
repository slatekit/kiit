package slatekit.query

import slatekit.common.data.Command
import slatekit.common.data.Value
import java.util.concurrent.atomic.AtomicInteger


/**
 * Interface for building a Select statement with criteria
 */
abstract class Select(converter: ((String) -> String)? = null,
                         encoder:((String) -> String)? = null)
    : CriteriaBase<Select>(converter, encoder), Stmt {
    private val limit = AtomicInteger(0)
    private val orders = mutableListOf<Pair<String, Order>>()

    /**
     * Limit x number of records
     */
    fun limit(max: Int): Select {
        this.limit.set(max)
        return this
    }


    /**
     * Order by field1 asc, field2 desc
     */
    fun orderBy(field: String, order: Order): Select {
        this.orders.add(Pair(field, order))
        return this
    }


    /**
     * Builds the select command
     */
    override fun build(): Command {
        return Command("", listOf(), listOf())
    }
}


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


    /**
     * Builds the select command
     */
    override fun build(): Command {
        return Command("", listOf(), listOf())
    }
}


/**
 * Interface for building a delete statement with criteria
 */
open class Where(converter: ((String) -> String)? = null,
                  encoder:((String) -> String)? = null)
    : CriteriaBase<Where>(converter, encoder), Stmt {

    /**
     * Builds the select command
     */
    override fun build(): Command {
        if(conditions.isEmpty()){
            return Command("", listOf(), listOf())
        }
        return Command("", listOf(), listOf())
    }
}
