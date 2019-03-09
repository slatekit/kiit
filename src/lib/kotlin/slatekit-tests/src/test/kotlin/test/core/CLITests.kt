/**
<slate_header>
author: Kishore Reddy
url: https://github.com/kishorereddy/scala-slate
copyright: 2015 Kishore Reddy
license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
desc: a scala micro-framework
usage: Please refer to license on github for more info.
</slate_header>
 */

package test.core

import org.junit.Assert
import org.junit.Test
import slatekit.cli.*
import slatekit.common.args.Args
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
            commands,reader,writer) {

        var testInit = false
        var testEnd = false
        var testExec = mutableListOf<String>()


        override fun init(): Try<Boolean> {
            testInit = true
            return super.init()
        }


        /**
         * executes a line of text by handing it off to the executor
         */
        override fun executeInternal(args:Args): Try<CliResponse<*>> {
            val text = args.line
            testExec.add(text)
            val req = CliRequest.build(Args.default(), text)
            return Success(CliResponse(req, true, StatusCodes.SUCCESS.code, mapOf(), text))
        }


        override fun end(status: Status): Try<Boolean> {
            testEnd = true
            return super.end(status)
        }
    }


    @Test
    fun can_ensure_flow() {
        val exit = { i: Unit -> "exit" }
        val cli = MyCLI(reader = exit)
        val result = cli.run()
        Assert.assertEquals(true, result.success)
        Assert.assertEquals("exit", cli.last())
    }


    @Test
    fun can_ensure_execution() {
        val exit = { i: Unit -> "exit" }
        val cli = MyCLI(reader = exit, commands = listOf("c1", "c2"))
        val result = cli.run()
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
        val result = cli.run()
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
        val result = cli.run()
        Assert.assertEquals(true, result.success)
        Assert.assertEquals("c2", cli.testExec[0])
        Assert.assertEquals(written[9], "c2")
    }


    @Test
    fun can_eval_about() {
        val cli = MyCLI()
        val result = cli.eval(Command.About.id)
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(StatusCodes.ABOUT.code, result.code)
    }


    @Test
    fun can_eval_help() {
        val cli = MyCLI()
        val result = cli.eval(Command.Help.id)
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(StatusCodes.HELP.code, result.code)
    }


    @Test
    fun can_eval_version() {
        val cli = MyCLI()
        val result = cli.eval(Command.Version.id)
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(StatusCodes.VERSION.code, result.code)
    }


    @Test
    fun can_eval_exit() {
        val cli = MyCLI()
        val result = cli.eval(Command.Exit.id)
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(StatusCodes.EXIT.code, result.code)
    }


    @Test
    fun can_eval_quit() {
        val cli = MyCLI()
        val result = cli.eval(Command.Quit.id)
        Assert.assertEquals(true, result.success)
        Assert.assertEquals(StatusCodes.EXIT.code, result.code)
    }
}
