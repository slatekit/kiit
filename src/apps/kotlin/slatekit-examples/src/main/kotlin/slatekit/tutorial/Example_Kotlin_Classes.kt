/**
<slate_header>
url: www.slatekit.com
git: www.github.com/code-helix/slatekit
org: www.codehelix.co
author: Kishore Reddy
copyright: 2016 CodeHelix Solutions Inc.
license: refer to website and/or github
about: A Scala utility library, tool-kit and server backend.
mantra: Simplicity above all else
</slate_header>
 */

package slatekit.tutorial

import slatekit.common.Result
import slatekit.common.results.ResultFuncs.ok
import slatekit.core.cmds.Cmd
import java.time.LocalDate


class Example_Kotlin_Classes : Cmd("types") {

    override fun executeInternal(args: Array<String>?): Result<Any> {

        return ok()
    }

}

// SUB-CLASSING:
// 1. You must declare your class as "open" to sub-class it
// 2. You must declare a method as "open" to override it
open class UserAccount( val name:String, val isActive:Boolean, val roles:String )
{
    open fun info():String = "USER: $name $isActive $roles"


    // You can put "static" methods on a class inside a companion object
    companion object {
        // This is essentially a static field.
        val guest = UserAccount("guest", false, "")
    }
}


// Just a simple example showing how to extend a class.
class AdminAccount(name:String) : UserAccount(name, true, "admin")
{
    override fun info():String = "ADMIN: $name $isActive $roles"
}


// This is essentially a singleton class
object Accounts {
    // This is essentially a static field.
    val guest = UserAccount("guest", false, "")
}


fun testStatic():Unit {
    val guestFromCompanion = UserAccount.guest
    val guestFromExternalCls = Accounts.guest
}


// Mark class with "data".
// USE CASES:
// 1. Data classes are useful when you typically want to just hold data
// 2. They are also useful in pattern-matching.
//
// AUTOMATIC SUPPORT
// Data classes have the "equals()", "toString()" and "copy(...)"
// methods automatically created by the compiler.
data class Role(val name:String, val desc:String, val features:List<String>)


fun testRoles():Unit {

    // Create role with name, desc, and feature list
    val role1 = Role("editors", "Create / Edit content", listOf("blogs", "comments"))
    println(role1)

    // Copy role1 with existing features but just change name and desc.
    val role2 = role1.copy("moderators", "moderate content")
    println(role2)
}


// Interfaces can have implementations
interface AccountSupport {
    fun activate():Unit {
        // do some processing and notify
        notify("account activated")
    }


    fun deactivate():Unit {
        // do some processing and notify
        notify("account deactivated")
    }


    fun notify(state:String):Unit {
        // e.g. send to phone / log / audit etc.
        // just println for sample purposes
        println(state)
    }
}


// Just a simple example showing how to extend a class.
class ModeratorAccount(name:String)
    : UserAccount(name, true, "moderators")
    , AccountSupport
{
    override fun info():String = "MOD: $name $isActive $roles"
}


fun testInterfaces():Unit {
    val mod = ModeratorAccount("john")
    mod.activate()
    mod.deactivate()
}


// 1. You can declare class members in the constructor
// 2. You can declare and set the access modifiers ( none = public, protected, private )
// 3. You can declare whether they are mutable ( var ) or immutable ( val )
open class User(val name: String,
                var isActive: Boolean,
                protected val email: String,
                private val account: Int

) {
    init {
        // initialization after primary constructor.
        println("instantiated with : " + name)
    }


    // Overloaded constructors must call the primary constructor first
    constructor() : this("", false, "", 0)


    // Overloaded constructors must call the primary constructor first
    constructor(name: String) : this(name, false, "", 0)


    fun printInfo(): Unit {

        println("$name, $email, $account, $isActive")
    }
}
