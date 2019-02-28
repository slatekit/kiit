package slatekit.entities.services

import slatekit.entities.core.*
import kotlin.reflect.KClass


//interface ActiveRecord<TId,T> : EntityServices<TId,T> where TId: kotlin.Comparable<TId>, T:Entity<TId> {
//
//    val kls:KClass<T>
//
//    override fun repo(): IEntityRepo = repo()
//
//    override fun repoT(): EntityRepo<TId, T> = entities().getRepo(kls)
//
//    override fun entities(): Entities = ActiveDB.entities
//}