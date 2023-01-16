/**
 *  <kiit_header>
 * url: www.slatekit.com
 * git: www.github.com/slatekit/kiit
 * org: www.codehelix.co
 * author: Kishore Reddy
 * copyright: 2016 CodeHelix Solutions Inc.
 * license: refer to website and/or github
 *  </kiit_header>
 */

package kiit.app

import kiit.context.AppContext
import kiit.common.args.Args
import kiit.common.args.ArgsCheck
import kiit.common.args.ArgsCheck.isExit
import kiit.common.args.ArgsCheck.isVersion
import kiit.common.args.ArgsSchema
import kiit.common.conf.*
import kiit.common.conf.Confs.CONFIG_DEFAULT_SUFFIX
import kiit.common.crypto.Encryptor
import kiit.common.envs.Envs
import kiit.common.info.About
import kiit.common.io.Alias
import kiit.common.io.Uri
import kiit.common.log.Logs
import kiit.results.*
import kiit.results.builders.Outcomes
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

    fun context(cls:Class<*>, args: Args, envs: Envs, enc: Encryptor?, logs: Logs?, confSource:Alias = Alias.Jar): AppContext {
        val inputs = inputs(cls, args, envs, enc, confSource)
        return AppBuilder.context(cls, inputs, enc, logs)
    }

    private fun inputs(cls:Class<*>, args: Args, envs: Envs, enc: Encryptor?, alias:Alias = Alias.Jar): AppInputs {
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
            val confEnv = Config.of(cls, overrideConfSource, confBase, enc)

            AppInputs(source, args, Envs(allEnvs).select(env.name), confBase, confEnv)
        } ?: throw Exception("Unknown environment name : $envName supplied")
    }


    data class AppInputs(
        val loc : Uri,
        val args: Args,
        val envs: Envs,
        val confBase: Conf,
        val confEnv: Conf
    )
}
