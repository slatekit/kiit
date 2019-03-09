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
import slatekit.apis.core.Annotated
import slatekit.apis.core.Api
import slatekit.apis.svcs.Authenticator
import slatekit.cli.*
import slatekit.common.info.ApiKey
import slatekit.common.info.Credentials
import slatekit.common.args.Args
import slatekit.common.conf.Config
import slatekit.common.info.Folders
import slatekit.common.info.Info
import slatekit.integration.apis.InfoApi
import slatekit.integration.apis.CliApi
import slatekit.integration.apis.VersionApi
import slatekit.integration.common.AppEntContext
import slatekit.results.*


class CLITests {

    class MyCLI(version: String = "1.0.0",
                commands: List<String> = listOf(),
                reader: ((Unit) -> String?)? = null) : CLI(
            Info.none.copy(about = Info.none.about.copy(version = version)),
            Folders.default,
            CliSettings(),
            commands, reader) {

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
        override fun execute(line: String): Try<CliResponse<*>> {
            testExec.add(line)
            val req = CliRequest.build(Args.default(), line)
            return Success(CliResponse(req, true, StatusCodes.SUCCESS.code, mapOf(), line))
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
