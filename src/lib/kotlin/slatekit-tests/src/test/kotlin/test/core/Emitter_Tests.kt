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
package test

import org.junit.Assert
import org.junit.Test
import slatekit.core.slatekit.core.common.Emitter


class Emitter_Tests {


    @Test
    fun can_init() {
        val emitter = Emitter<String>()
        Assert.assertTrue(emitter.listeners.isEmpty())
    }
}
