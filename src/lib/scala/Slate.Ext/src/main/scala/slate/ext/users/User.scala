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

package slate.ext.users

import slate.common.{Field, DateTime}
import slate.entities.core.{IEntity}
import slate.core.common.tenants.{Tenant, ITenant}
import slate.ext.devices.Device


class User extends IEntity with ITenant
{
    var id = 0L

    @Field("", true, -1)
    var tenantId = 0

    @Field("", true, 10)
    var version = ""

    @Field("", true, 50)
    var userId  = ""

    @Field("", true, 50)
    var userName  = ""

    @Field("", true, 50)
    var token  = ""

    @Field("", true, 50)
    var email  = ""

    @Field("", true, 20)
    var password  = ""

    @Field("", true, 20)
    var promoCode  = ""

    @Field("", true, 20)
    var firstName  = ""

    @Field("", true, 20)
    var lastName  = ""

    @Field("", true, 30)
    var roles  = ""

    @Field("", true, 30)
    var status  = ""

    @Field("", true, 50)
    var lastLogin  = DateTime.now()

    @Field("", true, 50)
    var lastActive  =  DateTime.now()

    @Field("", true, 20)
    var primaryPhone  = ""

    @Field("", true, 200)
    var primaryPhoneRegId  = ""

    @Field("", true, 10)
    var primaryPhonePlatform  = ""

    @Field("", true, 10)
    var isPrimaryPhoneVerified  = false

    @Field("", true, 10)
    var isPhoneDeviceVerified  = false

    @Field("", true, 10)
    var isEmailVerified  = false

    @Field("", true, 10)
    var primaryPhoneCountryCode  = ""

    @Field("",true, -1)
    var phoneConfirmCode = 0

    @Field("",true, -1)
    var deviceConfirmCode = 0

    @Field("", true, -1)
    var totalDevices  = 0

    @Field("", true, 50)
    var country  = ""

    @Field("", true, 50)
    var region  = ""

    @Field("", false, 50)
    var state  = ""

    @Field("", false, 50)
    var city  = ""

    @Field("", false, 10)
    var zip  = ""

    @Field("", true, 30)
    var refTag  = ""

    @Field("", false, 50)
    var imageId  = ""

    @Field("", false, 50)
    var imageThumbnailId  = ""

    @Field("", false, 50)
    var familyId  = ""

    @Field("", false, 1000)
    var blockedUsers  = ""

    @Field("", true, -1)
    var emailConfirmCode  = 0

    @Field("", false, 5000)
    var devices  = ""

    @Field("", true, -1)
    var recordState  = 0


    @Field("", true, -1)
    var createdAt  = DateTime.now()


    @Field("", true, -1)
    var createdBy  = 0


    @Field("", true, -1)
    var updatedAt  =  DateTime.now()


    @Field("", true, -1)
    var updatedBy  = 0


    var primaryDevice:Device = Device.empty


    def isSameAccount(user:User):Boolean = {
        user != null && user.email == this.email
    }

    def isSamePhone(user:User):Boolean = {
        user != null && user.primaryPhone == this.primaryPhone
    }


    def fullUserId():UserId = {
        new UserId(1, id, userId, country, region)
    }
}
