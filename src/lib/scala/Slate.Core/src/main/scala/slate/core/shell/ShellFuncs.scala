/**
<slate_header>
  url: www.slatekit.com
  git: www.github.com/code-helix/slatekit
  org: www.codehelix.co
  author: Kishore Reddy
  copyright: 2016 CodeHelix Solutions Inc.
  license: refer to website and/or github
  about: A Scala utility library, tool-kit and server backend.
  mantra: Simplicity above all else
</slate_header>
  */

package slate.core.shell


import slate.common.args.ArgsFuncs
import slate.common.info.Folders
import slate.common.results.ResultCode
import slate.common.{Result, Strings, Files}
import slate.common.results.ResultFuncs._


object ShellFuncs {

  def log(folders:Folders, cmd:ShellCommand, content:String):Unit=
  {
    Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
  }


  def log(folders:Folders, content:String):Unit=
  {
    Files.writeFileForDateAsTimeStamp(folders.pathToLogs, content)
  }


  def checkForAssistance(cmd:ShellCommand): Result[Boolean] =
  {
    val words = cmd.args.raw
    val verbs = cmd.args.actionVerbs

    // Case 1: Exit ?
    if (ArgsFuncs.isExit(words, 0))
    {
      yesWithCode(ResultCode.EXIT, msg = Some("exit"), tag = Some(cmd.args.action))
    }
    // Case 2a: version ?
    else if (ArgsFuncs.isVersion(words, 0))
    {
      yesWithCode(ResultCode.HELP, msg = Some("version"), tag = Some(cmd.args.action))
    }
    // Case 2b: about ?
    else if (ArgsFuncs.isAbout(words, 0))
    {
      yesWithCode(ResultCode.HELP, msg = Some("about"), tag = Some(cmd.args.action))
    }
    // Case 3a: Help ?
    else if (ArgsFuncs.isHelp(words, 0))
    {
      yesWithCode(ResultCode.HELP, msg = Some("help"), tag = Some(cmd.args.action))
    }
    // Case 3b: Help on area ?
    else if (ArgsFuncs.isHelp(verbs, 1))
    {
      yesWithCode(ResultCode.HELP, msg = Some("area ?"), tag = Some(cmd.args.action))
    }
    // Case 3c: Help on api ?
    else if (ArgsFuncs.isHelp(verbs, 2))
    {
      yesWithCode(ResultCode.HELP, msg = Some("area.api ?"), tag = Some(cmd.args.action))
    }
    // Case 3d: Help on action ?
    else if (!Strings.isNullOrEmpty(cmd.args.action) &&
      ( ArgsFuncs.isHelp(cmd.args.positional, 0) ||
        ArgsFuncs.isHelp(verbs, 3))
    )
    {
      yesWithCode(ResultCode.HELP, msg = Some("area.api.action ?"), tag = Some(cmd.args.action))
    }
    else
      no()
  }
}
