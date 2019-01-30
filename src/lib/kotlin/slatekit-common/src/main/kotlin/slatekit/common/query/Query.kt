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

package slatekit.common.query

import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by kreddy on 12/24/2015.
 */
open class Query : IQuery {

    class QueryData(
        val conditions: MutableList<ICondition>,
        val updates: MutableList<FieldValue>
    )

    protected val _limit = AtomicInteger(0)
    protected val EmptyString = "''"
    protected val _data = QueryData(mutableListOf(), mutableListOf())
    protected val _joins = mutableListOf<Triple<String, String, String>>()
    protected val _orders = mutableListOf<Pair<String, String>>()

    override fun toUpdates(): List<FieldValue> = _data.updates.toList()

    override fun toUpdatesText(): String {
        // No updates ?
        return if (_data.updates.size > 0) {
            // Build up the sql
            val text = mutableListOf("SET ")

            // Each update
            val updates = _data.updates.joinToString(",", transform = { u ->
                when (u.fieldValue) {
                    "" -> u.field + "=" + "''"
                    else -> u.field + "=" + QueryEncoder.convertVal(u.fieldValue)
                }
            })
            text += updates

            // Filters
            if (anyConditions()) text += " WHERE " + toFilter()
            if (anyLimit()) text += " LIMIT " + _limit

            val sql = text.reduce { a, b -> a + b }
            sql
        } else
            ""
    }

    override fun toFilter(): String {

        // 1. Handle the joins
        val joins = if (_joins.isEmpty())
            ""
        else
            " " + _joins.joinToString(",", transform = { (modelRaw, modelFieldRaw, refFieldRaw) ->
                val model = QueryEncoder.ensureField(modelRaw)
                val modelField = QueryEncoder.ensureField(modelFieldRaw)
                val refField = QueryEncoder.ensureField(refFieldRaw)
                " join $model on $modelField = $refField"
            }) + " "

        // 2. Where / and / or conditions
        val conditions = _data.conditions.joinToString(",", transform = { c -> c.toStringQuery() })

        // 3. Order by clauses
        val orders = if (_orders.isEmpty())
            ""
        else
            " order by " + _orders.joinToString(",", transform = { (fieldRaw, modeRaw) ->
                val mode = when (modeRaw.toLowerCase()) {
                    Query.Asc -> "asc"
                    Query.Desc -> "desc"
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
        val finalValue = fieldValue ?: Query.Null
        val col = QueryEncoder.ensureField(field)
        _data.updates.add(FieldValue(col, finalValue))
        return this
    }

    /**
     * builds up a set field clause
     *
     * @param pairs: vararg of Pair representing the field names and values to set
     * @return
     */
    override fun set(vararg pairs:Pair<String, Any>): IQuery {
        pairs.forEach {
            val col = QueryEncoder.ensureField(it.first)
            _data.updates.add(FieldValue(col, it.second))
        }
        return this
    }


    /**
     * builds up a where clause with the supplied arguments
     *
     * @param field:  The field name
     * @param op: The comparison operator ( =, >, >=, <, <=, !=, in )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun where(field: String, op: String, fieldValue: Any?): IQuery {
        val finalValue = fieldValue ?: Query.Null
        val condition = buildCondition(field, op, finalValue)
        _data.conditions.add(condition)
        return this
    }

    /**
     * builds up a where clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun where(field: String, compare: Op, fieldValue: Any?): IQuery {
        val finalValue = fieldValue ?: Query.Null
        return where(field, compare.text, finalValue)
    }

    /**
     * adds an and clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun and(field: String, compare: String, fieldValue: Any?): IQuery {
        val finalValue = fieldValue ?: Query.Null
        val cond = buildCondition(field, compare, finalValue)
        group("and", cond)
        return this
    }

    override fun and(field: String, compare: Op, fieldValue: Any?): IQuery {
        val finalValue = fieldValue ?: Query.Null
        return and(field, compare.text, finalValue)
    }

    /**
     * adds an or clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun or(field: String, compare: String, fieldValue: Any?): IQuery {
        val finalValue = fieldValue ?: Query.Null
        val cond = buildCondition(field, compare, finalValue)
        group("or", cond)
        return this
    }

    override fun or(field: String, compare: Op, fieldValue: Any?): IQuery {
        val finalValue = fieldValue ?: Query.Null
        return or(field, compare.text, finalValue)
    }

    override fun limit(max: Int): IQuery {
        this._limit.set(max)
        return this
    }

    override fun orderBy(field: String, mode: String): IQuery {
        this._orders.add(Pair(field, mode))
        return this
    }

    override fun join(model: String, modelField: String, refField: String): IQuery {
        this._joins.add(Triple(model, modelField, refField))
        return this
    }

    protected fun buildCondition(field: String, op: String, fieldValue: Any): Condition {
        val col = QueryEncoder.ensureField(field)
        val comparison = if (fieldValue == Query.Null) {
            val comp = when (op) {
                "=" -> "is"
                "is" -> "is"
                "!=" -> "is not"
                "<>" -> "is not"
                "in" -> "in"
                else -> "is"
            }
            Pair(comp, "null")
        } else {
            val comp = QueryEncoder.ensureCompare(op)
            Pair(comp, fieldValue)
        }
        val con = Condition(col, comparison.first, comparison.second)
        return con
    }

    protected fun group(op: String, condition: Condition) {
        // Pop the last one
        val last = _data.conditions.size - 1
        val left = _data.conditions[last]
        _data.conditions.removeAt(last)

        // Build a binary condition from left and right
        val group = ConditionGroup(left, op, condition)

        // Push back on condition list
        _data.conditions.add(group)
    }

    protected fun anyLimit(): Boolean = _limit.get() > 0

    protected fun anyConditions(): Boolean = _data.conditions.isNotEmpty()

    companion object {
        @JvmStatic val Null = "null"
        @JvmStatic val Asc = "asc"
        @JvmStatic val Desc = "desc"
    }
}
