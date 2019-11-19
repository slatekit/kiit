package slatekit.orm.databases.statements

import slatekit.orm.OrmMapper
import slatekit.meta.models.Model

interface Statement<TId, T> where TId: kotlin.Comparable<TId>, T: Any {

    fun sql(item: T, model: Model, mapper: OrmMapper<TId, T>):String
}