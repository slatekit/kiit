/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.integration.tenants

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.entities.core.EntityWithId


data class Tenant(
        @property:Field(length = 50)
        override val id: Long = 0L,


        @property:Field(length = 30)
        val name: String = "",


        @property:Field(length = 30)
        val desc: String = "",


        @property:Field(length = 30)
        val key: String = "",


        @property:Field(length = 30)
        val folder: String = "",


        @property:Field()
        val isEnabled: Boolean = false,


        @property:Field(length = 50)
        val account: String = "",


        @property:Field(length = 50)
        val contact: String = "",


        @property:Field(length = 50)
        val url: String = "",


        @property:Field(length = 50)
        val uniqueId: String = "",


        @property:Field()
        val createdAt: DateTime = DateTime.now(),


        @property:Field()
        val createdBy: Int = 0,


        @property:Field()
        val updatedAt: DateTime = DateTime.now(),


        @property:Field()
        val updatedBy: Int = 0
) : EntityWithId {
}
