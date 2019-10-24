package slatekit.apis

/**
 * Annotation to describe a parameter to an api action.
 * NOT CURRENTLY USED - Will be in upcoming versions.
 *
 * @param path : optional path to identity the source of documentation
 * @param key  : optional key to identity area within document
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Documented(val path:String = "", val key:String = "")