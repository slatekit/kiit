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

import slatekit.common.*
import slatekit.common.io.Files
import slatekit.results.Failure
import slatekit.results.Success
import java.io.File
import java.io.FileNotFoundException

class CliBatch(val cmd: CliCommand, val svc: CliService) {

    fun run(): CliCommand {

        val fileName = cmd.args.getSysString("file")
        val filePath = File(svc.folders.pathToInputs, fileName)
        if (!filePath.exists()) {
            throw FileNotFoundException(filePath.absolutePath)
        }
        val lines = Files.readLines(filePath.absolutePath)
        return if (lines.isEmpty()) {
            cmd
        } else {
            val results = svc.onCommandBatchExecute(lines, CliConstants.BatchModeContinueOnError)
            val messages = results.fold("", { s, res ->
                when (res) {
                    is Success -> {
                        s + "success: " + res.value.fullName() + " = " + (cmd.result?.value?.toString() ?: "") + newline
                    }
                    is Failure -> {
                        s + "failed: " + cmd.fullName() + " = " + (cmd.result?.msg ?: "") + newline
                    }
                }
            })
            if (svc.settings.enableOutput) {
                CliFuncs.log(svc.folders, messages)
            }
            val batchResult = cmd.copy(result = Success("batch output written to output directory").toResponse())
            return batchResult
        }
    }
}
