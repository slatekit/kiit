package slatekit.apis

/**
 * Annotation to reference external documentation API specs
 * @param path : optional path to identity the source of documentation
 * @param key  : optional key to identity area within document
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Documented(val path:String = "", val key:String = "")