package slatekit.data.sql

import slatekit.common.data.*
import slatekit.query.*

/**
 * Query Builders
 */
class Builders {
    /**
     * Delete Query builder
     */
    class Delete(val dialect: Dialect,
                 val table: String,
                 val types: (String) -> DataType,
                 val columns: ((String) -> String)? = null
    ) : slatekit.query.Delete() {

        override fun build(mode: BuildMode): Command {
            val tableName =  dialect.encode(source ?: table)
            val buffer = StringBuilder()
            val values = mutableListOf<Value>()

            // Delete
            buffer.append("delete from $tableName")

            // Where
            where?.let {
                buffer.append(" where ")
                filter(dialect, columns, types, mode, buffer, it, values)
            }

            // Order By
            if(orders.isNotEmpty()) {
                orders(dialect, orders, buffer, columns)
            }

            // Limit
            limit?.let { limit(mode, it, buffer, values) }

            // End
            buffer.append(";")

            return command(mode, buffer, values)
        }
    }


    class Select(val dialect: Dialect,
                 val table: String,
                 val types: (String) -> DataType,
                 val columns: ((String) -> String)? = null
    ) : slatekit.query.Select() {
        override fun build(mode: BuildMode): Command {
            val tableName =  dialect.encode(source ?: table)
            val buffer = StringBuilder()
            val values = mutableListOf<Value>()

            // Delete
            buffer.append("select * from $tableName ")

            // Where
            where?.let {
                buffer.append("where ")
                filter(dialect, columns, types, mode, buffer, it, values)
            }

            // Order By
            if(orders.isNotEmpty()) {
                orders(dialect, orders, buffer, columns)
            }

            // Limit
            limit?.let { limit(mode, it, buffer, values) }

            // End
            buffer.append(";")

            return command(mode, buffer, values)
        }
    }


    class Patch(val dialect: Dialect,
                val table: String,
                val types: (String) -> DataType,
                val columns: ((String) -> String)? = null) : slatekit.query.Update() {
        override fun build(mode: BuildMode): Command {
            return Command("", listOf(), listOf())
        }
    }


    companion object {

        fun command(mode: BuildMode, buffer: StringBuilder, values: MutableList<Value>):Command {
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


        fun orders(dialect: Dialect, orders:MutableList<Pair<String, Order>>, buffer: StringBuilder, columns: ((String) -> String)?) {
            // Order By
            if(orders.isNotEmpty()) {
                buffer.append(" order by ")
                orders.forEachIndexed { ndx, order ->
                    val rawColumn = columns?.invoke(order.first) ?: order.first
                    val column = dialect.encode(rawColumn)
                    val prefix = if(ndx > 0) ", " else ""
                    buffer.append("$prefix$column ${order.second.text}")
                }
            }
        }


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
         * Recursively build the filters
         * @param mode: Sql or Prepared statement mode
         * @param buffer: Holds the sql being built
         * @param expr: Current expression to evaluate
         * @param values: Stores values to use when in prepared statement mode e.g. sql = ?, value = 1
         */
        fun filter(dialect: Dialect, columns: ((String) -> String)?, types: (String) -> DataType, mode: BuildMode, buffer: StringBuilder, expr: Expr, values: MutableList<Value>) {
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
                    filter(dialect, columns, types, mode, buffer, expr.left, values)
                    buffer.append(" ${expr.op.text} ")
                    filter(dialect, columns, types, mode, buffer, expr.right, values)
                    buffer.append(")")
                }
            }
        }
    }
}
