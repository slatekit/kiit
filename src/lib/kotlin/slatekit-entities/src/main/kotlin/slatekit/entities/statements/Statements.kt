package slatekit.entities.statements

import slatekit.common.data.Mapper
import slatekit.entities.EntityMapper
import slatekit.entities.core.EntityInfo


/**
 * Contains common statements used to build Sql and/or Prepared Sql/statements
 */
interface Statements<TId, T> where TId: kotlin.Comparable<TId>, T: Any {
    /**
     * Builds insert statement sql/prepared sql
     */
    val insert: InsertStatement<TId, T>

    /**
     * Builds update statement sql/prepared sql
     */
    val update: UpdateStatement<TId, T>

    /**
     * Builds select statement sql/prepared sql
     */
    val select: SelectStatement<TId, T>

    /**
     * Builds delete statement sql/prepared sql
     */
    val delete: DeleteStatement<TId, T>
}


class SqlStatements<TId, T>(val info:EntityInfo, val mapper: EntityMapper<TId, T>) : Statements<TId, T> where TId: kotlin.Comparable<TId>, T: Any {
    override val insert = InsertStatement<TId, T>(info, mapper)
    override val update = UpdateStatement<TId, T>(info, mapper)
    override val select = SelectStatement<TId, T>(info, mapper)
    override val delete = DeleteStatement<TId, T>(info, mapper)
}



