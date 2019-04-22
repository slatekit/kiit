/**
 * <slate_header>
 * url: www.slatekit.com
 * git: www.github.com/code-helix/slatekit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * about: A tool-kit, utility library and server-backend
 * mantra: Simplicity above all else
 * </slate_header>
 */

package slatekit.common.args

import slatekit.common.Types
import slatekit.common.console.SemanticConsole
import slatekit.results.Failure
import slatekit.results.Notice
import slatekit.results.Try

/**
 * stores and builds a list of 1 or more arguments which collectively represent the schema.
 *
 * @note this schema is immutable and returns a  schema when adding additional arguments
 * @param items : the list of arguments.
 */
class ArgsSchema(val items: List<Arg> = listOf()) {

    val any: Boolean get() = items.isNotEmpty()

    /**
     * Adds a argument of type text to the schema
     *
     * @param alias : Short hand alias for the name
     * @param name : Name of argument
     * @param desc : Description
     * @param required : Whether this is required or not
     * @param defaultVal : Default value for argument
     * @param example : Example of value shown for help text
     * @param exampleMany : Examples of values shown for help text
     * @param group : Used to group arguments into categories
     * @return
     */
    fun text(
            alias: String = "",
            name: String,
            desc: String = "",
            required: Boolean = false,
            defaultVal: String = "",
            example: String = "",
            exampleMany: String = "",
            group: String = ""
    ): ArgsSchema =
            add(alias, name, desc, Types.JStringClass, required, defaultVal, example, exampleMany, group)

    /**
     * Adds a argument of type boolean to the schema
     *
     * @param alias : Short hand alias for the name
     * @param name : Name of argument
     * @param desc : Description
     * @param required : Whether this is required or not
     * @param defaultVal : Default value for argument
     * @param example : Example of value shown for help text
     * @param exampleMany : Examples of values shown for help text
     * @param group : Used to group arguments into categories
     * @return
     */
    fun flag(
            alias: String = "",
            name: String,
            desc: String = "",
            required: Boolean = false,
            defaultVal: String = "",
            example: String = "",
            exampleMany: String = "",
            group: String = ""
    ): ArgsSchema =
            add(alias, name, desc, Types.JBoolClass, required, defaultVal, example, exampleMany, group)

    /**
     * Adds a argument of type number to the schema
     *
     * @param alias : Short hand alias for the name
     * @param name : Name of argument
     * @param desc : Description
     * @param required : Whether this is required or not
     * @param defaultVal : Default value for argument
     * @param example : Example of value shown for help text
     * @param exampleMany : Examples of values shown for help text
     * @param group : Used to group arguments into categories
     * @return
     */
    fun number(
            alias: String = "",
            name: String,
            desc: String = "",
            required: Boolean = false,
            defaultVal: String = "",
            example: String = "",
            exampleMany: String = "",
            group: String = ""
    ): ArgsSchema =
            add(alias, name, desc, Types.JIntClass, required, defaultVal, example, exampleMany, group)

    /**
     * Adds a argument to the schema
     *
     * @param alias : Short hand alias for the name
     * @param name : Name of argument
     * @param desc : Description
     * @param dataType : Data type of the argument
     * @param required : Whether this is required or not
     * @param defaultVal : Default value for argument
     * @param example : Example of value shown for help text
     * @param exampleMany : Examples of values shown for help text
     * @param group : Used to group arguments into categories
     * @return
     */
    fun add(
            alias: String = "",
            name: String,
            desc: String = "",
            dataType: Class<*>,
            required: Boolean = false,
            defaultVal: String = "",
            example: String = "",
            exampleMany: String = "",
            group: String = ""
    ): ArgsSchema {
        val typeName = dataType.simpleName
        val arg = Arg(alias, name, desc, typeName
                ?: "string", required, false, false, false, group, "", defaultVal, example, exampleMany)
        val newList = items.plus(arg)
        return ArgsSchema(newList)
    }

    /**
     * whether or not the argument supplied is missing
     *
     * @param args
     * @param arg
     * @return
     */
    fun missing(args: Args, arg: Arg): Boolean = arg.isRequired && !args.containsKey(arg.name)


    fun buildHelp(prefix: String? = "-", separator: String? = "=") {

        // For color and semantic writing
        val writer = SemanticConsole()
        val maxLen = maxLengthOfName()

        items.forEach { arg ->
            val semanticHelp = arg.semantic(prefix, separator, maxLen)
            writer.writeItems(semanticHelp)
        }
    }

    /**
     * gets the maximum length of an argument name from all arguments
     *
     * @return
     */
    private fun maxLengthOfName(): Int = if (items.isEmpty()) 0 else items.maxBy { it.name.length }?.name?.length ?: 0


    companion object {

        /**
         * Parses the line against the schema and transforms any aliases into canonical names
         */
        @JvmStatic fun parse(schema:ArgsSchema, line:String):Try<Args> {
            val parsed = Args.parse(line, "-", "=", true)
            return parsed.map { transform(schema, it)  }
        }

        /**
         * Transforms the arguments which may have aliases into their canonical names
         * @param schema: The argument schema to transform aliases against
         * @param args  : The parsed arguments
         */
        @JvmStatic fun transform(schema: ArgsSchema?, args: Args): Args {
            if(schema == null) return args
            val canonical = args.named.toMutableMap()
            val aliased = schema.items.filter { !it.alias.isNullOrBlank() }
            if(aliased.isEmpty()) {
                return args
            }
            aliased.forEach { arg ->
                if(canonical.containsKey(arg.alias)){
                    val value = canonical.get(arg.alias)
                    if(value != null){
                        canonical.remove(arg.alias)
                        canonical[arg.name] = value
                    }
                }
            }
            val finalArgs = args.copy(namedArgs = canonical)
            return finalArgs
        }

        /**
         * Validates the arguments against this schema
         * @param schema: The argument schema to validate against
         * @param args  : The parsed arguments
         */
        @JvmStatic fun validate(schema: ArgsSchema, args: Args): Notice<Boolean> {
            val missing = schema.items.filter { arg -> arg.isRequired && !args.containsKey(arg.name) }
            return if (missing.isNotEmpty()) {
                Failure("invalid arguments supplied: Missing : " + missing.first().name)
            } else {
                slatekit.results.Success(true)
            }
        }
    }
}
