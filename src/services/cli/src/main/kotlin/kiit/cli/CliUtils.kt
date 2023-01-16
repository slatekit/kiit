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

package kiit.cli

import kiit.common.args.Args
import kiit.common.info.Folders
import kiit.common.io.Files
import kiit.common.types.*
import kiit.results.*

object CliUtils {

    fun log(folders: Folders, req: CliRequest, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }

    fun log(folders: Folders, content: String) {
        Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
    }

    fun convert(line: String, prefix: String, separator: String): Try<CliRequest> {
        // 1st step, parse the command line into arguments
        val argsResult = Args.parse(line, prefix, separator, true)
        return Success(CliRequest.build((argsResult as Success<Args>).value, line))
    }

    fun convert(args: Args): CliRequest {
        // 1st step, parse the command line into arguments
        return CliRequest.build(args, args.line)
    }
}
