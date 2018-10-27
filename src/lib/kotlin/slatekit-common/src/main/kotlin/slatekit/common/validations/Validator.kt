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

open class Validator<T> {

    open fun validate(item: T): ValidationResults = ValidationResults.build(null)


    fun result(isValid: Boolean, ref: Reference?, error: String): ValidationResult =
            if (isValid) {
                ValidationResult(true, error, ref, 1)
            }
            else {
                ValidationResult(false, error, ref, 0)
            }
}
