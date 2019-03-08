package slatekit.cli

sealed class Command(val id: String) {

    object Exit        : Command("exit")
    object Quit        : Command("quit")
    object Version     : Command("version")
    object About       : Command("about")
    object Help        : Command("help")
    data class Normal(val text:String)  : Command("normal")

    companion object {

        fun parse(name: String): Command = when (name.trim().toLowerCase()) {
            Exit   .id -> Exit
            Quit   .id -> Quit
            Version.id -> Version
            About  .id -> About
            Help   .id -> Help
            else       -> Normal(name)
        }
    }
}