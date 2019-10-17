package test

//import slatekit.common.Random
//import slatekit.db.Db
//import slatekit.entities.Entities
//import slatekit.support.ioc.DI


//
//
//class DITest {
//
//    fun test(){
//        val ent = Entities({ c -> Db(c) })
//        val di = DI.instance
//        di.one<IUsrRepo> { UsrRepo() }
//        di.new { App(Random.alpha6()) }
//        //di.one {  UsrService(di.get(), di.get()) }
//        //di.set<IUserService>( UserService(di.get()))
//
//        val s1   = di.get<App>()
//        val s2   = di.get<App>()
//        val repo1  = di.get<IUsrRepo>()
//        val repo2  = di.get<IUsrRepo>()
//        val svc1 = di.get<UsrService>()
//        val svc2 = di.get<UsrService>()
//
//        println(di.has<IUsrService>())
//
//        println(s1.name)
//        println(s2.name)
//        println(repo1.id)
//        println(repo2.id)
//        println(svc1.id)
//        println(svc2.id)
//    }
//}



