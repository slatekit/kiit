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

package slatekit.examples.common

import slatekit.common.Field
import slatekit.entities.core.EntityWithId


data class User(
            override val id:Long = 0L,


            @property:Field(required = true, length = 30)
            val email:String = "",


            @property:Field(required = true, length = 30)
            val firstName:String = "",


            @property:Field(required = true, length =30)
            val lastName:String = "",


            @property:Field(required = true)
            val isMale:Boolean = false,


            @property:Field(required = true)
            val age:Int = 35

) : EntityWithId<Long> {

    override fun isPersisted(): Boolean = id > 0

  fun fullname():String = firstName + " " + lastName



  override fun toString():String  {
    return "$email, $firstName, $lastName, $isMale, $age"
  }
}
