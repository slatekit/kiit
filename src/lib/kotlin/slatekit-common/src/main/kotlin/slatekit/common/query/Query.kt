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
import kotlin.reflect.KProperty


/**
 * Created by kreddy on 12/24/2015.
 */
open class Query : IQuery {

    class QueryData(val conditions: MutableList<ICondition>,
                    val updates: MutableList<FieldValue>)



    protected val _limit = AtomicInteger(0)
    protected val EmptyString = "''"
    protected val _data = QueryData(mutableListOf(), mutableListOf())


    override fun toUpdates(): List<FieldValue> = _data.updates.toList()


    override fun toUpdatesText(): String {
        // No updates ?
        return if (_data.updates.size > 0) {
            // Build up the sql
            val text = mutableListOf("SET ")

            // Each update
            val updates = _data.updates.joinToString(",", transform = { u ->
                when (u.fieldValue) {
                    ""   -> u.field + "=" + "''"
                    else -> u.field + "=" + QueryEncoder.convertVal(u.fieldValue)
                }
            })
            text += updates

            // Filters
            if (anyConditions()) text += " WHERE " + toFilter()
            if (anyLimit()) text += " LIMIT " + _limit

            val sql = text.reduce { a, b -> a + b }
            sql
        }
        else
            ""
    }


    override fun toFilter(): String {
        val filter = _data.conditions.joinToString(",", transform = { c -> c.toStringQuery() })
        return filter
    }


    /**
     * builds up a set field clause
     *
     * @param field
     * @param fieldValue
     * @return
     */
    override fun set(field: String, fieldValue: Any): IQuery {
        val col = QueryEncoder.ensureField(field)
        _data.updates.add(FieldValue(col, fieldValue))
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
    override fun where(field: String, compare: String, fieldValue: Any): IQuery {
        val condition = buildCondition(field, compare, fieldValue)
        _data.conditions.add(condition)
        return this
    }


    /**
     * builds up a where clause with the supplied arguments
     *
     * @param field:  The property reference
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun where(field: KProperty<*>, compare: String, fieldValue: Any): IQuery {
        val condition = buildCondition(field.name, compare, fieldValue)
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
    override fun where(field: String, compare: Op, fieldValue: Any): IQuery =
            where(field, compare.value, fieldValue)


    /**
     * adds an and clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun and(field: String, compare: String, fieldValue: Any): IQuery {
        val cond = buildCondition(field, compare, fieldValue)
        group("and", cond)
        return this
    }


    /**
     * adds an and clause with the supplied arguments
     *
     * @param field:  The property reference
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun and(field: KProperty<*>, compare: String, fieldValue: Any): IQuery {
        val cond = buildCondition(field.name, compare, fieldValue)
        group("and", cond)
        return this
    }


    override fun and(field: String, compare: Op, fieldValue: Any): IQuery =
            and(field, compare.value, fieldValue)


    /**
     * adds an or clause with the supplied arguments
     *
     * @param field:  The field name
     * @param compare: The comparison operator ( =, >, >=, <, <=, != )
     * @param fieldValue: The field value
     * @return this instance
     */
    override fun or(field: String, compare: String, fieldValue: Any): IQuery {
        val cond = buildCondition(field, compare, fieldValue)
        group("or", cond)
        return this
    }


    override fun or(field: String, compare: Op, fieldValue: Any): IQuery =
            or(field, compare.value, fieldValue)


    override fun limit(max: Int): IQuery {
        this._limit.set(max)
        return this
    }


    override fun orderBy(field: String): IQuery = this


    override fun asc(): IQuery = this


    override fun desc(): IQuery = this


    protected fun buildCondition(field: String, compare: String, fieldValue: Any): Condition {
        val col = QueryEncoder.ensureField(field)
        val comp = QueryEncoder.ensureCompare(compare)
        val con = Condition(col, comp, fieldValue)
        return con
    }


    protected fun group(op: String, condition: Condition): Unit {
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
}
