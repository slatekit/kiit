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

package slatekit.integration.mods

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