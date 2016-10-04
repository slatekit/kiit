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

package slate.ext.reg

import slate.common._
import slate.common.results.{ResultSupportIn}
import slate.ext.devices.Device
import slate.ext.users.User

object RegHelper extends ResultSupportIn {

  def ensureDefaults(user:User):Unit = {
    user.lastLogin = DateTime.now()
    user.updatedAt = DateTime.now()
    user.state = Strings.valueOrDefault(user.state, Strings.empty)
    user.city = Strings.valueOrDefault(user.city, Strings.empty)
    user.zip = Strings.valueOrDefault(user.zip, Strings.empty)
  }


  def validate(reg:RegInfo):Result[Boolean] = {
    if (Strings.isNullOrEmpty(reg.userName))        return no(Some("username not supplied"))
    if (Strings.isNullOrEmpty(reg.email))           return no(Some("email not supplied"))
    if (Strings.isNullOrEmpty(reg.countryCode))     return no(Some("countryCode not supplied"))
    if (Strings.isNullOrEmpty(reg.country))         return no(Some("country not supplied"))
    if (Strings.isNullOrEmpty(reg.phone))           return no(Some("phone not supplied"))
    if (Strings.isNullOrEmpty(reg.regId))           return no(Some("regId not supplied"))
    if (Strings.isNullOrEmpty(reg.appName))         return no(Some("appName not supplied"))
    if (Strings.isNullOrEmpty(reg.appVersion))      return no(Some("appVersion not supplied"))
    if (Strings.isNullOrEmpty(reg.devicePlatform))  return no(Some("devicePlatform not supplied"))
    if (Strings.isNullOrEmpty(reg.deviceType))      return no(Some("deviceType not supplied"))
    if (Strings.isNullOrEmpty(reg.deviceModel))     return no(Some("deviceModel not supplied"))
    if (Strings.isNullOrEmpty(reg.deviceOS))        return no(Some("deviceOS not supplied"))
    ok()
  }


  def convertToDevice(reg:RegInfo):Device = {
    // Build up the device.
    val device = new Device()
    device.isPrimary  = true
    device.country    = reg.countryCode
    device.platform   = reg.devicePlatform
    device.model      = reg.deviceModel
    device.oS         = reg.deviceOS
    device.dType      = reg.deviceType
    device.phone      = reg.phone
    device.regId      = reg.regId
    device.appName    = reg.appName
    device.appVersion = reg.appVersion
    device.isPhoneVerified = false
    device.isDeviceVerified = false
    device
  }


  def convertToUser(reg:RegInfo):User = {
    val user = new User()

    // build up the user
    user.version         = reg.version
    user.userName        = reg.userName
    user.email           = reg.email
    user.password        = reg.password
    user.roles           = "user"
    user.firstName       = reg.firstName
    user.lastName        = reg.lastName
    user.country         = reg.country
    user.state           = reg.state
    user.city            = reg.city
    user.zip             = reg.zip
    user.refTag          = reg.refTag
    user.region          = "001"
    user.isEmailVerified = false

    // copy over the duplicate fields from the device to the user.
    // this allows not having to lookup the primary device each time.
    user.primaryPhone = reg.phone
    user.primaryPhoneRegId = reg.regId
    user.isPhoneDeviceVerified = false
    user.isPrimaryPhoneVerified = false
    user.primaryPhonePlatform = reg.devicePlatform
    user.primaryPhoneCountryCode = reg.country
    user
  }


  def convertToUserDevice(reg:RegInfo):User = {
    val user = convertToUser(reg)

    // Build up the device.
    user.primaryDevice = convertToDevice(reg)

    user
  }


  def setupVerification(user:User)
  {
    user.isPrimaryPhoneVerified = false
    user.phoneConfirmCode = RandomGen.digits6()
  }


  def setupVerification(user:User, device:Device)
  {
    device.phoneConfirmCode = user.phoneConfirmCode
    device.isPhoneVerified = false
    device.deviceConfirmCode = RandomGen.digits6()
    device.isDeviceVerified = false

    user.isPhoneDeviceVerified = false
    user.deviceConfirmCode = device.deviceConfirmCode
  }


  def linkDeviceAndUser(model:User, device:Device)
  {
    // Ensure device references back the model
    device.userId = model.id
    device.userKey = model.userId

    // Setup primary device
    model.primaryPhonePlatform = device.platform
    model.primaryPhoneCountryCode = device.country
    model.primaryPhone = device.phone
    model.primaryPhoneRegId = device.regId
    model.phoneConfirmCode = device.phoneConfirmCode
    model.deviceConfirmCode = device.deviceConfirmCode
    model.isPrimaryPhoneVerified = device.isPhoneVerified
    model.isPhoneDeviceVerified = device.isDeviceVerified
  }
}
