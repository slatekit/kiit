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

package slatekit.apis.doc

import slatekit.apis.ApiAction
import slatekit.apis.ApiArg
import slatekit.apis.support.ApiInfo
import slatekit.common.IO
import slatekit.common.Strings
import slatekit.common.console.ConsoleSettings
import slatekit.common.console.ConsoleWrites
import kotlin.reflect.KParameter

/**
 * Generates help docs on the console.
 * TODO: Refactor this code a bit. May be able to use
 * recursion/tail-rec instead of some of the remnant visitor pattern.
 */
class DocConsole : Doc(), ConsoleWrites {

    override val _io: IO<Any, Unit> = slatekit.common.Print
    private val _consoleSettings = ConsoleSettings()
    override val settings: ConsoleSettings get() = _consoleSettings


    override fun onApiBegin(api: ApiInfo, options: ApiVisitOptions?): Unit {
        highlight(getFormattedText(api.name, (options?.maxLength ?: 0) + 3), endLine = false)
        text(":", endLine = false)
        text(api.desc, endLine = options?.endApiWithLine ?: false)
    }


    override fun onApiActionBegin(action: ApiAction, name: String, options: ApiVisitOptions?): Unit {
        tab(1)
        subTitle(getFormattedText(name, (options?.maxLength ?: 0) + 3), endLine = false)
        text(":", endLine = false)
        text(action.desc, endLine = false)
    }


    override fun onApiActionEnd(action: ApiAction, name: String): Unit {
        line()
    }


    override fun onApiActionExample(api: ApiInfo, actionName: String, action: ApiAction,
                                    args: List<KParameter>): Unit {
        line()
        tab(1)
        val fullName = api.area + "." + api.name + "." + actionName
        val txt = args.fold("", { s, arg ->
            s + "-" + arg.name + "=" + arg.name!! + " "
        })
        url(fullName + " ", endLine = false)
        text(txt, true)
        line()
    }


    override fun onArgBegin(arg: ApiArg, options: ApiVisitOptions?): Unit {
        onArgBegin(arg.name, arg.desc, arg.required, arg.name, arg.defaultVal, arg.eg, options)
    }


    override fun onArgBegin(
            name: String,
            desc: String,
            required: Boolean,
            type: String,
            defaultVal: String,
            eg: String,
            options: ApiVisitOptions?
    ): Unit {
        line()
        tab(2)

        // 1. name of the argument and description
        // e.g. email  : ""
        highlight(getFormattedText(name, (options?.maxLength ?: 10) + 3), endLine = false)
        text(":", endLine = false)
        text(Strings.valueOrDefault(desc, "\"\""), endLine = true)

        // 2. required/optional
        tab(2)
        val space = getFormattedText("", (options?.maxLength ?: 10) + 3)
        text(space, endLine = false)

        val txt = if (required) "!" else "?"
        if (required) {
            important(txt, endLine = false)
            text("required : " + type, endLine = false)
        }
        else {
            text(txt, endLine = false)
            text("optional : " + type, endLine = false)
        }
    }
}
