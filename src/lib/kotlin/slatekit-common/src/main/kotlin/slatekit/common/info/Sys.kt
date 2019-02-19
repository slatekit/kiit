package slatekit.common.info

data class Sys(
        val host: Host,
        val lang: Lang
) {

    companion object {
        fun build(): Sys = Sys(Host.local(), Lang.kotlin())
    }
}