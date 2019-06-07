package slatekit.entities.slatekit.entities

data class EntityOptions(val applyMetadata: Boolean,
                         val applyHooks:Boolean) {

    companion object {

        val empty = EntityOptions(false, false)
    }
}