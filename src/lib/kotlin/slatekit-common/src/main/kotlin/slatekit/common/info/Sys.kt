package slatekit.common.info

data class Sys(
        @JvmField val host: Host,
        @JvmField val lang: Lang
) {

    companion object {
        fun build(): Sys = Sys(Host.local(), Lang.kotlin())
    }
}