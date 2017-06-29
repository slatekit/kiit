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

package slatekit.apis.doc

data class DocSettings(
                          val maxLengthApi     :Int     = 0    ,
                          val maxLengthAction  :Int     = 0    ,
                          val maxLengthArg     :Int     = 0    ,
                          val enableDetailMode :Boolean = false
                        )