package slatekit.generator

/**
 * Template for the generation
 * @param name    : e.g. App
 * @param actions : List of [Action] to execute to generate this template
 */
data class Template(val name:String, val type: TemplateType, val actions:List<Action>)