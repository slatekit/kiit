/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 * 
 * 
 *  </kiit_header>
 */

package kiit.integration.mods

data class ModuleInfo(
    val name: String,
    val desc: String,
    val version: String,
    val isInstalled: Boolean,
    val isEnabled: Boolean,
    val isDbDependent: Boolean,
    val totalModels: Int,
    val source: String,
    val dependencies: String,
    val models: List<String>? = null
)