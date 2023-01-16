package kiit.common

/**
 * Use to return the raw underlying provider for any abstracted infrastructure component.
 * E.g. For the File Component that abstracts Cloud File storage, this would be used
 * in the S3 implementation to return the AWSSDK for S3.
 * This serves as an "Exit Hatch" when the abstraction not enough and we need
 * to access the underlying raw implementation
 */
interface Provider {
    val provider:Any

    @Suppress("UNCHECKED_CAST")
    fun <T> providerAs(): T = provider as T
}