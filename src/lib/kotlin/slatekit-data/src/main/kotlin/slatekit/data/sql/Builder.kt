package slatekit.data.sql

import slatekit.common.data.*
import slatekit.data.sql.Dialect
import slatekit.query.*

interface Builder {
    val dialect: Dialect
    val table: String
    val types: (String) -> DataType
    val columns: ((String) -> String)?

    /**
     * Used to build the where, order by, limit clauses
     * This is common between select, update, delete
     * @param mode: Whether to build full sql or prepared statements
     * @param buffer: Buffer to hold the string sql
     * @param values: Stores the values to use for prepared statements
     */
    fun clauses(mode: BuildMode,
                buffer:StringBuilder,
                values: MutableList<Value>,
                where: Expr?,
                orders: MutableList<Pair<String, Order>>,
                limit:Int?) {
        // Where
        where?.let {
            buffer.append(" where ")
            filter(dialect, mode, buffer, it, values)
        }

        // Order By
        if(orders.isNotEmpty()) {
            orders(orders, buffer)
        }

        // Limit
        limit?.let { limit(mode, it, buffer, values) }
    }


    /**
     * Last step of construction, builds the @see[slatekit.common.data.Command] containing
     * 1. the sql statement as text e.g. "select * from `user` where `id` = 1
     * 2. the values if used prepared statement mode ( e.g. `id`, 1 )
     * @param mode: Whether to build full sql or prepared statements
     * @param buffer: Buffer to hold the string sql
     * @param values: Stores the values to use for prepared statements
     */
    fun command(mode: BuildMode, buffer: StringBuilder, values: MutableList<Value>): Command {
        return when (mode) {
            BuildMode.Sql -> {
                val stmt = buffer.toString()
                Command(stmt, listOf(), listOf())
            }
            BuildMode.Prep -> {
                val stmt = buffer.toString()
                Command(stmt, values.toList(), listOf())
            }
        }
    }


    /**
     * Builds the order by clause, this also encodes the field names to column names
     * 1. `column1` asc
     * 2. `column1` desc
     */
    fun orders(orders:MutableList<Pair<String, Order>>, buffer: StringBuilder) {
        // Order By
        if(orders.isNotEmpty()) {
            buffer.append(" order by ")
            orders.forEachIndexed { ndx, order ->
                val column = column(order.first)
                val prefix = if(ndx > 0) ", " else ""
                buffer.append("$prefix$column ${order.second.text}")
            }
        }
    }


    /**
     * Builds the limit clause
     * The encoded column is enclosed in the char for the dialect e.g. mysql = `
     * 1. limit 2
     * 2. limit ? ( value 2 is placed in values for prepared statement )
     */
    fun limit(mode: BuildMode, limit:Int?, buffer: StringBuilder, values: MutableList<Value>) {
        // Limit
        limit?.let {
            when (mode) {
                BuildMode.Sql -> {
                    buffer.append(" limit $limit")
                }
                BuildMode.Prep -> {
                    buffer.append(" limit ?")
                    values.add(Value("", DataType.DTInt, limit))
                }
            }
        }
    }


    /**
     * Maps the field name to an encoded ( enclosed ) column
     * The encoded column is enclosed in the char for the dialect e.g. mysql = `
     * 1. field -> column1
     * 2. column1 -> `column1`
     */
    fun column(field:String):String {
        val rawColumn = columns?.invoke(field) ?: field
        val column = dialect.encode(rawColumn)
        return column
    }


    /**
     * Recursively build the filters
     * @param mode: Sql or Prepared statement mode
     * @param buffer: Holds the sql being built
     * @param expr: Current expression to evaluate
     * @param values: Stores values to use when in prepared statement mode e.g. sql = ?, value = 1
     */
    fun filter(dialect: Dialect, mode: BuildMode, buffer: StringBuilder, expr: Expr, values: MutableList<Value>) {
        when (expr) {
            is Condition -> {
                // Get the actual name of the column ( could be different than field )
                val rawColumn = columns?.invoke(expr.field) ?: expr.field

                // Enclose it e.g. level = `level`
                val column = dialect.encode(rawColumn)

                // Data Type ( for used in setting prepared statement value )
                val type = types(expr.field)

                // Convert op e.g. "eq" -> "="
                val op = dialect.op(expr.op)

                when (mode) {
                    BuildMode.Sql -> {
                        val text = Encoding.convertVal(expr.value)
                        buffer.append("$column $op $text")
                    }
                    BuildMode.Prep -> {
                        if(expr.op == Op.In && expr.value is List<*>) {
                            val inputs = (expr.value as List<*>)
                            val delimited = inputs.joinToString(",") { it -> "?" }
                            val all = inputs.map { Value(column, type, it) }
                            buffer.append("$column $op ($delimited)")
                            values.addAll(all)
                        }
                        else {
                            buffer.append("$column $op ?")
                            values.add(Value(column, type, expr.value, null))
                        }
                    }
                }
            }
            is LogicalExpr -> {
                // Build "`level` = 1 and `category` = 2
                buffer.append("(")
                filter(dialect, mode, buffer, expr.left, values)
                buffer.append(" ${expr.op.text} ")
                filter(dialect, mode, buffer, expr.right, values)
                buffer.append(")")
            }
        }
    }
}
