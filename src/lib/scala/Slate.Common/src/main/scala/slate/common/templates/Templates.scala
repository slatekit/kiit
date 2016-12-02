/**
  * <slate_header>
  * author: Kishore Reddy
  * url: https://github.com/kishorereddy/scala-slate
  * copyright: 2016 Kishore Reddy
  * license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  * desc: a scala micro-framework
  * usage: Please refer to license on github for more info.
  * </slate_header>
  */
package slate.common.templates

import slate.common.{Random, DateTime, Result, ListMap}
import TemplateConstants._

object Templates{

  /**
    * Builds the templates object after parsing the given individual templates
    *
    * @param templates
    * @param subs
    * @return
    */
  def apply(templates  :Seq[Template],
            subs: Option[List[(String,(TemplatePart)=> String)]] = None):Templates = {

    // Each template
    val parsed = parse(templates)
    new Templates(Some(parsed), subs)
  }


  def subs(items:List[(String, (TemplatePart)=> String)]): Subs = {
    new Subs(Option(items))
  }


  def parse(templates  :Seq[Template]):Seq[Template] = {

    // Each template
    val parsed = templates.map( template => {

      // Parse the template into individual parts( plain text, variables )
      val result = new TemplateParser(template.content).parse()

      // Build the new template
      template.copy(parsed = true,
        valid = result.success,
        parts = Option(result.getOrElse(List[TemplatePart]())) )
    })
    parsed
  }
}


/**
  * Handles processing of text templates with variables/substitutions inside.
  *
  * @param templates
  * @param variables
  * @param setDefaults
  */
class Templates(val templates  :Option[Seq[Template]] = None,
                val variables  :Option[List[(String,(TemplatePart)=> String)]] = None,
                setDefaults:Boolean = true) {

  /**
    * The actual variables/substitutions that map to functions to substite the values
    */
  val subs = new Subs(variables, setDefaults)

  /**
    * parses the text template
    *
    * @param text
    * @return
    */
  def parse(text:String): Result[List[TemplatePart]] = {
    new TemplateParser(text).parse()
  }


  /**
    * parses the text and returns a parsed template with individual parts.
    *
    * @param text
    * @return
    */
  def parseTemplate(name:String, text:String): Template = {
    val result = parse(text)
    new Template(
      name    = name,
      content = text,
      parsed  = true,
      valid   = result.success,
      status  = result.msg,
      group   = None,
      path    = None,
      parts   = Option(result.getOrElse(List[TemplatePart]())))
  }


  /**
    * Processes the template with the variables supplied during creation
    *
    * @param text
    * @return
    */
  def resolve(text:String): Option[String] = {
    resolve(text, Some(subs))
  }


  /**
    * Processes the stored template associated with the name, with the variables supplied
    * at creation.
    *
    * @param name
    * @return
    */
  def resolveTemplate(name:String, substitutions:Option[Subs]): Option[String] = {

    val template = templates.fold[Option[Template]](None)( all =>
      all.filter( t => t.name == name).headOption)

    template.fold[Option[String]](None)( temp => {
      if ( temp.valid )
        resolveParts(temp.parts.get, substitutions.getOrElse(subs))
      else
        None
    })
  }


  /**
    * Processes the template with the variables supplied
    *
    * @param text
    * @return
    */
  def resolve(text:String, substitutions:Option[Subs]): Option[String] = {
    val result = parse(text)

    // Failed parsing ?
    if (!result.success) {
      return result.msg
    }

    // Get the individual substitution parts.
    val tokens = result.getOrElse(List[TemplatePart]())
    if (tokens == null || tokens.isEmpty) {
      return Some(text)
    }
    resolveParts(tokens, substitutions.getOrElse(subs))
  }


  private def resolveParts(tokens:List[TemplatePart],
                           substitutions:Subs) : Option[String] = {
    var finalText = ""
    for(token <- tokens){
      if(token.subType == TypeText){
        finalText += token.text
      }
      else if(token.subType == TypeSub){
        finalText += substitutions.lookup(token.text)
      }
    }
    Some(finalText)
  }
}
