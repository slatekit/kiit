package slatekit.cli

sealed class Command(val id: String) {

    /* ktlint-disable */
    object Exit        : Command("exit")
    object Quit        : Command("quit")
    object Version     : Command("version")
    object About       : Command("about")
    object Help        : Command("help")
    object Retry       : Command("retry")
    object Last        : Command("last")
    data class Normal(val text:String)  : Command("normal")

    companion object {

        fun parse(name: String): Command = when (name.trim().toLowerCase()) {
            Exit   .id -> Exit
            Quit   .id -> Quit
            Version.id -> Version
            About  .id -> About
            Help   .id -> Help
            Last   .id -> Last
            Retry  .id -> Retry
            else       -> Normal(name)
        }
    }
    /* ktlint-enable */
}
