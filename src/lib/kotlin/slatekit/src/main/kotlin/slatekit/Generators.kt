package slatekit

import kotlinx.coroutines.runBlocking
import slatekit.app.AppRunner
import slatekit.common.CommonContext
import slatekit.docs.DocService
import slatekit.generator.GeneratorApi
import slatekit.generator.GeneratorService
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs
import slatekit.samples.app.App

/**
 * Runs slatekit cli in interactive mode
 */
fun slatekitCLI(args:Array<String>) {
    runBlocking {
        AppRunner.run(
                rawArgs = args,
                about = SlateKit.about,
                schema = SlateKit.schema,
                enc = SlateKit.encryptor,
                logs = LogbackLogs(),
                builder = { ctx -> SlateKit(AppEntContext.fromContext(ctx), interactive = true) }
        )
    }
}


/**
 * Runs slatekit cli in execution mode ( for code generation from command line )
 * This is also used by the code generation
 */
fun slatekitApp(args:Array<String>) {
    runBlocking {
        AppRunner.run(
                rawArgs = args,
                about = SlateKit.about,
                schema = SlateKit.schema,
                enc = SlateKit.encryptor,
                logs = LogbackLogs(),
                builder = { ctx -> SlateKit(AppEntContext.fromContext(ctx), interactive = false) }
        )
    }
}


/**
 * Runs the sample app
 * This is also used by the code generation
 */
fun sampleApp(args:Array<String>) {
    runBlocking {
        AppRunner.run(
                rawArgs = args,
                about = App.about,
                schema = App.schema,
                enc = App.encryptor,
                logs = LogbackLogs(),
                builder = { ctx -> App(AppEntContext.fromContext(ctx)) }
        )
    }
}


/**
 * Runs the sample cli.
 * This is also used by the code generation
 */
fun sampleCli(args:Array<String>) {
    runBlocking {
        AppRunner.run(
                rawArgs = args,
                about = slatekit.samples.cli.App.about,
                schema = slatekit.samples.cli.App.schema,
                enc = slatekit.samples.cli.App.encryptor,
                logs = LogbackLogs(),
                builder = { ctx -> slatekit.samples.cli.App(AppEntContext.fromContext(ctx)) }
        )
    }
}


/**
 * Runs the sample server.
 * This is also used by the code generation
 */
fun sampleSrv(args:Array<String>) {
    runBlocking {
        AppRunner.run(
                rawArgs = args,
                about = slatekit.samples.srv.App.about,
                schema = slatekit.samples.srv.App.schema,
                enc = slatekit.samples.srv.App.encryptor,
                logs = LogbackLogs(),
                builder = { ctx -> slatekit.samples.srv.App(AppEntContext.fromContext(ctx)) }
        )
    }
}


fun genApp(args:Array<String>, dest:String, name:String) {
    gen(args) {
        it.app(name, "myapp.app")
    }
}


fun genLib(args:Array<String>, dest:String, name:String) {
    gen(args) {
        it.lib(name, "myapp.libs", "codehelix", dest )
    }
}


fun genCli(args:Array<String>, dest:String, name:String) {
    gen(args) {
        it.cli(name, "myapp.cli", "codehelix", dest )
    }
}


fun genSrv(args:Array<String>, dest:String, name:String) {
    gen(args) {
        it.api(name, "myapp.apis", "codehelix", dest )
    }
}


fun gen(args:Array<String>, op: (GeneratorApi) -> Unit ) {
    val ctx = CommonContext.simple("slatekit.generator")
    val svc = GeneratorService(ctx, SlateKit::class.java)
    val api = GeneratorApi(ctx, svc)
    op(api)
}


fun doc(args:Array<String>){

    val root = "/Users/kishorereddy/git/slatekit"
    val svc = DocService(
            "$root/slatekit",
            "$root/slatekit-site/src/hugo/slatekit-v3/content/core",
            "scripts/doc/doc_template_cloud.md")
    svc.processItems(listOf(
            "AWS-S3",
            "AWS-SQS"
    ))
}




fun temp(args:Array<String>){
    val dir = "/Users/kishore.reddy/dev/tmp/slatekit/slatekit/src/lib/kotlin/slatekit/src/main/resources/templates"
    //val template = Templates.load(dir, "slatekit/app")
    //sampleApp(args)
    //sampleCli(args)
    //sampleSrv(args)
    val dest = "/Users/kishore.reddy/dev/tmp/slatekit/slatekit/test/generator/app/myapp1"
    genApp(args, dest, "MyApp1")
    //genCli(args)
    //genSrv(args)
}


