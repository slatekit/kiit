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

package slatekit.core.cli


import slatekit.common.Files
import slatekit.common.newline
import slatekit.common.results.ResultFuncs.yes


class CliBatch(val cmd: CliCommand, val svc: CliService) {


    fun run(): CliCommand {

        val path = svc.folders.inputs + "/" + cmd.args.getString("file")
        val lines = Files.readLines(path)
        return if (lines.isEmpty()) {
            cmd
        }
        else {
            val results = svc.onCommandBatchExecute(lines, CliConstants.BatchModeContinueOnError)
            val messages = results.fold("", { s, res ->
                if (res.success) {
                    res.value?.let { cmd ->
                        s + "success: " + cmd.fullName() + " = " + (cmd.result?.value?.toString() ?: "") + newline
                    } ?: s
                }
                else {
                    res.value?.let { cmd ->
                        s + "failed: " + cmd.fullName() + " = " + (cmd.result?.msg ?: "") + newline
                    } ?: s
                }
            })
            if (svc.settings.enableOutput) {
                CliFuncs.log(svc.folders, messages)
            }
            val batchResult = cmd.copy(result = yes("batch output written to output directory"))
            return batchResult
        }
    }
}
