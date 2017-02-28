/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2015 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */

import slate.tests.common._
import org.scalatest._
import slate.common.results.ResultCode
import slate.common.{Result}
import slate.ext.devices.{Device, DeviceService}
import slate.ext.reg._
import slate.ext.users.{User, UserConstants, UserService}
import slate.tests.common.MyEncryptor


class RegistrationTests extends FunSuite with BeforeAndAfter with BeforeAndAfterAll{
  val TEST_USER_EMAIL_01 = "johndoe@gmail.com"
  val TEST_USER_EMAIL_02 = "janedoe@gmail.com"
  val TEST_USER_PHONE_01 = "12223334444"
  val TEST_USER_PHONE_02 = "19998887777"


  //describe("User Registration") {
  private var count = 0
  private var _reg:RegService = null
  private var _usr:UserService = null
  private var _dvc:DeviceService = null


  override def beforeAll() {
    println("before all")
  }


  override def afterAll() {
    println("after all")
  }


  def counter():Int = { count = count+1; count;}

  before {
    ServiceFactory.init()
    _usr = ServiceFactory.userService()
    _reg = ServiceFactory.regService()
    _dvc = ServiceFactory.dvcService()
    val user = _usr.getByEmail(TEST_USER_EMAIL_01)
    if(user.isDefined){
      val dvc = _dvc.getDevicePrimary(user.get.userId)
      _dvc.delete(dvc)
      _usr.delete(user)
    }
    println("before: " + counter())
  }

  after {
    println("after: " + counter())
  }


   test("fails validation without user info") {
      val info = new RegInfo()
      RegTestHelper.appendAppData(info)
      val result = _reg.register(info)
      ensureFailure(result)
   }


  test("fails validation without device info") {
    val info = new RegInfo()
    RegTestHelper.appendUserData(info)
    val result = _reg.register(info)
    ensureFailure(result)
  }


  test("can signup") {
    val info = new RegInfo()
    RegTestHelper.appendUserData(info)
    RegTestHelper.appendAppData(info)

    // check ok
    val result = _reg.register(info)
    assert(result.success)

    // check by getting user from database
    val usr1 = _usr.getByEmail(TEST_USER_EMAIL_01)
    assert(usr1.isDefined)
  }


  test("can login") {
    val info = new RegInfo()
    RegTestHelper.appendUserData(info)
    RegTestHelper.appendAppData(info)

    // created user
    val result = _reg.register(info)
    assert(result.success)

    // check by getting user from database
    val userAndDevice = fullyVerfiyUser()
    val usr1 = userAndDevice._1

    // now log in : all verifications codes are different code status reset
    val loginResult = _reg.register(info)
    assert(loginResult.success)

    // now ensure verifiations codes are different and status is unverified
    val usr2 = _usr.getByEmail(TEST_USER_EMAIL_01)
    assert(!usr2.get.isPrimaryPhoneVerified && usr2.get.phoneConfirmCode != usr1.phoneConfirmCode)
    assert(!usr2.get.isPhoneDeviceVerified && usr2.get.deviceConfirmCode != usr1.deviceConfirmCode)
  }


  test("can update device id") {
    val info = new RegInfo()
    RegTestHelper.appendUserData(info)
    RegTestHelper.appendAppData(info)

    // created user
    val result = _reg.register(info)
    assert(result.success)

    // check by getting user from database
    val userAndDevice = fullyVerfiyUser()
    val usr1 = userAndDevice._1
    val dvc1 = userAndDevice._2

    // now log in : all verifications codes are different code status reset
    val updateResult = _reg.updateDeviceId(MyEncryptor.encrypt(usr1.fullUserId().delimited()), "1212121212121212121212")
    assert(updateResult.success)

    // now ensure verification codes are different and status is unverified
    val usr2 = _usr.getByEmail(TEST_USER_EMAIL_01)
    val dvc2 = _dvc.getDevicePrimary(usr1.userId)
    assert(!usr2.get.isPhoneDeviceVerified && usr2.get.deviceConfirmCode != usr1.deviceConfirmCode)
    assert(dvc1.deviceConfirmCode != dvc2.get.deviceConfirmCode)
  }


