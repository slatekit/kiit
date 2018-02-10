/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
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

) : EntityWithId {


  fun fullname():String = firstName + " " + lastName



  override fun toString():String  {
    return "$email, $firstName, $lastName, $isMale, $age"
  }
}
