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

package slate.ext.invites

import slate.common.{Field, DateTime}
import slate.entities.core.{IEntityUnique, IEntity}
import slate.core.common.tenants.ITenant

/**
  * Created by kreddy on 2/24/2016.
  */
class Invite extends IEntity with IEntityUnique with ITenant
{
    var id = 0L

    @Field("",true, 50)
    var uniqueId: String = ""


    @Field("", true, -1)
    var tenantId = 0


    @Field("", true, 50)
    var userEmail = ""


    @Field("", true, 20)
    var userPassword = ""


    @Field("", true, 50)
    var userName = ""


    @Field("", true, 20)
    var promocode = ""


    @Field("", true, 20)
    var firstName = ""


    @Field("", true, 20)
    var lastName = ""


    @Field("", true, 20)
    var primaryPhone = ""


    @Field("", true, 10)
    var primaryPhonePlatform = ""


    @Field("", true, 20)
    var country = ""


    @Field("", true, 50)
    var status = ""


    @Field("", true, 30)
    var refTag = ""


    @Field("", true, -1)
    var recordState = 0


    @Field("", true, -1)
    var createdAt  = DateTime.now()


    @Field("", true, -1)
    var createdBy  = 0


    @Field("", true, -1)
    var updatedAt  =  DateTime.now()


    @Field("", true, -1)
    var updatedBy  = 0
}
