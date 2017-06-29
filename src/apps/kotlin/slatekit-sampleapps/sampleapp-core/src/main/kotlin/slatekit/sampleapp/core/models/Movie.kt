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

package slatekit.sampleapp.core.models

import slatekit.common.DateTime
import slatekit.common.Field
import slatekit.entities.core.EntityWithId


data class Movie(
                    override val id :Long = 0L,


                    @property:Field(required = true, length = 50)
                    val title :String = "",


                    @property:Field(required = true, length = 20)
                    val desc :String = "",


                    @property:Field(required = true, length = 20)
                    val category :String = "",


                    @property:Field(required = true, length = 30)
                    val status :String = "",


                    @property:Field(required = true, length = 50)
                    val country :String = "",


                    // These are the timestamp and audit fields.
                    @property:Field(required = true)
                    val createdAt :DateTime = DateTime.now(),


                    @property:Field(required = true)
                    val createdBy :Long  = 0,


                    @property:Field(required = true)
                    val updatedAt :DateTime =  DateTime.now(),


                    @property:Field(required = true)
                    val updatedBy :Long  = 0
)
: EntityWithId
{
}