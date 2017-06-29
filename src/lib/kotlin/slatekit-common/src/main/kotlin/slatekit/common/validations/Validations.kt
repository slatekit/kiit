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

object Validations {

    /**
     * checks if all the rules are true against the input
     * @param rules : The rules to check
     * @param input : The input to check rules against
     * @tparam T
     * @return
     */
    fun <T> allTrue(rules: List<(T) -> Boolean>, input: T): Boolean = rules.all { rule -> rule(input) }


    /**
     * checks if all the rules are false against the input
     * @param rules : The rules to check
     * @param input : The input to check rules against
     * @tparam T
     * @return
     */
    fun <T> allFalse(rules: List<(T) -> Boolean>, input: T): Boolean = rules.all { rule -> !rule(input) }


    /**
     * checks if any of the rules are true against the input
     * @param rules : The rules to check
     * @param input : The input to check rules against
     * @tparam T
     * @return
     */
    fun <T> anyTrue(rules: List<(T) -> Boolean>, input: T): Boolean = rules.any { rule -> rule(input) }


    /**
     * checks if any of the rules are false against the input
     * @param rules : The rules to check
     * @param input : The input to check rules against
     * @tparam T
     * @return
     */
    fun <T> anyFalse(rules: List<(T) -> Boolean>, input: T): Boolean = rules.any { rule -> !rule(input) }


    fun <T> collect(rules: List<(String) -> ValidationResult>, text: String): List<ValidationResult> =
            rules.map { rule -> rule(text) }
                    .filter { result -> !result.success }
                    .toList()


    fun collect(rules: List<() -> ValidationResult>): List<ValidationResult> =
            rules.map { rule -> rule() }
                    .filter { result -> !result.success }
                    .toList()

}
