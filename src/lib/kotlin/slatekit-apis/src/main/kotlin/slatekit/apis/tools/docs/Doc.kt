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
import slatekit.apis.core.Action
import slatekit.apis.core.Api
import slatekit.apis.core.Area
import slatekit.apis.core.Lookup
import slatekit.common.console.ConsoleWriter
import slatekit.common.console.TextSettings
import slatekit.common.console.TextType
import slatekit.common.console.Writer
import kotlin.reflect.KClass

abstract class Doc  {

    protected open val writer: Writer = ConsoleWriter(TextSettings())
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
            areas.items.forEachIndexed { ndx, area ->
                writer.tab()
                writer.highlight((ndx + 1).toString() + "." + area.name, endLine = true)
            }
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
            writer.tab()
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
            writer.title("API", endLine = true)
            buildApi(api, details = true)
            writer.line()

            // Actions
            writer.subTitle("ACTIONS", endLine = true)
            val maxLength = api.actions.items.maxBy { it.name.length }?.name?.length ?: 15
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
            writer.title("ACTION", endLine = true)
            writer.text(action.verb.name + " ", endLine = false)
            writer.url("$pathSeparator${area.name}$pathSeparator${api.name}$pathSeparator${action.name}", endLine = true)
            writer.line()

            // APISs
            writer.subTitle("API", endLine = true)
            buildApi(api, details = true)
            writer.line()

            // Actions
            writer.subTitle("ACTION", endLine = true)
            buildAction(api, action,30, true)
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
        writer.tab()
        writer.highlight(getFormattedText(api.name, (docSettings.maxLengthApi ) + 3), endLine = false)
        writer.text(":", endLine = false)
        writer.text(api.desc, endLine = true)

        if(details) {
            with(writer) {
                line()
                tab(); keyValue("route", "${api.area}$pathSeparator${api.name}", true)
                tab(); keyValue("area ", api.area, true)
                tab(); keyValue("name ", api.name, true)
                tab(); keyValue("verb ", api.verb.name, true)
                tab(); keyValue("auth ", api.auth.name, true)
                tab(); keyValue("roles", api.roles.all.joinToString(","), true)
                tab(); keyValue("proto", api.sources.all.joinToString(",") { it.id }, true)
            }
        }
    }

    private fun buildAction(api: Api, action: Action, maxLength:Int, details: Boolean) {
        writer.tab()
        writer.highlight(getFormattedText(action.name, maxLength), endLine = false)
        writer.text(":", endLine = false)
        writer.text(action.desc, endLine = true)

        if(details) {
            with(writer) {
                line()
                tab(); keyValue("name ", action.name, true)
                tab(); keyValue("verb ", action.verb.name, true)
                tab(); keyValue("auth ", action.auth.name, true)
                tab(); keyValue("roles", action.roles.all.joinToString(","), true)
                tab(); keyValue("proto", action.sources.all.joinToString(",") { it.id }, true)
            }
            writer.line()
            writer.subTitle("INPUTS", endLine = true)
            action.paramsUser.forEachIndexed { ndx, input ->
                writer.tab()
                writer.highlight( (ndx + 1).toString() + ". " + input.name!!, endLine = true)
                val cls = input.type.classifier as KClass<*>
                val type = when(input.type.arguments.isEmpty()){
                    true -> cls.simpleName!!
                    false -> input.type.arguments.joinToString { (it.type?.classifier as KClass<*>).simpleName!! }
                }
                writer.tab(); writer.keyValue("type    ", type, true)
                writer.tab(); writer.keyValue("required", (!input.isOptional).toString(), true)
                writer.line()
            }
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
