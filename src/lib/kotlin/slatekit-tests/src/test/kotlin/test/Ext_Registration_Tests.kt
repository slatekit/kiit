package test

import org.junit.Test
import slatekit.common.Result
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.db.DbLookup
import slatekit.common.envs.Dev
import slatekit.common.envs.Env
import slatekit.common.info.About
import slatekit.common.log.LoggerConsole
import slatekit.entities.core.Entities
import slatekit.ext.devices.Device
import slatekit.ext.devices.DeviceService
import slatekit.ext.registration.RegInfo
import slatekit.ext.registration.RegService
import slatekit.ext.users.User
import slatekit.ext.users.UserConstants
import slatekit.ext.users.Token
import slatekit.ext.users.UserService
import slatekit.integration.common.AppEntContext
import test.common.MyEncryptor


class Ext_Registration_Tests {

    var ent:Entities? = null
    var ctx:AppEntContext? = null

    fun regService(): RegService {

        // 1. load config from env.local
        val config = Config()

        // 2. load connection
        val con = config.dbCon()
        val dbs = DbLookup.defaultDb(con)

        // 3. entities
        val ents = Entities(dbs)
        ctx = AppEntContext (
                arg  = Args.default(),
                env  = Env("local", Dev),
                cfg  = Config(),
                log  = LoggerConsole(),
                ent  = ents,
                inf  = About("myapp", "sample app", "product group 1", "slatekit", "ny", "", "", "", "1.1.0", "", ""),
                dbs  = dbs,
                enc  = MyEncryptor
        )
        ent = ents

        ents.register<User>(true, User::class, UserService::class, serviceCtx = ctx)
        ents.register<Device>(true, Device::class, DeviceService::class, serviceCtx = ctx)

        return RegService(ctx!!, null, false, false, false)
    }


    fun buildSampleInfo(email:String):RegInfo {
        return RegInfo(
                userName = "kishore",
                password = "123456",
                firstName = "kishore",
                lastName = "reddy",
                email = email,
                country = "us",
                state = "NY",
                city = "New York",
                zip = "10000",
                regId = "1234567890",
                phone = "1234567890",
                tag = "test",
                appVersion = "0.9.6",
                devicePlatform = "android",
                deviceName = "and-primary",
                deviceType = "phone",
                deviceOS = "android",
                deviceModel = "samsung"
        )
    }


    @Test fun can_fail_validation() {
        val info = buildSampleInfo("")
        createUser(info, { svc, result, userId ->
            assert(!result.success)
            assert(result.msg == "email not supplied")
        })
    }


    @Test fun can_register_new() {

        val info = buildSampleInfo("unittest-register-new@gmail.com")
        createUser(info, { svc, result, userId ->

            assert(result.success)
            val dsvc = svc.deviceService()
            val device = dsvc.get(userId.deviceId)
            assert(device?.userId == userId.id)
            assert(device?.platform == info.devicePlatform)
        })
    }


    @Test fun can_register_signin() {
        val info = buildSampleInfo("unittest-register-signin@gmail.com")

        createUser(info, { svc, result, token ->

            // Verify device
            var device1 = svc.deviceService().get(token.deviceId)
            svc.confirmPhone(token, device1!!.phoneConfirmCode)
            device1 = svc.deviceService().get(token.deviceId)
            assert(device1!!.isPhoneVerified)

            // Sign in 2nd time
            val signinResult = svc.register(info)
            val device2 = svc.deviceService().get(token.deviceId)

            // Have to reverify the phone
            assert(signinResult.success)
            assert(!device2!!.isPhoneVerified)
            assert(device2!!.phoneConfirmCode != device1.phoneConfirmCode)
            assert(device2!!.token != device1.token)
        })
    }


