package slatekit.orm.databases.statements

import slatekit.meta.models.Model
import slatekit.entities.Entity
import slatekit.orm.OrmMapper


class Select<TId, T> : Statement<TId, T> where TId: kotlin.Comparable<TId>, T:Any  {

    override fun sql(item: T, model: Model, mapper: OrmMapper<TId, T>): String {
        val id = (item as Entity<*>).identity()
        val table = mapper.tableName()
        return "select * from $table where id = $id"
    }
}