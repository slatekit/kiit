package kiit.codes

/**
 * An exception that carries a structured [Status] instead of a plain message string.
 *
 * Throw this when a [Status] needs to cross a call boundary (e.g. from a service layer into
 * a framework that catches [Exception]). Callers inspect [status] to branch on the outcome:
 *
 * ```kotlin
 * throw StatusException(Codes.UNAUTHORIZED)
 *
 * try { ... } catch (e: StatusException) {
 *     when (e.status) {
 *         is Failed.Denied  -> // handle auth failure
 *         is Failed.Errored -> // handle business error
 *         else              -> // ...
 *     }
 * }
 * ```
 *
 * On iOS, prefer the [StatusError] subclass defined in `iosMain` — it carries an
 * `@ObjCName("StatusError")` annotation so Swift consumers see an idiomatic name.
 * On JS, prefer the [StatusError] subclass defined in `jsMain` — it is annotated with
 * `@JsExport` so TypeScript consumers see it in the generated `.d.ts` file.
 */
open class StatusException(
    val status: Status,
    cause: Throwable? = null,
) : Exception(status.message, cause)