    @Test fun can_register_edit() {
        val info = buildSampleInfo("unittest-register-edit@gmail.com")

        createUser(info, { svc, result, token ->

            var user1 = svc.userService().get(token.id)
            var device1 = svc.deviceService().get(token.deviceId)
            svc.confirmPhone(token, device1!!.phoneConfirmCode)
            device1 = svc.deviceService().get(token.deviceId)

            val info2 = info.copy(firstName = "kishore2", lastName = "reddy2", phone = "9876543210")
            val update = svc.register(info2)
            val user2 = svc.userService().get(token.id)
            var device2 = svc.deviceService().get(token.deviceId)

            // ensure device info same
            user2?.let { u2 ->
                device2?.let { d2 ->

                    assert(device1!!.isPhoneVerified  != d2.isPhoneVerified)
                    assert(device1!!.token            != d2.token)
                    assert(device1!!.phoneConfirmCode != d2.phoneConfirmCode)

                    assert(u2.firstName == "kishore2"   )
                    assert(u2.lastName  == "reddy2"     )
                    assert(u2.phone     != user1!!.phone)
                    assert(u2.phone     == "9876543210" )
                } ?: throw IllegalArgumentException("Device not retrieved")
            }?: throw IllegalArgumentException("User not retrieved")
        })
    }


    @Test fun can_activate_user() {
        val info = buildSampleInfo("unittest-activate-user@gmail.com")

        createUser(info, { svc, result, userId ->

            // Ensure unverified
            val user = svc.userService().get(userId.id)
            assert(user!!.status == UserConstants.StatusUnverified)

            // Ensure activated
            svc.activate(userId)
            val user2 = svc.userService().get(userId.id)
            assert(user2!!.status == UserConstants.StatusActive)
        })
    }


    @Test fun can_deactivate_user() {
        val info = buildSampleInfo("unittest-deactivate-user@gmail.com")

        createUser(info, { svc, result, userId ->

            // Ensure unverified
            val user = svc.userService().get(userId.id)
            assert(user!!.status == UserConstants.StatusUnverified)

            // Ensure activated
            svc.deactivate(userId)
            val user2 = svc.userService().get(userId.id)
            assert(user2!!.status == UserConstants.StatusDeactivated)
        })
    }


    @Test fun can_confirm_email() {
        val info = buildSampleInfo("unittest-confirm_email@gmail.com")

        createUser(info, { svc, result, token ->

            // Ensure unverified
            val user = svc.userService().get(token.id)
            assert(!user!!.isEmailVerified)

            // Ensure activated
            svc.confirmEmail(token, user.emailConfirmCode)
            val user2 = svc.userService().get(token.id)
            assert(user2!!.isEmailVerified)
        })
    }


    @Test fun can_confirm_phone() {
        val info = buildSampleInfo("unittest-confirm_phone@gmail.com")

        createUser(info, { svc, result, token ->

            // Ensure unverified
            val device = svc.deviceService().get(token.deviceId)
            assert(!device!!.isPhoneVerified)

            // Ensure activated
            svc.confirmPhone(token, device.phoneConfirmCode)
            val device2 = svc.deviceService().get(token.deviceId)
            assert(device2!!.isPhoneVerified)
        })
    }


    @Test fun can_confirm_device() {
        val info = buildSampleInfo("unittest-confirm_device@gmail.com")

        createUser(info, { svc, result, token ->

            // Ensure unverified
            val device = svc.deviceService().get(token.deviceId)
            assert(!device!!.isDeviceVerified)

            // Ensure activated
            svc.confirmDevice(token, device.deviceConfirmCode)
            val device2 = svc.deviceService().get(token.deviceId)
            assert(device2!!.isDeviceVerified)
        })
    }


    private fun createUser(info:RegInfo, callback:(RegService, Result<String>, Token) -> Unit ):Unit {

        val svc = regService()
        val result = svc.register(info)
        if(!result.success) {
            callback(svc, result, Token.empty)
            return
        }
        // Get token
        val token = ctx!!.enc!!.decrypt(result.value!!)
        val userId = Token.parse(token)

        val attempt = try {
            callback(svc, result, userId)
            Pair(true, null)
        }
        catch ( ex:Exception ) {
            Pair(false, ex)
        }

        // Delete
        svc.userService().deleteByField(User::email, info.email)
        svc.deviceService().deleteByField(Device::userId, userId.id)

        if(!attempt.first)
            throw Exception(attempt.second)
    }
}