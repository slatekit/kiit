package kiit.entities.core

import kiit.common.data.DataAction
import kiit.entities.Entity
import kiit.entities.EntityRepo

interface EntityOps<TId, T> where TId : Comparable<TId>, T : Entity<TId> {

    fun repo(): EntityRepo<TId, T>

    /**
     * Hook for derived to apply any other logic/field changes before create/update
     * @param mode
     * @param entity
     * @return
     */
    fun applyFieldData(mode: DataAction, entity: T): T {
        return entity
    }

    fun isCreated(id: TId): Boolean {
        return repo().meta.id.isPersisted(id)
    }

    fun columnName(name:String):String {
        // Get column name from model schema ( if available )
        val column = name
        //val encoded = QueryEncoder.ensureField(column)
        return column
    }
}


