package slatekit.common.functions

/**
 * @param name     : name of the command e.g "createUser" "admin.users.create"
 * @param desc     : description of what this command does
 * @param version  : optional version of the command
 * @param category : optional category of the command
 */
data class FunctionInfo(
        val name: String,
        val desc: String,
        val alias: String = name,
        val version: String = "1.0",
        val category: FunctionType = FunctionType.Generic
) {

    /**
     * Parts of the name. This is if the name namespaced such as area.group.action
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