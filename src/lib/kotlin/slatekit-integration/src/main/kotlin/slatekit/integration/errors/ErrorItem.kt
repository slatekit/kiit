package slatekit.integration.errors

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.common.Random

data class ErrorItem(

        @property:Field(length = 50)
        override val id: Long = 0L,


        @property:Field(length = 50)
        override val uuid: String = Random.guid(),


        @property:Field(length = 12)
        override val shard: String = "",


        /**
         * Field for identifying source of error
         */
        @property:Field(length = 50)
        val source: String = "",


        /**
         * Field for identifying specific action that caused error
         * e.g. could be the API path "app/reg/newUser"
         */
        @property:Field(length = 50)
        val action: String = "",


        /**
         * Status to indicate if the processing status of the item
         */
        @property:Field()
        val status: ErrorItemStatus = ErrorItemStatus.Active,


        /**
         * The number of retry attempts made to reprocess this request
         */
        @property:Field()
        val retries: Int = 0,


        /**
         * The error message/details
         */
        @property:Field(length = -1)
        val error: String = "",


        /**
         * The content of the request that caused the error. This should be used during reprocessing
         */
        @property:Field(length = -1)
        val request: String = "",


        /**
         * Tags used as attributes for this item as a message ( which has attributes )
         */
        @property:Field(length = -1)
        val tags: String = "",


        @property:Field()
        val lastActive: DateTime = DateTime.now(),


        @property:Field(length = 20)
        override val tag: String = "",


        @property:Field()
        override val createdAt: DateTime = DateTime.now(),


        @property:Field(length = 50)
        override val createdBy: String = "",


        @property:Field()
        override val updatedAt: DateTime = DateTime.now(),


        @property:Field(length = 50)
        override val updatedBy: String = ""
)

    : slatekit.entities.core.EntityWithId, slatekit.entities.core.EntityWithMeta,
        slatekit.entities.core.EntityWithShard, slatekit.entities.core.EntityUpdatable<ErrorItem> {
    /**
     * sets the id on the entity and returns the entity with updated id.
     * @param id
     * @return
     */
    override fun withId(id: Long): ErrorItem = this.copy(id = id)

}