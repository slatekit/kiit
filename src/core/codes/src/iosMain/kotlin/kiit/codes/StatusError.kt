package kiit.codes

/**
 * Swift-idiomatic alias for [StatusException].
 *
 * Swift error types are conventionally named `XxxError` (e.g. `URLError`, `CocoaError`).
 * `@ObjCName` prevents the auto-generated `KiitCodesStatusError` prefix in the ObjC header,
 * giving Swift consumers the clean name `StatusError`.
 *
 * Swift usage (without SKIE):
 * ```swift
 * do {
 *     try someKotlinApi()
 * } catch let e as StatusError {
 *     print(e.status.name)  // e.g. "UNAUTHORIZED"
 * }
 * ```
 *
 * With the SKIE plugin, functions annotated with `@Throws(StatusError::class)` are
 * automatically bridged to idiomatic Swift `throws` with no manual NSError casting.
 */
@OptIn(kotlin.experimental.ExperimentalObjCName::class)
@ObjCName("StatusError", swiftName = "StatusError")
class StatusError(
    status: Status,
    cause: Throwable? = null
) : StatusException(status, cause)
