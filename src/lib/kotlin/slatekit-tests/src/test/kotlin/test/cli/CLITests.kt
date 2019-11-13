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

package test.cli

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import slatekit.cli.*
import slatekit.common.args.Args
import slatekit.common.types.Content
import slatekit.common.info.Folders
import slatekit.common.info.Info
import slatekit.results.*


class CLITests {

    class MyCLI(version: String = "1.0.0",
                commands: List<String> = listOf(),
                reader: ((Unit) -> String?)? = null,
                writer: ((CliOutput) -> Unit)? = null) : CLI(
            CliSettings(),
            Info.none.copy(about = Info.none.about.copy(version = version)),
            Folders.default,
            null,
            commands,reader,writer,
            { item, type -> Content.csv(slatekit.meta.Serialization.csv().serialize(item) )}
            ) {

        var testInit = false
        var testEnd = false
        var testExec = mutableListOf<String>()


        override suspend fun init(): Try<Boolean> {
            testInit = true
            return super.init()
        }


        /**
         * executes a line of text by handing it off to the executor
         */
        override suspend fun executeRequest(request:CliRequest): Try<CliResponse<*>> {
            val args = request.args
            val text = args.line
            testExec.add(text)
            val req = CliRequest.build(Args.default(), text)
            return Success(CliResponse(req, true, Codes.SUCCESS.code, mapOf(), text))
        }


        override suspend fun end(status: Status): Try<Boolean> {
            testEnd = true
            return super.end(status)
        }
    }


    @Test
    fun can_ensure_flow() {
        val exit = { i: Unit -> "exit" }
        val cli = MyCLI(reader = exit)
        val result = runBlocking{ cli.run() }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals("exit", cli.last())
    }


    @Test
    fun can_ensure_execution() {
        val exit = { i: Unit -> "exit" }
        val cli = MyCLI(reader = exit, commands = listOf("c1", "c2"))
        val result = runBlocking { cli.run() }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals("c1", cli.testExec[0])
        Assert.assertEquals("c2", cli.testExec[1])
        Assert.assertEquals("exit", cli.last())
    }


    @Test
    fun can_ensure_retry() {
        var ndx = 0
        val commands = listOf("c1", "retry", "exit")
        val reader = { i: Unit ->
            val cmd = commands[ndx]
            ndx++
            cmd
        }
        val cli = MyCLI(reader = reader)
        val result = runBlocking { cli.run() }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals("c1", cli.testExec[0])
        Assert.assertEquals("c1", cli.testExec[1])
        Assert.assertEquals("exit", cli.last())
    }


    @Test
    fun can_ensure_last() {
        var ndx = 0
        val commands = listOf("c2", "last", "exit")
        val reader = { i: Unit ->
            val cmd = commands[ndx]
            ndx++
            cmd
        }
        val written = mutableListOf<String>()
        val writer = { i:CliOutput ->
            written.add(i.text ?: "")
            Unit
        }
        val cli = MyCLI(reader = reader, writer = writer)
        val result = runBlocking { cli.run() }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals("c2", cli.testExec[0])
        Assert.assertEquals(written[9], "c2")
    }


    @Test
    fun can_eval_about() {
        val cli = MyCLI()
        val result = runBlocking { cli.eval(Command.About.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.ABOUT.code, result.code)
    }


    @Test
    fun can_eval_help() {
        val cli = MyCLI()
        val result = runBlocking { cli.eval(Command.Help.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.HELP.code, result.code)
    }


    @Test
    fun can_eval_version() {
        val cli = MyCLI()
        val result = runBlocking { cli.eval(Command.Version.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.VERSION.code, result.code)
    }


    @Test
    fun can_eval_exit() {
        val cli = MyCLI()
        val result = runBlocking { cli.eval(Command.Exit.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.EXIT.code, result.code)
    }


    @Test
    fun can_eval_quit() {
        val cli = MyCLI()
        val result = runBlocking { cli.eval(Command.Quit.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.EXIT.code, result.code)
    }
}
