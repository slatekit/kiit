package test

//import org.koin.core.KoinComponent
//import org.koin.core.context.startKoin
//import org.koin.core.get
//import org.koin.core.inject
//import org.koin.core.module.Module
//import org.koin.dsl.module
//import slatekit.common.Random
//import slatekit.common.encrypt.HMAC
//import slatekit.common.info.ApiLogin
//import slatekit.core.email.EmailService
//import slatekit.core.email.EmailServiceSendGrid
//import slatekit.core.sms.SmsService
//import slatekit.core.sms.SmsServiceTwilio
//
//
//object CoreModule : KoinComponent {
//
//    fun mod():Module {
//        return module {
//            single  { SmsServiceTwilio(ApiLogin.empty) as SmsService }
//            single  { EmailServiceSendGrid(ApiLogin.empty) as EmailService  }
//            single  { HMAC("abc".toByteArray()) }
//        }
//    }
//}
//
//
//object UserModule : KoinComponent {
//
//    fun mod():Module {
//        return module {
//            factory { App(Random.alpha3())  }
//            single<IUsrRepo>      { UsrRepo() }
//            single<IUsrService>   { UsrService(get(), get()) }
//            single<SignupService> { SignupService(get(), get()) }
//        }
//    }
//}
//
//
//class DIKoin : KoinComponent {
//
//    fun test() {
//        startKoin {
//            // use Koin logger
//            printLogger()
//            // declare modules
//            modules(
//                    CoreModule.mod(),
//                    UserModule.mod()
//            )
//        }
//
//        val s1   by inject<App>()
//        val s2   by inject<App>()
//        val repo1  by inject<IUsrRepo>()
//        val repo2  by inject<IUsrRepo>()
//        val svc1 by inject<IUsrService>()
//        val svc2 by inject<IUsrService>()
//        val signup by inject<SignupService>()
//        println(signup.smsService)
//        println(signup.userService)
//        println(s1.name)
//        println(s2.name)
//        println(repo1.id)
//        println(repo2.id)
//        println(svc1.id)
//        println(svc2.id)
//
//        val f1 = Feature1(this)
//        f1.test()
//    }
//}
//
//
//class Feature1(val koin: KoinComponent){
//
//    val s1:App  = koin.get()
//    val s2:App  = koin.get()
//    val repo1:IUsrRepo = koin.get()
//    val repo2:IUsrRepo = koin.get()
//    val svc1:IUsrService = koin.get()
//    val svc2:IUsrService = koin.get()
//
//
//    fun test(){
//        println(s1.name)
//        println(s2.name)
//        println(repo1.id)
//        println(repo2.id)
//        println(svc1.id)
//        println(svc2.id)
//    }
//}