package kiit.apis.setup

import kiit.apis.Access
import kiit.apis.AuthMode
import kiit.apis.AuthModes
import kiit.apis.Verb
import kiit.apis.core.Roles
import kiit.apis.core.Sources

object References {

    fun auth(parent:AuthMode, childAuth:String?): AuthMode {
        return when(childAuth) {
            AuthModes.PARENT -> parent
            null -> parent
            else -> AuthMode.parse(childAuth)
        }
    }


    fun roles(parent:Roles, childRoles:Array<String>): Roles {
        return when {
            childRoles.isEmpty() -> parent
            else -> Roles.of(childRoles)
        }
    }


    fun access(parent:Access, childAccess:String?): Access {
        return when(childAccess) {
            AuthModes.PARENT -> parent
            null -> parent
            else -> Access.parse(childAccess)
        }
    }


    fun sources(parent:Sources, childAccess:Array<String>): Sources {
        return when {
            childAccess.isEmpty() -> parent
            else -> Sources.of(childAccess)
        }
    }


    fun version(parent:String, childAccess:String?): String {
        return childAccess ?: "0"
    }


    fun verb(parent:Verb, childVerb:String?, childName:String): Verb {
        val verb = when(childVerb) {
            AuthModes.PARENT -> parent
            null -> parent
            else -> Verb.parse(childVerb)
        }
        return when(verb) {
            Verb.Auto -> toVerb(childName)
            else -> verb
        }
    }
}
