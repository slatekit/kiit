package slatekit.entities.core

import slatekit.entities.Entities
import slatekit.entities.Entity
import slatekit.entities.EntityRepo

interface ServiceSupport<TId, T> where TId : Comparable<TId>, T : Entity<TId> {

    fun repo(): IEntityRepo
    fun repoT(): EntityRepo<TId, T>
    fun entities(): Entities

    /**
     * Hook for derived to apply any other logic/field changes before create/update
     * @param mode
     * @param entity
     * @return
     */
    fun applyFieldData(mode: Int, entity: T): T {
        return entity
    }
}
