package slatekit.generator

import java.io.File

/**
 * Template for the generation
 * @param name    : Name of this template e.g. "app"
 * @param version : Version of this template e.g. 1.0.0
 * @param desc    : Description of this template
 * @param type    : Type of this template ( e.g. App | Job | API | CLI | Service | etc )
 * @param actions : List of [Action] to execute to generate this template
 * @param requires: List of required dependent templates
 */
data class Template(val root:File,
                    val parent:File,
                    val dir: File,
                    val path: File,
                    val name:String,
                    val version:String,
                    val desc:String,
                    val type: String,
                    val actions:List<Action>,
                    val requires:List<Template>)