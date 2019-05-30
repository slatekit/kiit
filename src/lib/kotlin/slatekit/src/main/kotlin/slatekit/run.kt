package slatekit


import kotlinx.coroutines.runBlocking
import slatekit.app.AppRunner
import slatekit.common.CommonContext
import slatekit.docs.DocService
import slatekit.generator.*
import slatekit.integration.common.AppEntContext
import slatekit.providers.logs.logback.LogbackLogs


/**
 * Entry point into the sample console application with support for:
 *
 * 1. environment ( local, dev, qat, pro )
 * 2. command line args
 * 3. argument validation
 * 4. about / help / version display
 * 5. diagnostics ( on startup and end )
 * 6. logging ( console + logback )
 * 7. life-cycle events ( init, exec, end )
 *
 * java -jar ${app.name}.jar ?
 * java -jar ${app.name}.jar --about
 * java -jar ${app.name}.jar --version
 * java -jar ${app.name}.jar -env=dev
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "jars"
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "conf"
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "file://./conf-sample-batch"
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "file://./conf-sample-shell"
 * java -jar ${app.name}.jar -env=dev -log.level=info -conf.dir = "file://./conf-sample-server"
 */
fun main(args: Array<String>) {
    //app(args)
    srv(args)
    //cli(args)
    //doc(args)
}


fun cli(args:Array<String>) {
    runBlocking {
        AppRunner.run(
                rawArgs = args,
                about = SlateKit.about,
                schema = SlateKit.schema,
                enc = SlateKit.encryptor,
                logs = LogbackLogs(),
                builder = { ctx -> SlateKit(AppEntContext.fromContext(ctx)) }
        )
    }
}


fun app(args:Array<String>) {
    gen(args, "app8", "Test slate kit", "myapp.app", "codehelix", "/Users/kishore.reddy/dev/tests/slatekit", Templates.app())
}


fun lib(args:Array<String>) {
    gen(args,"lib1", "Test slate kit", "myapp.lib", "codehelix", "/Users/kishore.reddy/dev/tests/slatekit", Templates.lib())
}


fun srv(args:Array<String>) {
    gen(args,"srv1", "Test slate kit", "myapp.srv", "codehelix", "/Users/kishore.reddy/dev/tests/slatekit", Templates.srv())
}


fun gen(args:Array<String>, name:String, desc:String, packageName:String, company:String, dest:String, template: Template) {
    val svc = GeneratorService(CommonContext.simple(""), SlateKit::class.java)
    val ctx = GeneratorContext(name, desc, packageName, company, dest, CredentialMode.EnvVars)
    svc.generate(ctx, template)
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