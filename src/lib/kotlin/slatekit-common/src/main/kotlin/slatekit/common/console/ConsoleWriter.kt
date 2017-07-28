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

package slatekit.common.console

import slatekit.common.IO


/**
 * Semantic console writer to print text in different colors and in Uppercase/lowercase for
 * things like title, subtitle, url etc.
 */
class ConsoleWriter(private val _settings: ConsoleSettings = Console.defaults()) : ConsoleWrites {
    override val settings: ConsoleSettings get() = _settings


    /**
     * IO abstraction for system.println.
     * Assists with testing and making code a bit more "purely functional"
     * This is a simple, custom alternative to the IO Monad.
     * Refer to IO.scala for details.
     */
    override val _io: IO<Any, Unit> = slatekit.common.Print
}