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

package slatekit.app

import slatekit.context.AppContext
import slatekit.common.args.Args
import slatekit.common.args.ArgsCheck
import slatekit.common.args.ArgsCheck.isExit
import slatekit.common.args.ArgsCheck.isVersion
import slatekit.common.args.ArgsSchema
import slatekit.common.conf.*
import slatekit.common.conf.Confs.CONFIG_DEFAULT_SUFFIX
import slatekit.common.crypto.Encryptor
import slatekit.common.envs.Envs
import slatekit.common.info.About
import slatekit.common.io.Alias
import slatekit.common.io.Uri
import slatekit.common.log.Logs
import slatekit.common.log.Prints
import slatekit.results.*
import slatekit.results.builders.Outcomes
import java.io.File

object AppUtils {

    /**
     * Checks the command for either an instructions about app or for exiting:
     * 1. exit
     * 2. version
     * 3. about
     *
     * @param raw
     * @return
     */
    fun isMetaCommand(raw: List<String>): Outcome<String> {

        return when {
            // Case 1: Exit ?
            isExit(raw, 0) -> Outcomes.success("exit", Codes.EXIT)

            // Case 2a: version ?
            isVersion(raw, 0) -> Outcomes.success("version", Codes.VERSION)

            // Case 2b: about ?
            ArgsCheck.isAbout(raw, 0) -> Outcomes.success("about", Codes.ABOUT)

            // Case 3a: Help ?
            ArgsCheck.isHelp(raw, 0) -> Outcomes.success("help", Codes.HELP)

            else -> Failure(Err.of("other"))
        }
    }

    fun context(cls:Class<*>, args: Args, envs: Envs, about: About, schema: ArgsSchema, enc: Encryptor?, logs: Logs?, confSource:Alias = Alias.Jar): AppContext {
        val inputs = inputs(cls, args, envs, about, schema, enc, logs, confSource)
        return AppBuilder.context(cls, inputs, enc, logs)
    }

    private fun inputs(cls:Class<*>, args: Args, envs: Envs, about: About, schema: ArgsSchema, enc: Encryptor?, logs: Logs?, alias:Alias = Alias.Jar): AppInputs {
        // We need to determine where the "env.conf" is loaded from.
        // The location is defaulted to load from jars but can be explicitly supplied in args
        // or specified in the "conf.dirs" config setting in the env.conf file
        // 1. user dir: conf.dir=user:/app1  -> ~/app1
        // 2. curr dir: conf.dir=curr:/app1  -> ./app1
        // 3. path dir: conf.dir=path:/app1  -> /app1/
        // 4. temp dir: conf.dir=temp:/app1  -> $TMPDIR/app1
        // 5. conf dir: conf.dir=conf:/app1  -> ./conf
        // 6. jars dir: conf.dir=jars:/app1  -> app.jar/resources
        val source = AppBuilder.dir(args, alias)
        Prints.keys("appinputs.inputs", listOf("alias" to alias.value, "source.root" to source.root.value, "source.path" to source.path))
        val props = AppBuilder.conf(cls, args, Confs.CONFIG_DEFAULT_PROPERTIES, alias)
        val confBase = Config(cls, source, props, enc)

        // 2. The environment can be selected in the following order:
        // - command line ( via "-env=dev"   )
        // - env.conf ( via env.name = dev )
        // getEnv will first look for selected environment from args, then in config.
        val values = AppValues(args, confBase)
        val envSelected = values.env()

        // 2. Validate the environment
        // Get all
        val allEnvs = envs.all
        val envCheck = Envs(allEnvs).validate(envSelected)
        val envName = envSelected.name

        return envCheck?.let { env ->
            // 4. We now have the environment to use ( e.g. "dev" )
            // Now load the final environment specific override
            // for directory reference provide: "file://./conf/"
            val overrideConfName = "env.${env.name}" + CONFIG_DEFAULT_SUFFIX
            val overrideConfSource = source.combine(overrideConfName)
            Prints.keys("appinputs.inputs.override", listOf("name" to overrideConfName, "path" to overrideConfSource.path))
            val confEnv = Config.of(cls, overrideConfSource, confBase, enc)

            AppInputs(source, args, Envs(allEnvs).select(env.name), confBase, confEnv)
        } ?: throw Exception("Unknown environment name : $envName supplied")
    }

    fun resourceExists(uri:Uri, path: String): Boolean {
        val actual = uri.combine(path)
        val res = File(actual.full)
        return res.exists()
    }


    data class AppInputs(
        val loc : Uri,
        val args: Args,
        val envs: Envs,
        val confBase: Conf,
        val confEnv: Conf
    )
}
