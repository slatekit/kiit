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

package slatekit.integration.mods

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.entities.core.EntityWithUUID
import slatekit.entities.core.EntityWithId


data class Mod(
        @property:Field()
        override val id: Long = 0L,


        @property:Field(length = 50)
        val name: String = "",


        @property:Field(length = 200)
        val desc: String = "",


        @property:Field(length = 30)
        val version: String = "",


        @property:Field()
        val isInstalled: Boolean = false,


        @property:Field()
        val isEnabled: Boolean = false,


        @property:Field()
        val isDbDependent: Boolean = false,


        @property:Field()
        val totalModels: Int = 0,


        @property:Field(length = 50)
        val source: String = "",


        @property:Field(length = 100)
        val dependencies: String = "",


        @property:Field()
        val createdAt: DateTime = DateTime.now(),


        @property:Field()
        val createdBy: Int = 0,


        @property:Field()
        val updatedAt: DateTime = DateTime.now(),


        @property:Field()
        val updatedBy: Int = 0,


        @property:Field(length = 50)
        override val uuid: String = ""
)
    : EntityWithId, EntityWithUUID {
}
