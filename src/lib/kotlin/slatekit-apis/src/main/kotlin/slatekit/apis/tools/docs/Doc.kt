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

package slatekit.apis.tools.docs

import java.lang.Math.abs
import kotlin.reflect.KParameter
import slatekit.apis.Input
import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.apis.core.Area
import slatekit.apis.core.Lookup
import slatekit.common.console.SemanticConsole
import slatekit.common.console.SemanticConsoleSettings
import slatekit.common.console.SemanticText
import slatekit.common.console.SemanticWrites
import slatekit.common.ext.orElse
import slatekit.meta.KTypes
import slatekit.meta.Serialization

abstract class Doc  {

    protected open val writer: SemanticWrites = SemanticConsole(SemanticConsoleSettings())
    open val docSettings = DocSettings()
    open val pathSeparator = "."
    open val helpSuffix = "?"
    open val helpSeparator = " "
    open val headerDelimeter = "-"
    open val headerDelimeterLength = 40

    fun lineBreak() {
        writer.text("---------------------------------------------------------------", endLine = true)
    }

    fun error(msg: String) {
        writer.failure(msg)
    }

    fun begin(){
        writer.line()
        writer.text(headerDelimeter.repeat(headerDelimeterLength), true)
    }

    fun end() {
        writer.line()
    }

    fun section(call:() -> Unit ){
        begin()
        call()
        end()
    }

    /**
     * Builds the list of areas:
     *
     * AREAS
     *
     * 1. areaA
     * 2. areaB
     * 3. areaC
     *
     * use  {area} ? to list all APIs in an area
     * e.g. areaA ?
     */
    fun areas(areas: Lookup<Area>) {
        section {
            writer.title("AREAS", endLine = true)
            areas.items.forEachIndexed { ndx, area -> writer.highlight((ndx + 1).toString() + "." + area.name, endLine = true) }
            writer.line()

            // Usage
            writer.text("use {area}$helpSeparator$helpSuffix to list all apis in the area. ")
            writer.url("e.g. ${areas.items.first().name} ?", endLine = true)
            lineBreak()
        }
    }

    /**
     * Builds the list of areas:
     *
     * AREA
     * areaA
     *
     * APIs
     * apiA : description of API
     * apiB : description of API
     * apiC : description of API
     *
     * use  {area} {api}? to list all Actions in an API
     * e.g. areaA apiA ?
     */
    fun area(area:Area){
        section {
            writer.title("AREA", endLine = true)
            writer.highlight(area.name, endLine = true)
            writer.line()

            // APISs
            writer.subTitle("APIs", endLine = true)
            area.apis.items.sortedBy { it.name }.forEach { buildApi(it, details = false) }
            writer.line()

            // Usage
            writer.text("use {area}$helpSeparator{api}$helpSuffix to list all Actions in an API. ")
            writer.url("e.g. ${area.name}$helpSeparator${area.apis.items.first().name}?", endLine = true)
        }
    }


    /**
     * Builds the list of areas:
     *
     * AREA
     * areaA
     *
     * APIs
     * apiA : description of API
     *
     * ACTIONS
     *      action1: description of ACTION
     *      action2: description of ACTION
     *      action3: description of ACTION
     *
     * use  {area} {api} {action}? to list Action details
     * e.g. areaA apiA action1 ?
     */
    fun api(area:Area, api: Api){
        section {
            writer.title("AREA", endLine = true)
            writer.highlight(area.name, endLine = true)
            writer.line()

            // APISs
            writer.subTitle("API", endLine = true)
            buildApi(api, details = true)
            writer.line()

            // Actions
            writer.subTitle("ACTIONS", endLine = true)
            val maxLength = api.actions.items.maxBy { it.name }?.name?.length ?: 10
            api.actions.items.sortedBy { it.name }.forEach{ buildAction(api, it, maxLength,false)}
            writer.line()

            // Usage
            writer.text("use {area}$helpSeparator{api}$helpSeparator{action}$helpSuffix to list Action details. ")
            writer.url("e.g. ${area.name}$helpSeparator${api.name}$helpSeparator${api.actions.items.first().name}?", endLine = true)
        }
    }

