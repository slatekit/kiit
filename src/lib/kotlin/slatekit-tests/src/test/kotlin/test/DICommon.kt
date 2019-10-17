package test

import slatekit.common.Random
import slatekit.core.sms.SmsService

/**
 * Generice Interfaces
 */
interface Repo<T> {
    val id:String
    fun save(item:T)
}


interface Service<T> {
    val id:String
    fun save(item:T)
}


/**
 * Generice Implementations
 */
open class SimpleRepo<T> : Repo<T> {
    var t:T? = null
    override val id:String = "repo:" + Random.alpha3()

    override fun save(item:T){
        t = item
    }


    fun get():T? = t
}

open class SimpleService<T>(val app:App, val repo:Repo<T>) : Service<T> {
    override val id:String = "app:" + app.name + "_" + repo.id + "_" + "svc:" + Random.alpha3()

    override fun save(item:T){
        println("saving")
        repo.save(item)
        println("done")
    }
}


/**
 * Application objects
 */
class Usr(val name:String)
class App(val name:String)


interface IUsrService : Service<Usr>
interface IUsrRepo : Repo<Usr>
class UserApi(val service:UsrService)


class UsrService(app:App, repo:IUsrRepo) : SimpleService<Usr>(app, repo), IUsrService
class UsrRepo : SimpleRepo<Usr>(), IUsrRepo
class SignupService(val userService:IUsrService, val smsService: SmsService)