  test("can update account") {
    val info = new RegInfo()
    RegTestHelper.appendUserData(info)
    RegTestHelper.appendAppData(info)

    // created user
    val result = _reg.register(info)
    assert(result.success)

    // check by getting user from database
    val userAndDevice = fullyVerfiyUser()
    val usr1 = userAndDevice._1
    val dvc1 = userAndDevice._2

    // now log in : all verifications codes are different code status reset
    info.phone = TEST_USER_PHONE_02
    val updateResult = _reg.register(info)
    assert(updateResult.success)

    // now ensure verification codes are different and status is unverified
    val usr2 = _usr.getByEmail(TEST_USER_EMAIL_01)
    val dvc2 = _dvc.getDevicePrimary(usr1.userId)
    assert(!usr2.get.isPhoneDeviceVerified && usr2.get.deviceConfirmCode != usr1.deviceConfirmCode)
    assert(!usr2.get.isPrimaryPhoneVerified && usr2.get.phoneConfirmCode != usr1.phoneConfirmCode)
    assert(dvc1.deviceConfirmCode != dvc2.get.deviceConfirmCode)
  }


  test("can confirm device code") {
    val info = new RegInfo()
    RegTestHelper.appendUserData(info)
    RegTestHelper.appendAppData(info)
    val result = _reg.register(info)

    // check not verified
    val usr1 = _usr.getByEmail(TEST_USER_EMAIL_01)
    assert(!usr1.get.isPhoneDeviceVerified)

    val usrId = usr1.get.fullUserId()
    val res = _reg.confirmDevice(MyEncryptor.encrypt(usrId.delimited), usr1.get.deviceConfirmCode)
    val usr2 = _usr.getByEmail(TEST_USER_EMAIL_01)
    assert(usr2.get.isPhoneDeviceVerified)
    assert(result.success)
  }


  test("can confirm phone code") {
    val info = new RegInfo()
    RegTestHelper.appendUserData(info)
    RegTestHelper.appendAppData(info)
    val result = _reg.register(info)

    // check not verified
    val usr1 = _usr.getByEmail(TEST_USER_EMAIL_01)
    assert(!usr1.get.isPrimaryPhoneVerified)

    val usrId = usr1.get.fullUserId()
    val res = _reg.confirmPhone(MyEncryptor.encrypt(usrId.delimited), usr1.get.phoneConfirmCode)
    val usr2 = _usr.getByEmail(TEST_USER_EMAIL_01)
    assert(usr2.get.isPrimaryPhoneVerified)
    assert(result.success)
  }


  test("can confirm email code") {
    val info = new RegInfo()
    RegTestHelper.appendUserData(info)
    RegTestHelper.appendAppData(info)
    val result = _reg.register(info)

    // check not verified
    val usr1 = _usr.getByEmail(TEST_USER_EMAIL_01)
    assert(!usr1.get.isEmailVerified)

    val usrId = usr1.get.fullUserId()
    val res = _reg.confirmEmail(MyEncryptor.encrypt(usrId.delimited), usr1.get.emailConfirmCode)
    val usr2 = _usr.getByEmail(TEST_USER_EMAIL_01)
    assert(usr2.get.isEmailVerified)
    assert(result.success)
  }


  def ensureFailure(result:Result[Any]):Unit = {
    assert( !result.success )
    assert( result.code == ResultCode.FAILURE)
  }


  def fullyVerfiyUser():(User,Device) = {

    val usr1 = _usr.getByEmail(TEST_USER_EMAIL_01)
    val device = _dvc.getDevicePrimary(usr1.get.userId)

    // simulate all code confirmatations
    usr1.get.isPhoneDeviceVerified = true
    usr1.get.isEmailVerified = true
    usr1.get.isPrimaryPhoneVerified = true
    usr1.get.status = UserConstants.StatusActive.toString
    _usr.update(usr1.get)
    device.get.isDeviceVerified = true
    device.get.isPhoneVerified = true
    _dvc.update(device.get)

    (usr1.get, device.get)
  }
}
