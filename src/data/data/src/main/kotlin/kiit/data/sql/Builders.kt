package kiit.data.sql

import kiit.common.data.*


/**
 * Query Builders
 */
class Builders {
    /**
     * Delete Query builder
     */
    class Delete(override val dialect: Dialect,
                 override val schema: String,
                 override val table: String,
                 override val types: (String) -> DataType,
                 override val columns: ((String) -> String)? = null
    ) : kiit.query.Delete(), Builder  {

        override fun build(mode: BuildMode): Command {
            val tableName =  dialect.encode(schema,source ?: table)
            val buffer = StringBuilder()
            val values = mutableListOf<Value>()

            // Delete
            buffer.append("delete from $tableName")

            // Where, Order by, Limit
            clauses(mode, buffer, values, where, orders, limit)

            // End
            buffer.append(";")

            return command(mode, buffer, values)
        }
    }


    class Select(override val dialect: Dialect,
                 override val schema: String,
                 override val table: String,
                 override val types: (String) -> DataType,
                 override val columns: ((String) -> String)? = null
    ) : kiit.query.Select(), Builder {
        override fun build(mode: BuildMode): Command {
            val tableName =  dialect.encode(schema,source ?: table)
            val buffer = StringBuilder()
            val values = mutableListOf<Value>()

            // Select * or count(*)
            val selects = when(agg) {
                null -> "*"
                else -> {
                    val aggregate =agg!!
                    when(aggregate.name) {
                        dialect.aggr.count -> "count(*)"
                        dialect.aggr.avg   -> "avg(${column(aggregate.field)})"
                        dialect.aggr.min   -> "min(${column(aggregate.field)})"
                        dialect.aggr.max   -> "max(${column(aggregate.field)})"
                        dialect.aggr.sum   -> "sum(${column(aggregate.field)})"
                        else -> "*"
                    }
                }
            }
            buffer.append("select $selects from $tableName")

            // Where, Order by, Limit
            clauses(mode, buffer, values, where, orders, limit)

            // End
            buffer.append(";")
            return command(mode, buffer, values)
        }
    }


    class Patch(override val dialect: Dialect,
                override val schema: String,
                override val table: String,
                override val types: (String) -> DataType,
                override val columns: ((String) -> String)? = null) : kiit.query.Update(), Builder {

        override fun build(mode: BuildMode): Command {
            val tableName =  dialect.encode(schema,source ?: table)
            val buffer = StringBuilder()
            val values = mutableListOf<Value>()

            // Delete
            buffer.append("update $tableName")
            buffer.append(" set ")
            when(mode){
                BuildMode.Sql -> {
                    updates.forEachIndexed { ndx, update ->
                        val column = column(update.field)
                        val value = Encoding.convertVal(update.fieldValue)
                        val prefix = if(ndx > 0) ", " else ""
                        buffer.append("$prefix$column = $value")
                    }
                }
                BuildMode.Prep -> {
                    updates.forEachIndexed { ndx, update ->
                        val column = column(update.field)
                        val type = types(update.field)
                        val prefix = if(ndx > 0) ", " else ""
                        buffer.append("$prefix$column = ?")
                        values.add(Value(column, type, update.fieldValue, null))
                    }
                }
            }

            // Where, Order by, Limit
            clauses(mode, buffer, values, where, orders, limit)

            // End
            buffer.append(";")
            return command(mode, buffer, values)
        }
    }
}
