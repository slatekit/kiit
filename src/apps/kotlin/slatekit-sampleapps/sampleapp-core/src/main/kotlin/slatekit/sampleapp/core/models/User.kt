/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2016 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */

package slatekit.sampleapp.core.models

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.entities.core.EntityUpdatable
import slatekit.entities.core.EntityWithId

/**
  * Created by kreddy on 3/1/2016.
  */
data class User (
                  override val id :Long = 0L,

                  @property:Field(required = true, length = 50)
                  val userName :String  = "",


                  @property:Field(required = true, length = 50)
                  val token  :String  = "",


                  @property:Field(required = true, length = 50)
                  val email  :String  = "",


                  @property:Field(required = true, length = 20)
                  val password  :String  = "",


                  @property:Field(required = true, length = 20)
                  val firstName  :String  = "",


                  @property:Field(required = true, length = 20)
                  val lastName  :String  = "",


                  @property:Field(required = true, length = 30)
                  val roles  :String  = "",


                  @property:Field(required = true, length = 30)
                  val status :String  = "",


                  @property:Field(required = true, length = 50)
                  val lastLogin :DateTime = DateTime.now(),


                  @property:Field(required = true, length = 50)
                  val lastActive :DateTime =  DateTime.now(),


                  @property:Field(required = true, length = 20)
                  val phone  :String  = "",


                  @property:Field(required = true, length = 50)
                  val country  :String  = "",


                  @property:Field(required = true, length = 10)
                  val isEmailVerified  :Boolean  = false,


                  // These are the timestamp and audit fields.
                  @property:Field(required = true)
                  val createdAt :DateTime = DateTime.now(),


                  @property:Field(required = true)
                  val createdBy :Long  = 0,


                  @property:Field(required = true)
                  val updatedAt :DateTime =  DateTime.now(),


                  @property:Field(required = true)
                  val updatedBy :Long  = 0
) : EntityWithId, EntityUpdatable<User>
{
  /**
    * sets the id on the entity and returns the entity with updated id.
    * @param id
    * @return
    */
  override fun withId(id:Long): User = copy(id = id)


  fun isSameAccount(user:User):Boolean {
    return user.email == this.email
  }


  fun isSamePhone(user:User):Boolean {
    return user.phone == this.phone
  }
}
