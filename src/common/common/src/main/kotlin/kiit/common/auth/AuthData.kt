package kiit.common.auth

data class AuthData(
    val full:String,
    val id: TokenIdentity,
    val ac: TokenAccess,
    val rf: TokenRefresh
) {
    companion object {
        val empty = AuthData(
            full = "",
            id = TokenIdentity("", mapOf()),
            ac = TokenAccess("", mapOf()),
            rf = TokenRefresh(""),
        )
    }
}