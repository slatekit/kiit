/**
<slate_header>
  author: Kishore Reddy
  url: https://github.com/kishorereddy/scala-slate
  copyright: 2015 Kishore Reddy
  license: https://github.com/kishorereddy/scala-slate/blob/master/LICENSE.md
  desc: a scala micro-framework
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slate.examples


//<doc:import_required>
import slate.common.results.ResultSupportIn
import slate.common.{Model}
import scala.reflect.runtime.universe._
import slate.common.databases.DbBuilder
//</doc:import_required>

//<doc:import_examples>
import slate.common.Result
import slate.common.info.Host
import slate.core.cmds.Cmd
//</doc:import_examples>


class Example_Model extends Cmd("types") with ResultSupportIn
  {
    override protected def executeInternal(args: Any) : AnyRef =
    {
    //<doc:examples>
    // CASE 1: specify the name of the model e.g. "Resource"
    val model = new Model("Resource", "slate.ext.resources.Resource")

    // CASE 2. add a field for uniqueness / identity
    model.addId  (name = "id", autoIncrement = true, dataType = typeOf[Long])

    // CASE 3: add fields for text, bool, int, date etc.
    model.addText  (name = "key"     , isRequired = true, maxLength = 30)
         .addText  (name = "name"       , isRequired = true, maxLength = 30)
         .addText  (name = "category"   , isRequired = true, maxLength = 30)
         .addText  (name = "country"    , isRequired = true, maxLength = 30)
         .addText  (name = "region"     , isRequired = true, maxLength = 30)
         .addText  (name = "aggRegion"  , isRequired = true, maxLength = 30)
         .addText  (name = "aggCategory", isRequired = true, maxLength = 30)
         .addText  (name = "links"      , isRequired = true, maxLength = 30)
         .addText  (name = "owner"      , isRequired = true, maxLength = 30)
         .addText  (name = "status"     , isRequired = true, maxLength = 30)
         .addInt   (name = "recordState", isRequired = true)
         .addObject(name = "hostInfo"   , isRequired = true, dataType = typeOf[Host])
         .addDate  (name = "created"    , isRequired = true                )
         .addDate  (name = "updated"    , isRequired = true                )

    // CASE 4. check for any fields
    showResult( "any fields: " + model.anyFields() )

    // CASE 5. total number of fields
    showResult( "total fields: " + model.totalFields())

    // CASE 6. get the id field
    showResult( "id field: " + model.hasId() )

    // CASE 7. get the id field
    showResult( "id field: " + model.idField )

    // CASE 8. string representation of field
    showResult( "id field to string: " + model.idField().toString)

    // CASE 8. access the fields
    showResult ("access to fields : " + model.fields.size)

    // CASE 9. get name + full name of model
    showResult ("model name/fullName: " + model.name + ", " + model.fullName)

    // CASE 10. build up the table sql for this model
    showResult( "table sql : " + new DbBuilder().addTable(model))
    //</doc:examples>

    ok()
  }


  def showResult(content:String):Unit =
  {
    println()
    println( content )
  }
}
