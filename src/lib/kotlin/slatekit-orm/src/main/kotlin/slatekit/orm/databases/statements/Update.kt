package slatekit.orm.databases.statements

import slatekit.entities.core.Entity
import slatekit.entities.core.EntityWithId
import slatekit.orm.core.OrmMapper
import slatekit.meta.Reflector
import slatekit.meta.models.Model

class Update<TId, T> : Statement<TId, T> where TId: kotlin.Comparable<TId>, T: Entity<TId> {

    override fun sql(item:Entity<TId>, model: Model, mapper: OrmMapper<TId, T>): String {
        // TODO: Have to support entity with other name for id
        val id = Reflector.getFieldValue(item, EntityWithId<*>::id.name)
        val table = mapper.tableName()
        val result = mapper.mapFields(null, item, model, true)
        val updates = " " + result.joinToString( ",", transform = {it.second } )
        return "update $table set $updates where id = $id;"
    }
}