package slatekit.orm.databases.statements

import slatekit.entities.core.Entity
import slatekit.orm.core.OrmMapper
import slatekit.meta.models.Model

interface Statement<TId, T> where TId: kotlin.Comparable<TId>, T: Entity<TId> {

    fun sql(item:Entity<TId>, model: Model, mapper: OrmMapper<TId, T>):String
}