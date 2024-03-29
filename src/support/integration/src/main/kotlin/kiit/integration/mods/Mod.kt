/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.integration.mods

import kiit.common.DateTime
import kiit.entities.EntityWithUUID
import kiit.entities.EntityWithId
import kiit.entities.Id
import kiit.entities.Column

data class Mod(
    @property:Id()
    override val id: Long = 0L,

    @property:Column(length = 50)
    val name: String = "",

    @property:Column(length = 200)
    val desc: String = "",

    @property:Column(length = 30)
    val version: String = "",

    @property:Column()
    val isInstalled: Boolean = false,

    @property:Column()
    val isEnabled: Boolean = false,

    @property:Column()
    val isDbDependent: Boolean = false,

    @property:Column()
    val totalModels: Int = 0,

    @property:Column(length = 50)
    val source: String = "",

    @property:Column(length = 100)
    val dependencies: String = "",

    @property:Column()
    val createdAt: DateTime = DateTime.now(),

    @property:Column()
    val createdBy: Int = 0,

    @property:Column()
    val updatedAt: DateTime = DateTime.now(),

    @property:Column()
    val updatedBy: Int = 0,

    @property:Column(length = 50)
    override val uuid: String = ""
)
    : EntityWithId<Long>, EntityWithUUID {

    override fun isPersisted(): Boolean = id > 0

}
