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

import slate.common.Random
import slate.ext.devices.DeviceService
import slate.ext.users.User

class RegHooks(val _devices:DeviceService, val _reg:RegService) {

  def onBeforeUserCreate(user:User):Unit = {

    RegHelper.setupVerification(user, user.primaryDevice)
    RegHelper.linkDeviceAndUser(user, user.primaryDevice)
  }


  def onAfterUserCreate(user:User):Unit = {

    // Create the device
    RegHelper.linkDeviceAndUser(user, user.primaryDevice)
    _devices.create(user.primaryDevice)
  }


  def onBeforeUserLogin(user:User):Unit = {
    val device = _devices.getDevicePrimary(user.userId)
    RegHelper.setupVerification(user, device.get)
    _devices.update(device.get)
  }


  def onAfterUserLogin(user:User):Unit = {
  }


  def onBeforeUserUpdate(user:User):Unit = {
    val device = _devices.getDevicePrimary(user.userId)
    RegHelper.setupVerification(user, device.get)
    _devices.update(device.get)
  }


  def onAfterUserUpdate(existing:User, updated:User):Unit = {
    // Changed the phone ?
    var phoneChanged = false
    val device = _devices.getDevicePrimary(existing.userId)
    val samePhone = existing.isSamePhone(updated)
    if( device.isDefined && !samePhone )
    {
      // changed phone.. take all the new info.
      RegHelper.setupVerification(existing, device.get)
      phoneChanged = true
    }

    // Phone changed ?
    if (phoneChanged)
    {
      _reg.sendConfirmSms(existing)
    }
  }


  def onBeforeDeviceUpdate(user:User):Unit = {

    // Update device
    val deviceCheck = _devices.getDevicePrimary(user.userId)
    val device = deviceCheck.get
    device.regId = user.primaryPhoneRegId
    device.isDeviceVerified = false
    device.deviceConfirmCode = Random.digits6()

    // Link the items
    user.deviceConfirmCode = device.deviceConfirmCode
    user.isPhoneDeviceVerified = device.isDeviceVerified

    // Update the device and user
    _devices.update(device)
  }


  def onAfterDeviceUpdate(user:User):Unit = {

    // Send device push message for confirmation.
    _reg.sendConfirmDevice(user)
  }


  def onAfterPhoneVerified(user:User):Unit = {
    // Update device
    val device = _devices.getDevicePrimary(user.userId)
    device.get.isPhoneVerified = true
    _devices.update(device.get)
  }


  def onAfterDeviceVerified(user:User):Unit = {
    // Update device
    val device = _devices.getDevicePrimary(user.userId)
    device.get.isDeviceVerified = true
    _devices.update(device.get)
  }


  def onAfterEmailVerified(user:User):Unit = {
  }
}
