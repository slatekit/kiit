/**
<slate_header>
  author: Kishore Reddy
  url: www.github.com/code-helix/slatekit
  copyright: 2015 Kishore Reddy
  license: www.github.com/code-helix/slatekit/blob/master/LICENSE.md
  desc: A tool-kit, utility library and server-backend
  usage: Please refer to license on github for more info.
</slate_header>
  */


package slatekit.examples

//<doc:import_required>
import slatekit.meta.models.*
//</doc:import_required>

//<doc:import_examples>

import slatekit.common.DateTime
import slatekit.results.Try
import slatekit.results.Success
import slatekit.common.auth.User
import slatekit.common.info.Host

import slatekit.orm.databases.vendors.MySqlBuilder

//</doc:import_examples>



class Example_Model : Command("model") {

  override fun execute(request: CommandRequest) : Try<Any>
  {
    //<doc:examples>
    // ABOUT:
    // The Model component allows you to easily build up a model with fields
    // This allows you to have a schema / structure representing a data model.
    // With the structure in place, it helps facilitate code-generation.
    // Also, the ORM / Mapper of Slate Kit internally builds a model for each
    // Kotlin class that is mapped by the ORM.

    // CASE 1: specify the api of the model e.g. "Resource"
    // NOTE: The model is IMMUTABLE ( any additions of fields will result in a new model )
    var model = Model("Resource", "slate.ext.resources.Resource")

    // CASE 2. add a field for uniqueness / identity
    val schema2 = Model.of<Long, Example_Mapper.Movie>(Long::class, Example_Mapper.Movie::class) {
          field(Example_Mapper.Movie::id       , category = FieldCategory.Id)
          field(Example_Mapper.Movie::title    , desc = "Title of movie", min = 5, max = 30)
          field(Example_Mapper.Movie::category , desc = "Category (action|drama)", min = 1, max = 20)
          field(Example_Mapper.Movie::playing  , desc = "Whether its playing now")
          field(Example_Mapper.Movie::rating   , desc = "Rating from users")
          field(Example_Mapper.Movie::released , desc = "Date of release")
          field(Example_Mapper.Movie::createdAt, desc = "Who created record")
          field(Example_Mapper.Movie::createdBy, desc = "When record was created")
          field(Example_Mapper.Movie::updatedAt, desc = "Who updated record")
          field(Example_Mapper.Movie::updatedBy, desc = "When record was updated")
    }

    // CASE 3: add fields for text, bool, int, date etc.
    model = Model("Resource", "", dataType = User::class, desc = "", tableName = "users", modelFields = listOf(
            ModelField(name = "key"        , isRequired = true, maxLength = 30, dataCls = String::class),
            ModelField(name = "api"        , isRequired = true, maxLength = 30, dataCls = String::class),
            ModelField(name = "recordState", isRequired = true, dataCls = Int::class),
            ModelField(name = "hostInfo"   , isRequired = true, dataCls = Host::class),
            ModelField(name = "created"    , isRequired = true, dataCls = DateTime::class),
            ModelField(name = "updated"    , isRequired = true, dataCls = DateTime::class)
    ))

    // CASE 4. check for any fields
    showResult( "any fields: " + model.any )

    // CASE 5. total number of fields
    showResult( "total fields: " + model.size )

    // CASE 6. get the id field
    showResult( "id field: " + model.hasId )

    // CASE 7. get the id field
    showResult( "id field: " + model.idField )

    // CASE 8. string representation of field
    showResult( "id field to string: " + model.idField.toString())

    // CASE 8. access the fields
    showResult ("access to fields : " + model.fields.size)

    // CASE 9. get api + full api of model
    showResult ("model api/fullName: " + model.name + ", " + model.fullName)

    // CASE 10. build up the table sql for this model
    showResult( "table sql : " + MySqlBuilder(null).createTable(model))
    //</doc:examples>

    return Success("")
  }


  fun showResult(content:String):Unit
  {
    println()
    println( content )
  }
}