    /**
     * Builds the list of areas:
     *
     * AREA
     * areaA
     *
     * APIs
     * apiA : description of API
     *
     * ACTIONS
     *      action1: description of ACTION
     *      action2: description of ACTION
     *      action3: description of ACTION
     *
     * use  {area} {api} {action}? to list Action details
     * e.g. areaA apiA action1 ?
     */
    fun action(area:Area, api: Api, action: Action){
        section {
            writer.title("AREA", endLine = true)
            writer.highlight(area.name, endLine = true)
            writer.line()

            // APISs
            writer.subTitle("API", endLine = true)
            buildApi(api, details = true)
            writer.line()

            // Actions
            writer.line()

            // Usage
            writer.text("use {area}$helpSeparator{api}$helpSeparator{action}$helpSuffix to list Action details. ")
            writer.url("e.g. ${area.name}$helpSeparator${api.name}$helpSeparator${api.actions.items.first().name}?", endLine = true)
        }
    }

    protected fun getFormattedText(text: String, max: Int): String {
        return if (text.length == max)
            text
        else {
            val remainder =  writer.SPACE.repeat(abs(max - text.length))
            text + remainder
        }
    }

    private fun buildApi(api: Api, details:Boolean ) {
        writer.highlight(getFormattedText(api.name, (docSettings.maxLengthApi ) + 3), endLine = false)
        writer.text(":", endLine = false)
        writer.text(api.desc, endLine = true)

        if(details) {
            with(writer) {
                line()
                keyValue("route", "${api.area}$pathSeparator${api.name}", true)
                keyValue("area", api.area, true)
                keyValue("name", api.name, true)
                keyValue("verb", api.verb.name, true)
                keyValue("roles", api.roles.all.joinToString(","), true)
                keyValue("proto", api.sources.all.joinToString(",") { it.id }, true)
            }
        }
    }

    private fun buildAction(api: Api, action: Action, maxLength:Int, details: Boolean) {
        writer.tab(1)
        writer.highlight(getFormattedText(action.name, 0 + 3), endLine = false)
        writer.text(":", endLine = false)
        writer.text(action.desc, endLine = true)
    }


    fun onApiActionExample(
        api: Api,
        actionName: String,
        action: Action,
        args: List<KParameter>
    ) {
        writer.line()

        val exampleCli = buildPath(api.area, api.name, actionName, null)
        val exampleWeb = buildPath(api.area, api.name, actionName, "/")
        val paramsCli = args.fold("", { s, arg ->
            s + "-" + arg.name + "=" + KTypes.getTypeExample(arg.name!!, arg.type, "'a bc'") + " "
        })
        val paramsQuery = args.fold("", { s, arg ->
            s + "&" + arg.name + "=" + KTypes.getTypeExample(arg.name!!, arg.type, "a%20bc")
        })
        val serializer = Serialization.sampler()
        val json = serializer.serialize(args)
        writer.tab(1)
        writer.url("1. cli      : $exampleCli ", endLine = false)
        writer.text(paramsCli, true)

        writer.tab(1)
        writer.url("2. web/url  : $exampleWeb ", endLine = false)
        writer.text(paramsQuery, true)

        if (!actionName.startsWith("get")) {
            writer.tab(1)
            writer.url("3. web/json : $exampleWeb ", endLine = false)
            writer.text(json, true)
        }

        writer.line()
    }

    fun argsHeader(action: Action) {
        writer.text("Inputs : ", true)
    }

    fun argInfo(arg: Input) {

        val example = if (arg.examples.size > 0) arg.examples[0] else ""
        onArgBegin(arg.name, arg.desc, arg.required, arg.name, arg.defaults, example)
    }

    fun onArgBegin(
        name: String,
        desc: String,
        required: Boolean,
        type: String,
        defaultVal: String,
        eg: String
    ) {
        writer.line()
        writer.tab(2)

        // 1. name of the argument and description
        // e.g. email  : ""
        writer.highlight(getFormattedText(name, (docSettings.maxLengthArg ?: 10) + 3), endLine = false)
        writer.text(":", endLine = false)
        writer.text(desc.orElse("\"\""), endLine = true)

        // 2. required/optional
        writer.tab(2)
        val space = getFormattedText("", (docSettings.maxLengthArg ?: 10) + 3)
        writer.text(space, endLine = false)

        val txt = if (required) "!" else "?"
        if (required) {
            writer.important(txt, endLine = false)
            writer.text("required : " + type, endLine = false)
        } else {
            writer.text(txt, endLine = false)
            writer.text("optional : " + type, endLine = false)
        }
    }

    open fun buildPath(action: Action, sep: String? = null): String {
        return buildPath("", "", action.name, sep)
    }

    open fun buildPath(area: String, api: String, action: String, sep: String? = null): String {
        val separator = sep ?: pathSeparator
        return area + separator + api + separator + action
    }
}
