package slatekit.entities.core

import slatekit.common.data.DataAction
import slatekit.entities.Entity
import slatekit.entities.EntityRepo

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
        return EntityUtils.isCreated(id)
    }

    fun columnName(name:String):String {
        // Get column name from model schema ( if available )
        val column = name
        //val encoded = QueryEncoder.ensureField(column)
        return column
    }
}


