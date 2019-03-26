package slatekit.orm.databases.statements

import slatekit.meta.models.Model
import slatekit.entities.Entity
import slatekit.orm.OrmMapper

class Insert<TId, T> : Statement<TId, T> where TId: kotlin.Comparable<TId>, T: Entity<TId> {

    override fun sql(item: Entity<TId>, model: Model, mapper: OrmMapper<TId, T>): String {
        val table = mapper.tableName()
        val result = mapper.mapFields(null, item, model, false)
        val cols = "(" + result.joinToString( ",", transform = {it.first } ) + ") "
        val vals = "VALUES (" + result.joinToString( ",", transform = {it.second } ) +  ")"
        return "insert into $table $cols $vals;"
    }
}