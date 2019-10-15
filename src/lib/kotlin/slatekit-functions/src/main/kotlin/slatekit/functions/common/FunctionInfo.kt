package slatekit.functions.common

import slatekit.common.Random
import slatekit.common.toId

/**
 * @param name     : name of the command e.g "createUser" "admin.users.create"
 * @param desc     : description of what this function does
 * @param uuid     : optional uuid to uniquely identify this function
 * @param alias    : optional alias for the function
 * @param version  : optional version of the function
 * @param category : optional category of the function
 */
data class FunctionInfo(
        val name: String,
        val desc: String,
        val uuid: String = Random.uuid(),
        val alias: String = name,
        val version: String = "1.0",
        val category: FunctionType = FunctionType.Misc
) {

    /**
     * Converts the name to a reasonable id without spaces and toLowercase
     */
    val nameId:String = name.toId()


    /**
     * Parts of the name. This is if the name has a namespace such as area.group.action
     */
    val parts: List<String> = if (name.contains(".")) name.split(".").toList() else listOf()


    /**
     * The 1st part of name if name has a namespace.
     * E.g. "admin", the logical area associated with this command
     */
    val area: String = if (parts.isNotEmpty()) parts[0] else ""


    /**
     * The 2nd part of name if name has a namespace
     * E.g. "users", the logical group associated with this command
     */
    val group: String = if (parts.size > 1) parts[1] else ""


    /**
     * The 3rd part of name if name has a namespace
     * E.g. "create", the logical action associated with this command
     */
    val action: String = if (parts.size > 2) parts[2] else ""
}