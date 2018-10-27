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

package slatekit.common

/**
  * Named pattern as a value class
  * @param pattern
  */
data class Pattern(val pattern: String)

/**
  * Named regex pattern
  */
object Patterns {
    val email = Pattern("""(\w+)@([\w\.]+)""")
    val alpha = Pattern("""^[a-zA-Z]*$""")
    val alphaUpperCase = Pattern("""^[A-Z]*$""")
    val alphaLowerCase = Pattern("""^[a-z]*$""")
    val alphaNumeric = Pattern("""^[a-zA-Z0-9]*$""")
    val alphaNumericSpace = Pattern("""^[a-zA-Z0-9 ]*$""")
    val alphaNumericSpaceDash = Pattern("""^[a-zA-Z0-9 \-]*$""")
    val alphaNumericSpaceDashUnderscore = Pattern("""^[a-zA-Z0-9 \-_]*$""")
    val alphaNumericSpaceDashUnderscorePeriod = Pattern("""^[a-zA-Z0-9\. \-_]*$""")
    val numeric = Pattern("""^\-?[0-9]*\.?[0-9]*$""")
    val socialSecurity = Pattern("""\d{3}[-]?\d{2}[-]?\d{4}""")
    val url = Pattern("""^^(ht|f)tp(s?)\:\/\/[0-9a-zA-Z]([-.\w]*[0-9a-zA-Z])*(:(0-9)*)*(\/?)([a-zA-Z0-9\-\.\?\,\'\/\\\+&%\$#_=]*)?$""")
    val zipCodeUS = Pattern("""^\d{5}$""")
    val zipCodeUSWithFour = Pattern("""\d{5}[-]\d{4}""")
    val sipCodeUSWithFourOptional = Pattern("""\d{5}([-]\d{4})?""")
    val phoneUS = Pattern("""\d{3}[-]?\d{3}[-]?\d{4}""")
}
