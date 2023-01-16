/**
 *  <kiit_header>
 * url: www.kiit.dev
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github 
 *  </kiit_header>
 */

package kiit.common.checks

/**
  * Named pattern as a value class
  * @param pattern
  */
data class Pattern(val pattern: String)

/**
  * Named regex pattern
  */
object Patterns {
    @JvmField val email = Pattern("""([\w\.\-\_]+)@([\w\.\-\_]+)""")
    @JvmField val alpha = Pattern("""^[a-zA-Z]*$""")
    @JvmField val alphaUpperCase = Pattern("""^[A-Z]*$""")
    @JvmField val alphaLowerCase = Pattern("""^[a-z]*$""")
    @JvmField val alphaNumeric = Pattern("""^[a-zA-Z0-9]*$""")
    @JvmField val alphaNumericSpace = Pattern("""^[a-zA-Z0-9 ]*$""")
    @JvmField val alphaNumericSpaceDash = Pattern("""^[a-zA-Z0-9 \-]*$""")
    @JvmField val alphaNumericSpaceDashUnderscore = Pattern("""^[a-zA-Z0-9 \-_]*$""")
    @JvmField val alphaNumericSpaceDashUnderscorePeriod = Pattern("""^[a-zA-Z0-9\. \-_]*$""")
    @JvmField val numeric = Pattern("""^\-?[0-9]*\.?[0-9]*$""")
    @JvmField val digits = Pattern("""^\-?[0-9]*$""")
    @JvmField val socialSecurity = Pattern("""\d{3}[-]?\d{2}[-]?\d{4}""")
    @JvmField val url = Pattern("""^^(ht|f)tp(s?)\:\/\/[0-9a-zA-Z]([-.\w]*[0-9a-zA-Z])*(:(0-9)*)*(\/?)([a-zA-Z0-9\-\.\?\,\'\/\\\+&%\$#_=]*)?$""")
    @JvmField val zipCodeUS = Pattern("""^\d{5}$""")
    @JvmField val zipCodeUSWithFour = Pattern("""\d{5}[-]\d{4}""")
    @JvmField val zipCodeUSWithFourOptional = Pattern("""\d{5}([-]\d{4})?""")
    @JvmField val phoneUS = Pattern("""\d{3}[-]?\d{3}[-]?\d{4}""")
}
