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

package slatekit.common.templates

import slatekit.common.ResultEx
import slatekit.common.getOrElse
import slatekit.common.templates.TemplateConstants.TypeSub
import slatekit.common.templates.TemplateConstants.TypeText


/**
 * Handles processing of text templates with variables/substitutions inside.
 *
 * @param templates
 * @param variables
 * @param setDefaults
 */
class Templates(val templates: List<Template>? = null,
                val variables: List<Pair<String, (TemplatePart) -> String>>? = null,
                setDefaults: Boolean = true) {

    /**
     * The actual variables/substitutions that map to functions to substite the values
     */
    val subs = Subs(variables, setDefaults)


    val emptyParts = listOf<TemplatePart>()


    /**
     * parses the text template
     *
     * @param text
     * @return
     */
    fun parse(text: String): ResultEx<List<TemplatePart>> = TemplateParser(text).parse()


    /**
     * parses the text and returns a parsed template with individual parts.
     *
     * @param text
     * @return
     */
    fun parseTemplate(name: String, text: String): Template {
        val result = parse(text)
        return Template(
                name = name,
                content = text,
                parsed = true,
                valid = result.success,
                status = result.msg,
                group = null,
                path = null,
                parts = result.getOrElse({ listOf<TemplatePart>() })
        )
    }


    /**
     * Processes the template with the variables supplied during creation
     *
     * @param text
     * @return
     */
    fun resolve(text: String): String? = resolve(text, subs)


    /**
     * Processes the stored template associated with the name, with the variables supplied
     * at creation.
     *
     * @param name
     * @return
     */
    fun resolveTemplate(name: String, substitutions: Subs? = null): String? {

        val template = templates?.filter { it.name == name }?.firstOrNull()
        val result = template?.let { t ->
            if (t.valid)
                resolveParts(t.parts ?: emptyParts, substitutions ?: subs)
            else
                null
        }
        return result
    }


    fun resolveTemplateWithVars(name: String, vars: Map<String, Any>): String? {
        val template = templates?.filter { it.name == name }?.firstOrNull()

        val result = template?.let { t ->
            if (t.valid)
                resolvePartsWithVars(t.parts ?: emptyParts, vars)
            else
                null
        }
        return result
    }


    /**
     * Processes the template with the variables supplied
     *
     * @param text
     * @return
     */
    fun resolve(text: String, substitutions: Subs?): String? {
        val result = parse(text)

        // Failed parsing ?
        val finalResult = if (result.success) {
            val parts = result.getOrElse { listOf() }
            if (parts.isEmpty()) {
                    text
                }
                else {
                resolveParts(parts, substitutions ?: subs)
            }

        }
        else {
            result.msg
        }
        return finalResult
    }


    private fun resolveParts(tokens: List<TemplatePart>, substitutions: Subs): String? {
        val finalText = tokens.fold("", { s, t ->
            when (t.subType) {
                TypeText -> s + t.text
                TypeSub  -> s + substitutions.lookup(t.text)
                else     -> s + ""
            }
        })
        return finalText
    }


    private fun resolvePartsWithVars(tokens: List<TemplatePart>, vars: Map<String, Any>?): String? {
        val finalText = tokens.fold("", { s, t ->
            when (t.subType) {
                TypeText -> s + t.text
                TypeSub  -> s + resolveToken(t, vars)
                else     -> s + ""
            }
        })
        return finalText
    }


    private fun resolveToken(token: TemplatePart, vars: Map<String, Any>?): Any {
        val result = vars?.let { v ->
            if (v.containsKey(token.text)) {
                val vr = v[token.text]
                vr ?: ""
            }
            else if (subs.contains(token.text)) {
                subs[token.text]
            }
            else {
                ""
            }
        } ?: ""
        return result
    }

    companion object TemplatesCompanion {

        /**
         * Builds the templates object after parsing the given individual templates
         *
         * @param templates
         * @param subs
         * @return
         */
        fun build(templates: List<Template>,
                  subs: List<Pair<String, (TemplatePart) -> String>>? = null): Templates {

            // Each template
            val parsed = parse(templates)
            return Templates(parsed, subs)
        }


        fun subs(items: List<Pair<String, (TemplatePart) -> String>>): Subs = Subs(items)


        fun parse(templates: List<Template>): List<Template> {

            // Each template
            val parsed = templates.map { template ->

                // Parse the template into individual parts( plain text, variables )
                val result = TemplateParser(template.content).parse()

                // Build the  template
                template.copy(parsed = true,
                        valid = result.success,
                        parts = result.getOrElse { listOf() }
                )
            }
            return parsed
        }
    }
}
