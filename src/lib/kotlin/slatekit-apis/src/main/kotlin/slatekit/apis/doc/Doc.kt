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

import slatekit.apis.ApiArg
import slatekit.apis.core.Action
import slatekit.apis.support.ApiInfo
import slatekit.common.IO
import slatekit.common.console.ConsoleSettings
import slatekit.common.console.ConsoleWrites
import java.lang.Math.abs


abstract class Doc : ApiVisit, ConsoleWrites {

    /**
     * IO abstraction for system.println.
     * Assists with testing and making code a bit more "purely functional"
     * This is a simple, custom alternative to the IO Monad.
     * Refer to IO.scala for details.
     */
    override val _io: IO<Any, Unit> = slatekit.common.Println
    private val _settings = DocSettings()
    private val _consoleSettings = ConsoleSettings()
    override val docSettings: DocSettings get() = _settings
    override val settings: ConsoleSettings get() = _consoleSettings


    fun lineBreak(): Unit {
        text("---------------------------------------------------------------", endLine = true)
    }


    override fun onApiError(msg: String): Unit {
        error(msg)
    }


    override fun onVisitSeparator(): Unit {
        line()
    }


    override fun onAreasBegin(): Unit {
        lineBreak()
        title("supported areas: ", endLine = true)
        line()
    }


    override fun onAreasEnd(): Unit {
        text("type '{area} ?' to list all apis in the area. ")
        url("e.g. sys ?", endLine = true)
        lineBreak()
    }


    override fun onAreaBegin(area: String): Unit {
        highlight(area, endLine = true)
    }


    override fun onAreaEnd(area: String): Unit {
    }


    override fun onApisBegin(area: String): Unit {
        lineBreak()
        title("supported apis: ", endLine = true)
        line()
    }


    override fun onApisEnd(area: String, exampleApi: String?): Unit {
        val eg = exampleApi ?: "sys.models"
        line()
        text("type {area}.{api} ? to list all actions on an api. ")
        url("e.g. $eg ?", endLine = true)
        lineBreak()
    }


    override fun onApiEnd(api: ApiInfo): Unit {
        line()
    }


    override fun onArgEnd(arg: ApiArg): Unit {
        line()
    }


    override fun onApiActionSyntax(action: Action?): Unit {
        val example = action?.let { it.api.area + "." + it.api.name + "." + it.name }
                ?: "sys.models.install"
        line()
        text("type {area}.{api}.{action} ? to list inputs for an action. ")
        url("e.g. $example ?", endLine = true)
        lineBreak()
    }


    protected fun getFormattedText(text: String, max: Int): String {
        return if (text.length == max)
            text
        else {
            val remainder = " ".repeat(abs(max - text.length))
            text + remainder
        }
    }
}
