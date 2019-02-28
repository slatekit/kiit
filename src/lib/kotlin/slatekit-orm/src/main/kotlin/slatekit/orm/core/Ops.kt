package slatekit.orm.core

enum class Op(val text: String) {
    Eq("="),
    Neq("<>"),
    Gt("="),
    Gte(">="),
    Lt("<"),
    Lte("<="),
}
