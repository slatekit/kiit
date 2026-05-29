package kiit.codes

/**
 * JS/TypeScript-idiomatic alias for [StatusException].
 *
 * JavaScript error types are conventionally named `XxxError`. This subclass is annotated
 * with `@JsExport` so it appears in the generated `.d.ts` as `StatusError`, while
 * [StatusException] remains internal to the Kotlin bundle and is not exported.
 *
 * TypeScript usage:
 * ```ts
 * import { StatusError, Codes } from '@kiit/codes'
 *
 * throw new StatusError(Codes.UNAUTHORIZED)
 *
 * try { ... } catch (e) {
 *     if (e instanceof StatusError) { console.log(e.status.name) }
 * }
 * ```
 */
@JsExport
@JsName("StatusError")
class StatusError(
    status: Status,
    cause: Throwable? = null,
) : StatusException(status, cause)
