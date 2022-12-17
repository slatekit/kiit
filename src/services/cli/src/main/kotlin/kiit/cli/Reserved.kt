package kiit.cli

sealed class Reserved(val id: String) {

    /* ktlint-disable */
    object Exit        : Reserved("exit")
    object Quit        : Reserved("quit")
    object Version     : Reserved("version")
    object About       : Reserved("about")
    object Help        : Reserved("help")
    object Retry       : Reserved("retry")
    object Last        : Reserved("last")
    data class Normal(val text:String)  : Reserved("normal")

    companion object {

        fun parse(name: String): Reserved = when (name.trim().toLowerCase()) {
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
