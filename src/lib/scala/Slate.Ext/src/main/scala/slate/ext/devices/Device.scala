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

package slate.ext.devices

import slate.common.{Field, DateTime}
import slate.entities.core.{IEntityUnique, IEntity}
import slate.core.common.tenants.ITenant


class Device extends IEntity with IEntityUnique with ITenant
{
    var id = 0L

    @Field("",true, 50)
    var uniqueId: String = ""


    @Field("", true, -1)
    var tenantId = 0


    @Field("",true, -1)
    var userId = 0L


    @Field("",true, 50)
    var userKey = ""


    @Field("",true, -1)
    var isPrimary = false


    @Field("",true, 10)
    var platform = ""


    @Field("",true, 30)
    var model = ""


    @Field("",true, 10)
    var oS = ""


    @Field("",true, 10)
    var dType = ""


    @Field("",true, 10)
    var country = ""


    @Field("",true, 14)
    var phone = ""


    @Field("",true, 5000)
    var regId = ""


    @Field("",true, -1)
    var phoneConfirmCode = 0


    @Field("",true, -1)
    var deviceConfirmCode = 0


    @Field("",true, -1)
    var isPhoneVerified = false


    @Field("",true, -1)
    var isDeviceVerified = false


    @Field("",true, 30)
    var appName = ""


    @Field("",true, 15)
    var appVersion = ""


    @Field("", true, -1)
    var createdAt  = DateTime.now()


    @Field("", true, -1)
    var createdBy  = 0


    @Field("", true, -1)
    var updatedAt  =  DateTime.now()


    @Field("", true, -1)
    var updatedBy  = 0
}


object Device {
    val empty = new Device()
}
