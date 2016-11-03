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
package slate.ext.reg

import slate.common._
import slate.common.encrypt.Encryptor
import slate.common.results.{ResultCode, ResultSupportIn}

import slate.core.email.EmailService
import slate.core.mobile.MessageService
import slate.core.sms.SmsService
import slate.ext.devices.{Device, DeviceService}
import slate.ext.users._

class RegService(
                  private val _encryptor:Encryptor,
                  private val _users: UserService,
                  private val _devices:DeviceService,
                  private val _smsSvc:Option[SmsService],
                  private val _emailSvc:Option[EmailService],
                  private val _msgSvc:Option[MessageService]
                 )

  extends ResultSupportIn {

  protected var _hooks:Option[RegHooks] = None
  val TEMP_FIELD_VALUE = "deferred"


  def setHooks(hooks:RegHooks):Unit = {
    _hooks = Some(hooks)
  }


  /**
    * activates the user account
 *
    * @param userId : id of the user
    * @return
    */
  def activate(userId:String): Result[Boolean] =
  {
    val user = ensureUser(userId)
    user.status = UserConstants.StatusActive.toString
    saveUser(user)
    ok(Some("User has been activated"))
  }


  /**
    * deactivates the user
 *
    * @param userId : id of the user
    * @return
    */
  def deactivate(userId:String): Result[Boolean] =
  {
    val user = ensureUser(userId)
    user.status = UserConstants.StatusDeactivated.toString
    saveUser(user)
    ok(Some("User has been deactivated"))
  }


  /**
    * confirms the device by checking the code supplied with the one on record.
 *
    * @param userId   : id of user
    * @param code : code supplied to device
    * @return
    */
  def confirmDevice(userId:String, code:Int): Result[Boolean] =
  {
    // Ensure match
    val user = ensureUser(userId)
    if(user.deviceConfirmCode != code) {
      return no(Some("Incorrect confirmation code"))
    }

    // Verified!
    user.isPhoneDeviceVerified = true
    saveUser(user)

    if(_hooks.isDefined){
      _hooks.get.onAfterDeviceVerified(user)
    }

    ok(Some("Device verified"))
  }


  /**
    * confirms the phone by checking the code supplied with the one on record.
 *
    * @param userId   : id of user
    * @param code : code supplied to phone
    * @return
    */
  def confirmPhone(userId:String, code:Int): Result[Boolean] =
  {
    // Ensure phone
    val user = ensureUser(userId)
    if(user.phoneConfirmCode != code) {
      return no(Some("Incorrect confirmation code"))
    }

    // Verified!
    user.isPrimaryPhoneVerified = true
    saveUser(user)

    if(_hooks.isDefined){
      _hooks.get.onAfterPhoneVerified(user)
    }

    ok(Some("Phone verified"))
  }


  /**
    * confirms the email by checking the code supplied with the one on record.
 *
    * @param userId   : id of user
    * @param code : code supplied to email
    * @return
    */
  def confirmEmail(userId:String, code:Int): Result[Boolean] =
  {
    // Ensure phone
    val user = ensureUser(userId)
    if(user.emailConfirmCode != code) {
      return no(Some("Incorrect confirmation code"))
    }

    // Verified!
    user.isEmailVerified = true
    saveUser(user)

    if(_hooks.isDefined){
      _hooks.get.onAfterEmailVerified(user)
    }

    ok(Some("Email verified"))
  }


  def register(info:RegInfo): Result[Boolean] =
  {
    // check inputs
    val infoCheck = RegHelper.validate(info)
    if (!infoCheck.success ) {
      return infoCheck
    }

    // Convert to user / device
    var user = RegHelper.convertToUser(info)

    // Get existing
    val existing = _users.getByEmail(user.email)

    // already existing user ?
    val isExisting=  if(existing.isDefined) true else false
    val sameAccount = if(existing.isDefined) user.isSameAccount(existing.get) else false
    val samePhone = if(existing.isDefined) user.isSamePhone(existing.get) else false
    var action = ""
    var opResult:Result[Boolean] = null

    // CASE 1: First time user
    if (!isExisting)
    {
      action = "register"
      user.primaryDevice = RegHelper.convertToDevice(info)
      opResult = registerUser(user)
    }
    // CASE 2: Login ( same email, phone )
    else if (isExisting && sameAccount && samePhone)
    {
      action = "login"
      opResult = registerLogin(existing.get, true)
      user = existing.get
    }
    // CASE 3: Edit ( same email, different phone )
    else if (isExisting && sameAccount && !samePhone)
    {
      action = "edit account"
      opResult = registerEdit(existing.get, user)
    }
    // existing user but different info
    else if (isExisting)
    {
      action = "already reg"
      opResult = failure[Boolean](msg = Some("Already registered"))
    }
    else {
      opResult = failure[Boolean](msg = Some("Unknown registration action"))
    }
    opResult
  }


  def registerUser(user:User): Result[Boolean] = {

    // Verification codes
    RegHelper.setupVerification(user)

    // Email
    user.isEmailVerified = false
    user.emailConfirmCode = Random.digits6()
    user.userId = Random.stringGuid(false)

    // Ensure location fields.
    RegHelper.ensureDefaults(user)

    // hook before user is created.
    if(_hooks.isDefined) {
      _hooks.get.onBeforeUserCreate(user)
    }

    // Now create user
    _users.create(user)

    // hook after user is created
    if(_hooks.isDefined) {
      _hooks.get.onAfterUserCreate(user)
    }

    // Send confirm codes.
    sendConfirmSms(user)
    sendConfirmEmail(user)

    confirmResult(user)
  }


  def registerLogin(user:User, sendPhoneConfirmCode:Boolean): Result[Boolean] = {

    // Reset verification process
    RegHelper.setupVerification(user)

    // Reset timestamps
    RegHelper.ensureDefaults(user)

    // hook before update
    if(_hooks.isDefined) {
      _hooks.get.onBeforeUserLogin(user)
    }

    // update
    _users.update(user)

    // hook after update
    if(_hooks.isDefined) {
      _hooks.get.onAfterUserLogin(user)
    }

    // Send code via sms to phone ( 1st step in verification process )
    sendConfirmSms(user)

    confirmResult(user)
  }


  def registerEdit(existing:User, updated:User): Result[Boolean] = {

    // Reset timestamps
    existing.userName = Strings.valueOrDefault(updated.userName, existing.userName)
    existing.firstName = Strings.valueOrDefault(updated.firstName, existing.firstName)
    existing.lastName = Strings.valueOrDefault(updated.lastName, existing.lastName)
    existing.lastLogin = DateTime.now()
    existing.updatedAt = DateTime.now()
    existing.state = Strings.valueOrDefault(updated.state, existing.state)
    existing.city = Strings.valueOrDefault(updated.city, existing.city)
    existing.zip = Strings.valueOrDefault(updated.zip, existing.zip)

    // Changed the phone ?
    var phoneChanged = false
    val samePhone = existing.isSamePhone(updated)
    if( !samePhone )
    {
      // changed phone.. take all the new info.
      RegHelper.setupVerification(existing)
      phoneChanged = true
    }

    // hook before update
    if(_hooks.isDefined) {
      _hooks.get.onBeforeUserUpdate(existing)
    }

    // Now update user
    _users.update(existing)

    // Phone changed ?
    if (phoneChanged)
    {
      sendConfirmSms(existing)
    }

    confirmResult(existing)
  }


  def resetPass(id:String): Result[Boolean] =
  {
    notImplemented()
  }


  def sendConfirmCode(user:User, confirmType:String):Result[Boolean] = {
    var userId = ""
    var success = true
    var msg = ""
    try
    {
      userId = user.email
      if(Strings.isMatch(confirmType, RegConstants.CODE_TYPE_PHONE))
      {
        sendConfirmSms(user)
      }
      else if (Strings.isMatch(confirmType, RegConstants.CODE_TYPE_DEVICE))
      {
        sendConfirmDevice(user)
      }
      else if (Strings.isMatch(confirmType, RegConstants.CODE_TYPE_EMAIL))
      {
        sendConfirmEmail(user)
      }
    }
    catch
    {
      case ex:Exception => {
        //error("reg", "Error sending confirm code for type: " + type + ", to : " + userId, ex)
        success = false
        msg = "Error sending confirmation code/message"
      }
    }
    okOrFailure(success, Some(msg))
  }


  def sendConfirmDeviceById(id:String):Result[Boolean] = {

    val user = ensureUser(id)
    sendConfirmDevice(user)
  }


  def sendConfirmDevice(user:User): Result[Boolean] =
  {
    if(!_msgSvc.isDefined) {
      return no(Some("Sms service not configured"))
    }
    val result = _msgSvc.get.confirmDevice(user.primaryPhoneCountryCode, user.primaryPhonePlatform,
      user.primaryPhoneRegId, user.deviceConfirmCode.toString)

    result
  }


  def sendConfirmSmsById(id:String):Result[Boolean] = {

    val user = ensureUser(id)
    sendConfirmSms(user)
  }


  def sendConfirmSms(user:User): Result[Boolean] =
  {
    if(!_smsSvc.isDefined) {
      return no(Some("Sms service not configured"))
    }
    val result = _smsSvc.get.send("Blend app phone verification code: " + user.phoneConfirmCode,
      user.country, user.primaryPhone)

    result
  }


  def sendConfirmEmailById(id:String):Result[Boolean] = {

    val user = ensureUser(id)
    sendConfirmEmail(user)
  }


  def sendConfirmEmail(user:User): Result[Boolean] =
  {
    if(!_emailSvc.isDefined) {
      return failure[Boolean](msg = Some("Sms service not configured"))
    }
    val result = _emailSvc.get.send(user.email, "Welcome to Blend", "Welcome to Blend.Life."
        + " Confirmation code = " + user.emailConfirmCode, false)

    result
  }


  def signIn(id:String, password:String): Result[String] =
  {
    val user = new User()

    // Determine what to update based on registration rules
    user.lastLogin = DateTime.now()
    saveUser(user)

    success(id)
  }


  def signOut(id:String): Result[String] =
  {
    val user = new User()

    // Determine what to update based on registration rules
    user.lastActive = DateTime.now()
    saveUser(user)

    success(id)
  }


  def createSampleUser(first:String, last:String, email:String, phone:String,
      country:String, region:String): Result[Long] =
  {
    val user = new User()
    user.version                 = "v1"
    user.userId                  = Random.stringGuid(false)
    user.token                   = Random.stringGuid(false)
    user.userName                = email
    user.email                   = email
    user.password                = "temp"
    user.promoCode               = "201604FRE"
    user.firstName               = first
    user.lastName                = last
    user.roles                   = "user"
    user.lastLogin               = DateTime.now()
    user.lastActive              = DateTime.now()
    user.primaryPhone            = phone
    user.primaryPhoneRegId       = ""
    user.primaryPhonePlatform    = "and"
    user.isPrimaryPhoneVerified    = false
    user.isPhoneDeviceVerified     = false
    user.primaryPhoneCountryCode = country
    user.totalDevices            = 0
    user.country                 = country
    user.region                  = region
    user.state                   = ""
    user.city                    = ""
    user.zip                     = ""
    user.refTag                  = ""
    user.imageId                 = ""
    user.imageThumbnailId        = ""
    user.familyId                = ""
    user.blockedUsers            = ""
    user.emailConfirmCode        = Random.digits6()
    user.phoneConfirmCode        = Random.digits6()
    user.deviceConfirmCode       = Random.digits6()
    user.isEmailVerified         = false
    user.devices                 = ""
    user.recordState             = 0
    user.createdAt                 = DateTime.now()
    user.updatedAt                 = DateTime.now()
    success("test")
    val id = _users.create(user)
    success(id)
  }


  def updateDeviceId(userId:String, deviceId:String):Result[Boolean] = {

    // confirm code supplied ?
    if (Strings.isNullOrEmpty(deviceId))
      return failure(msg = Some("Device id code not supplied"))

    // validate user
    val userCheck = getUser(userId, true)
    if (!userCheck.isDefined)
      return failure(msg = Some("Unknown user"))

    // Update user model
    val user = userCheck.get
    user.primaryPhoneRegId = deviceId

    // hook before
    if(_hooks.isDefined) {
      _hooks.get.onBeforeDeviceUpdate(user)
    }

    // update
    _users.update(user)

    // hook after
    if(_hooks.isDefined) {
      _hooks.get.onAfterDeviceUpdate(user)
    }

    ok(Some("Device id updated"))
  }


  protected  def ensureUser(id:String):User =
  {
    // Bad id ?
    val userResult = getUser(id, true)
    Ensure.isTrue(userResult.isDefined, "User id invalid")

    userResult.get
  }


  protected def getUser(id:String, decrypt:Boolean = false):Option[User] =
  {
    // id = "number:guid:country:region"
    val actualId = if(decrypt) _encryptor.decrypt(id) else id
    val userId = UserHelper.parseUserId(actualId)
    _users.getByUserId(userId.guid)
  }


  protected def saveUser(user:User):Unit =
  {
    _users.save(Some(user))
  }


  protected def getUserType(userType:String):String = {
    if (Strings.isNullOrEmpty(userType))
      return RegConstants.USER_TYPE_PROMO
    val result = Strings.valueOrDefault(userType, RegConstants.USER_TYPE_PROMO)
    result
  }


  protected def confirmResult(user:User): Result[Boolean] = {

    val msg = "Confirmation code needed to finish registration"
    val userId = user.fullUserId()
    val userIdEnc = _encryptor.encrypt(userId.delimited())
    val extCode = "USER_TYPE:" + getUserType(user.promoCode)
    val result = new SuccessResult[Boolean](true, ResultCode.CONFIRM, msg = Some(msg), ext = Some(userIdEnc))
    result
  }
}
