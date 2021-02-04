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

package slatekit.query

import slatekit.common.data.Value
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by kreddy on 12/24/2015.
 */
open class Query : IQuery {

    class QueryData(
        val conditions: MutableList<Expr>,
        val updates: MutableList<Set>
    )

    protected val limit = AtomicInteger(0)
    protected val data = QueryData(mutableListOf(), mutableListOf())
    protected val joins = mutableListOf<Triple<String, String, String>>()
    protected val orders = mutableListOf<Pair<String, String>>()

    override fun hasOrderBy(): Boolean = !orders.isEmpty()

    override fun getOrderBy(): String = orders.joinToString(",") { it.first + it.second }

    override fun toUpdates(): List<Set> = data.updates.toList()

    override fun toUpdatesText(): String {
        // No updates ?
        return if (data.updates.size > 0) {
            // Build up the sql
            val text = mutableListOf("SET ")

            // Each update
            val updates = data.updates.joinToString(",", transform = { u ->
                when (u.fieldValue) {
                    "" -> u.field + "=" + "''"
                    else -> u.field + "=" + QueryEncoder.convertVal(u.fieldValue)
                }
            })
            text += updates

            // Filters
            if (anyConditions()) text += " WHERE " + toFilter()
            if (anyLimit()) text += " LIMIT " + limit

            val sql = text.reduce { a, b -> a + b }
            sql
        } else
            ""
    }

    override fun toFilter(): String {

        // 1. Handle the joins
        val joins = if (joins.isEmpty())
            ""
        else
            " " + joins.joinToString(",", transform = { (modelRaw, modelFieldRaw, refFieldRaw) ->
                val model = QueryEncoder.ensureField(modelRaw)
                val modelField = QueryEncoder.ensureField(modelFieldRaw)
                val refField = QueryEncoder.ensureField(refFieldRaw)
                " join $model on $modelField = $refField"
            }) + " "

        // 2. Where / and / or conditions
        val conditions = data.conditions.joinToString(",", transform = { c -> c.toStringQuery() })

        // 3. Order by clauses
        val orders = if (orders.isEmpty())
            ""
        else
            " order by " + orders.joinToString(",", transform = { (fieldRaw, modeRaw) ->
                val mode = when (modeRaw.toLowerCase()) {
                    Const.Asc -> "asc"
                    Const.Desc -> "desc"
                    else -> QueryEncoder.convertVal(modeRaw)
                }
                val field = QueryEncoder.ensureField(fieldRaw)
                "$field $mode"
            })
        val filter = joins + conditions + orders
        return filter
    }

    /**
     * builds up a set field clause
     *
     * @param field
     * @param fieldValue
     * @return
     */
    override fun set(field: String, fieldValue: Any?): IQuery {
        val finalValue = fieldValue ?: Const.Null
        val col = QueryEncoder.ensureField(field)
        data.updates.add(Set(col, finalValue))
        return this
    }

    /**
     * builds up a set field clause
     *
     * @param pairs: vararg of Pair representing the field names and values to set
     * @return
     */
    override fun set(vararg pairs: Pair<String, Any>): IQuery {
        pairs.forEach {
            val col = QueryEncoder.ensureField(it.first)
            data.updates.add(Set(col, it.second))
        }
        return this
    }


    /**
     * builds up a set field clause
     *
     * @param pairs: vararg of Pair representing the field names and values to set
     * @return
     */
    override fun set(pairs: List<Value>): IQuery {
        pairs.forEach {
            val col = QueryEncoder.ensureField(it.name)
            data.updates.add(Set(col, it.value))
        }
        return this
    }

    /**
     * builds up a set field clause
     *
     * @param pairs: vararg of Pair representing the field names and values to set
     * @return
     */
    override fun set(vararg pairs: Value): IQuery {
        pairs.forEach {
            val col = QueryEncoder.ensureField(it.name)
            data.updates.add(Set(col, it.value))
        }
        return this
    }

    override fun limit(max: Int): IQuery {
        this.limit.set(max)
        return this
    }

    override fun orderBy(field: String, mode: String): IQuery {
        this.orders.add(Pair(field, mode))
        return this
    }

    override fun join(model: String, modelField: String, refField: String): IQuery {
        this.joins.add(Triple(model, modelField, refField))
        return this
    }

    override fun where(field: String, op: String, fieldValue: Any?): IQuery {
        TODO("Not yet implemented")
    }

    override fun where(field: String, compare: Op, fieldValue: Any?): IQuery {
        TODO("Not yet implemented")
    }

    override fun and(field: String, compare: String, fieldValue: Any?): IQuery {
        TODO("Not yet implemented")
    }

    override fun and(field: String, compare: Op, fieldValue: Any?): IQuery {
        TODO("Not yet implemented")
    }

    override fun or(field: String, compare: String, fieldValue: Any?): IQuery {
        TODO("Not yet implemented")
    }

    override fun or(field: String, compare: Op, fieldValue: Any?): IQuery {
        TODO("Not yet implemented")
    }

    override fun group(op: String, condition: Condition) {
        TODO("Not yet implemented")
    }

    protected fun anyLimit(): Boolean = limit.get() > 0

    protected fun anyConditions(): Boolean = data.conditions.isNotEmpty()

}
