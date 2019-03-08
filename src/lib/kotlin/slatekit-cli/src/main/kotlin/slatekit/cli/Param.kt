package slatekit.cli


/**
 * System parameters are parameters supplied that begin with "$"
 * These indicate to the CLI and the CliRequest settings about
 * how to run the command itself instead of representing parameters
 * for the command to execute.
 */
sealed class SysParam(val id: String) {

    object Sample : SysParam("sample")
    object File : SysParam("file")
    object Format : SysParam("format")
    object Log : SysParam("log")
    object CodeGen : SysParam("codegen")
    data class Other(val name: String) : SysParam("other")


    companion object {

        fun parse(name: String): SysParam = when (name.trim().toLowerCase()) {
            Sample.id -> Sample
            File.id -> File
            Format.id -> Format
            Log.id -> Log
            CodeGen.id -> CodeGen
            else -> Other(name)
        }
    }
}