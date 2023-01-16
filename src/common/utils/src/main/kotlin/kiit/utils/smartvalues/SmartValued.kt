package kiit.utils.smartvalues


/**
 * Provides a base class to have an easy reference to the underlying
 * metadata ( which should be a singleton on the companion object )
 * This allows lookup of info at runtime for any instance.
 * This is particularly useful in some scenarios for slatekit APIs
 * However, this is fully optional otherwise
 *
 * NOTE: Derived classes implementing SmartValue due NOT need to extend
 * this interface, unless you want easy access to the metadata for each
 * instance of the SmartValue. ( See [Email] for reference )
 */
open class SmartValued(override val meta: SmartMetadata) : SmartValue
