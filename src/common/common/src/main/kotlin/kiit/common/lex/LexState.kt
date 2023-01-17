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

package kiit.common.lex

/**
 * Low-level status for char/text parsing.
 * NOTE: Using var and basic while/for loops for
 * higher performance
 */
class LexState(val text: String) {

   // NOTE: Using vars here for low-level text parsing and higher performance
   var pos = 0
   var line = 0
   var charPos = 0
   var count = 0
   val END = text.length

  fun substring(start: Int, excludeLast: Boolean = false): String {
    val diff = if (excludeLast) 1 else 0
    val end = (start + (pos - start)) - diff
    val t = text.substring(start, end)

    val len = pos - start
    charPos = charPos + len
    return t
  }

  fun substringInclusive(start: Int): String {
    val end = (start + (pos - start))
    val t = text.substring(start, end)
    return t
  }

  fun incrementLine() {
    line = line + 1
    charPos = 1
  }

  fun incrementPos() {
    pos = pos + 1
  }
}
