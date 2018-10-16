package slatekit.entities.core

interface ServiceSupport<T> where T : Entity {

    fun repo(): IEntityRepo
    fun entityRepo(): EntityRepo<T>
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
