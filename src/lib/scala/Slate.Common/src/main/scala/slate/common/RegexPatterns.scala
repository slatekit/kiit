/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.common

/**
  * Named pattern as a value class
  * @param pattern
  */
class Pattern(val pattern:String) extends AnyVal


/**
  * Named regex pattern
  */
object RegexPatterns {
  val email                                 = new Pattern("""(\w+)@([\w\.]+)""")
  val alpha                                 = new Pattern("""^[a-zA-Z]*$""")
  val alphaUpperCase                        = new Pattern("""^[A-Z]*$""")
  val alphaLowerCase                        = new Pattern("""^[a-z]*$""")
  val alphaNumeric                          = new Pattern("""^[a-zA-Z0-9]*$""")
  val alphaNumericSpace                     = new Pattern("""^[a-zA-Z0-9 ]*$""")
  val alphaNumericSpaceDash                 = new Pattern("""^[a-zA-Z0-9 \-]*$""")
  val alphaNumericSpaceDashUnderscore       = new Pattern("""^[a-zA-Z0-9 \-_]*$""")
  val alphaNumericSpaceDashUnderscorePeriod = new Pattern("""^[a-zA-Z0-9\. \-_]*$""")
  val numeric                               = new Pattern("""^\-?[0-9]*\.?[0-9]*$""")
  val socialSecurity                        = new Pattern("""\d{3}[-]?\d{2}[-]?\d{4}""")
  val url                                   = new Pattern("""^^(ht|f)tp(s?)\:\/\/[0-9a-zA-Z]([-.\w]*[0-9a-zA-Z])*(:(0-9)*)*(\/?)([a-zA-Z0-9\-\.\?\,\'\/\\\+&%\$#_=]*)?$""")
  val zipCodeUS                             = new Pattern("""^\d{5}$""")
  val zipCodeUSWithFour                     = new Pattern("""\d{5}[-]\d{4}""")
  val sipCodeUSWithFourOptional             = new Pattern("""\d{5}([-]\d{4})?""")
  val phoneUS                               = new Pattern("""\d{3}[-]?\d{3}[-]?\d{4}""")
}

