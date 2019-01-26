package slatekit.results



inline fun <T1, T2, E> Result<T1, E>.map(f: (T1) -> T2): Result<T2, E> =
        when (this) {
            is Success -> Success(f(this.data), this.code, this.msg)
            is Failure -> this
        }

inline fun <T1, T2, E> Result<T1, E>.flatMap(f: (T1) -> Result<T2, E>): Result<T2, E> =
        when (this) {
            is Success -> f(this.data)
            is Failure -> this
        }

inline fun <T1, T2, E> Result<T1, E>.fold(onSuccess: (T1) -> T2, onError: (E) -> T2): T2 =
        when (this) {
            is Success -> onSuccess(this.data)
            is Failure -> onError(this.err)
        }

inline fun <T, E> Result<T, E>.getOrElse(f: () -> T): T =
        when (this) {
            is Success -> this.data
            is Failure -> f()
        }

inline fun <T, E> Result<T, E>.exists(f: (T) -> Boolean): Boolean =
        when (this) {
            is Success -> f(this.data)
            is Failure -> false
        }

inline fun <T, E> Result<T, E>.onSuccess(f: (T) -> Unit) =
        when (this) {
            is Success -> f(this.data)
            is Failure -> {
            }
        }

inline fun <T, E> Result<T, E>.onFailure(f: (E) -> Unit) =
        when (this) {
            is Success -> {
            }
            is Failure -> {
                f(this.err)
            }
        }

inline fun <T1, T2, E1, E2> Result<T1, E1>.transform(
        onSuccess: (T1) -> Result<T2, E2>,
        onFailure: (E1) -> Result<T2, E2>
): Result<T2, E2> =
        when (this) {
            is Success -> onSuccess(this.data)
            is Failure -> onFailure(this.err)
        }

@Suppress("UNCHECKED_CAST")
fun <T, E> Result<T, E>.toResultEx(): Result<T, Exception> =
        when (this) {
            is Success -> this
            is Failure -> {
                when (this.err) {
                    is Exception -> this as Result<T, Exception>
                    else -> Failure(Exception(this.msg), this.code, this.msg)
                }
            }
        }