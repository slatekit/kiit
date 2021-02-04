package slatekit.query

import java.util.concurrent.atomic.AtomicInteger

/**
 * Interface for building a Select statement with criteria
 */
abstract class Select(converter: ((String) -> String)? = null,
                         encoder:((String) -> String)? = null)
    : CriteriaBase<Select>(converter, encoder), Stmt {
    private var agg: Agg? = null
    private val fields = mutableListOf<String>()
    private val limit = AtomicInteger(0)
    private val orders = mutableListOf<Pair<String, Order>>()

    /**
     * Get all fields
     */
    fun all(): Select {
        return this
    }


    fun agg(name:String, field:String): Select {
        agg = Agg(name, field)
        return this
    }


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
}
