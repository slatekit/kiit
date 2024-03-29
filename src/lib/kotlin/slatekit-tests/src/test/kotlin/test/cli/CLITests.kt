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

package test.cli

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import kiit.cli.*
import kiit.common.args.Args
import kiit.common.types.Content
import kiit.common.info.Folders
import kiit.common.info.Info
import kiit.common.types.Contents
import kiit.results.*
import kiit.serialization.Serialization


class CLITests {

    class MyCLI(version: String = "1.0.0",
                commands: List<String> = listOf(),
                reader: ((Unit) -> String?)? = null,
                writer: ((CliOutput) -> Unit)? = null) : CLI(
            CliSettings(),
            Info.none.copy(build = Info.none.build.copy(version = version)),
            Folders.default,
            null,
            commands,reader,writer,
            { item, type -> Contents.csv(Serialization.csv().serialize(item) )}
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
            val req = CliRequest.build(Args.empty(), text)
            return Success(CliResponse(req, true, Codes.SUCCESS.name, Status.toType(Codes.SUCCESS), Codes.SUCCESS.code, mapOf(), text))
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
        val result = runBlocking { cli.eval(Reserved.About.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.ABOUT.code, result.code)
    }


    @Test
    fun can_eval_help() {
        val cli = MyCLI()
        val result = runBlocking { cli.eval(Reserved.Help.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.HELP.code, result.code)
    }


    @Test
    fun can_eval_version() {
        val cli = MyCLI()
        val result = runBlocking { cli.eval(Reserved.Version.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.VERSION.code, result.code)
    }


    @Test
    fun can_eval_exit() {
        val cli = MyCLI()
        val result = runBlocking { cli.eval(Reserved.Exit.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.EXIT.code, result.code)
    }


    @Test
    fun can_eval_quit() {
        val cli = MyCLI()
        val result = runBlocking { cli.eval(Reserved.Quit.id) }
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(Codes.EXIT.code, result.code)
    }
}
