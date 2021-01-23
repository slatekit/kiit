package slatekit.data.syntax

import slatekit.common.data.Mapper
import slatekit.data.core.Meta

/**
 * Contains syntax builders to build Sql and/or Prepared Sql/statements
 */
interface Syntax<TId, T> where TId: kotlin.Comparable<TId>, T: Any {
    /**
     * Builds insert statement sql/prepared sql
     */
    val insert: Insert<TId, T>

    /**
     * Builds update statement sql/prepared sql
     */
    val update: Update<TId, T>

    /**
     * Builds select statement sql/prepared sql
     */
    val select: Select<TId, T>

    /**
     * Builds delete statement sql/prepared sql
     */
    val delete: Delete<TId, T>

    /**
     * Using to build conditions
     */
    val filters:Filters
}


class SqlSyntax<TId, T>(val info: Meta<TId, T>, val mapper: Mapper<TId, T>) : Syntax<TId, T> where TId: kotlin.Comparable<TId>, T: Any {
    override val filters = Filters()
    override val insert = Insert<TId, T>(info, mapper)
    override val update = Update<TId, T>(info, mapper)
    override val select = Select<TId, T>(info, mapper, filters)
    override val delete = Delete<TId, T>(info, mapper, filters)
}



