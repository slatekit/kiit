package slatekit.entities.slatekit.entities

data class EntityOptions(
    val applyId: Boolean,
    val applyMetadata: Boolean,
    val applyHooks: Boolean
) {

    companion object {

        val empty = EntityOptions(true,false, false)
    }
}
