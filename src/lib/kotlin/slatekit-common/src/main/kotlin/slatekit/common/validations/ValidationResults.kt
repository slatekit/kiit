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

package slatekit.common.validations

data class ValidationResults(val success: Boolean,
                             val msg: String = "",
                             val ref: Reference? = null,
                             val code: Int = 0,
                             val results: List<ValidationResult>?) {
    // Any errors ?
    val hasErrors = results?.isEmpty() ?: false


    companion object {

        @JvmStatic
        fun build(errors: List<ValidationResult>?): ValidationResults {
            val success = errors?.isEmpty() ?: true
            val message = errors?.first()?.msg ?: ""
            val code = if (success) 1 else 0
            return ValidationResults(success, message, null, code, errors)
        }
    }
}

