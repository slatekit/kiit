package slatekit.tools


import slatekit.apis.ApiReg
import slatekit.apis.svcs.TokenAuth
import slatekit.common.ApiKey
import slatekit.common.Credentials
import slatekit.common.encrypt.Encryptor
import slatekit.core.app.AppRunner.build
import slatekit.core.cli.CliSettings
import slatekit.integration.apis.AppApi
import slatekit.integration.apis.CliApi
import slatekit.integration.apis.VersionApi
import slatekit.integration.common.AppEntContext
import slatekit.tools.docs.DocApi


/**
 * Entry point into the sample console application.
 */
fun main(args: Array<String>): Unit {
    // =========================================================================
    // 1: Build the application context
    // =========================================================================
    // NOTE: The app context contains the selected environment, logger,
    // conf, command line args database, encryptor, and many other components
    val ctx = AppEntContext.fromAppContext(build(
            args = args,
            enc = Encryptor("aksf2409bklja24b", "k3l4lkdfaoi97042")
    ))

    // =========================================================================
    // 4: Register the APIS
    // =========================================================================
    // Build up the shell services that handles all the command line features.
    // And setup the api container to hold all the apis.
    val keys = listOf(ApiKey("cli", "7031c47c268d41cfb57eb4b87e15d328", "dev,qa,ops,admin"))
    val creds = Credentials("1", "john doe", "jdoe@gmail.com", key = keys.first().key)
    val auth = TokenAuth(keys, ctx.enc)
    val shell = CliApi(creds, ctx, auth,
            CliSettings(enableLogging = true, enableOutput = true),
            listOf(
                    // Sample APIs for demo purposes
                    // Instances are created per request.
                    // The primary constructor must have either 0 parameters
                    // or a single paramter taking the same Context as ctx above )

                    // Example 1: without annotations ( pure kotlin objects )
                    ApiReg(DocApi::class, declaredOnly = true)
            )
    )

    // =========================================================================
    // 5: Run the CLI
    // =========================================================================
    shell.run()
}