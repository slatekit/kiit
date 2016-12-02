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

object RegexPatterns {
  val email                                 = """(\w+)@([\w\.]+)"""
  val alpha                                 = """^[a-zA-Z]*$"""
  val alphaUpperCase                        = """^[A-Z]*$"""
  val alphaLowerCase                        = """^[a-z]*$"""
  val alphaNumeric                          = """^[a-zA-Z0-9]*$"""
  val alphaNumericSpace                     = """^[a-zA-Z0-9 ]*$"""
  val alphaNumericSpaceDash                 = """^[a-zA-Z0-9 \-]*$"""
  val alphaNumericSpaceDashUnderscore       = """^[a-zA-Z0-9 \-_]*$"""
  val alphaNumericSpaceDashUnderscorePeriod = """^[a-zA-Z0-9\. \-_]*$"""
  val numeric                               = """^\-?[0-9]*\.?[0-9]*$"""
  val socialSecurity                        = """\d{3}[-]?\d{2}[-]?\d{4}"""
  val url                                   = """^^(ht|f)tp(s?)\:\/\/[0-9a-zA-Z]([-.\w]*[0-9a-zA-Z])*(:(0-9)*)*(\/?)([a-zA-Z0-9\-\.\?\,\'\/\\\+&%\$#_=]*)?$"""
  val zipCodeUS                             = """^\d{5}$"""
  val zipCodeUSWithFour                     = """\d{5}[-]\d{4}"""
  val sipCodeUSWithFourOptional             = """\d{5}([-]\d{4})?"""
  val phoneUS                               = """\d{3}[-]?\d{3}[-]?\d{4}"""
}

