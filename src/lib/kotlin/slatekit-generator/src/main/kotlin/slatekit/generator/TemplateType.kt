package slatekit.generator


/**
 * Represents the different type of templates available
 * ( for the different project types in Slate Kit )
 */
sealed class TemplateType {
    object App : TemplateType()
    object API : TemplateType()
    object CLI : TemplateType()
    object Job : TemplateType()
    object Lib : TemplateType()
}