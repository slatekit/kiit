package slatekit.orm.databases.statements

import slatekit.entities.Entity
import slatekit.orm.OrmMapper
import slatekit.meta.models.Model

interface Statement<TId, T> where TId: kotlin.Comparable<TId>, T: Entity<TId> {

    fun sql(item: Entity<TId>, model: Model, mapper: OrmMapper<TId, T>):String
}