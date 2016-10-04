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

package slate.tools.codegen

import java.io.File

import slate.common._
import slate.common.databases.{DbBuilder, DbMeta, DbLookup}
import slate.common.results.ResultSupportIn
import slate.core.apis.{ApiAction, Api}
import slate.core.common.svcs.ApiWithSupport
import slate.entities.core.EntityMapper
import slate.entities.models.ModelSerializer
import scala.reflect.runtime.universe.{Type,typeOf}

@Api(area = "slate", name = "models", desc = "api info about the application and host", roles= "?", auth = "key-roles", verb = "post", protocol="*")
class CodeGenApi extends ApiWithSupport with ResultSupportIn
{

  private var _rootFolder      = ""
  private def _sqlFolder       = { getOutputFolder() + File.separator + "sql"       }
  private def _defFolder       = { getOutputFolder() + File.separator + "def"       }
  private def _serverFolder    = { getOutputFolder() + File.separator + "server"    }
  private def _jsFolder        = { getOutputFolder() + File.separator + "js"        }
  private def _templatesFolder = { getOutputFolder() + File.separator + "templates" }


  @ApiAction(name = "", desc= "sets the root folder", roles= "@parent", verb = "@parent", protocol = "@parent")
  def setRootFolder(folder:String):Unit = {
    _rootFolder = folder
  }



  @ApiAction(name = "", desc= "creates a model from the table", roles= "@parent", verb = "@parent", protocol = "@parent")
  def createFromTable(tableName:String, modelName:String, standardize:Boolean, lang:String): Result[String] = {
    val con = this.context.cfg.dbCon().get
    val meta = new DbMeta(con)
    var model = meta.getTableAsModel(tableName)
    if(standardize){
      model = model.standardize()
    }

    // 1. Create model properties
    val map = new ListMap[String, String]()
    map("@{model.name}"            ) = modelName
    map("@{model.nameLower}"       ) = modelName(0).toLower + modelName.substring(1)
    map("@{model.table}"           ) = tableName
    map("@{model.fieldsDef}"       ) = ""
    map("@{model.fieldsInstance}"  ) = ""
    map("@{model.fieldAssignments}") = buildAssignments(model)
    map("@{model.fieldResources}"  ) = buildJsResources(model)

    // 2. Generate the SQL
    val asSql = new DbBuilder().addTable(model)
    val finalModelName = Strings.valueOrDefault(modelName, tableName)
    saveModelTableFile(finalModelName, asSql)

    // 3. Generate the Javascript Web API
    val jsModelDef = model.toJSFieldDefs()
    val jsModelInstances = model.toJSFieldInstances()
    val jsApi = processTemplate(modelName, jsModelDef, jsModelInstances, "jsApi.txt")
    saveClientJsFile(modelName, jsApi, "Api")

    // 4. Generate Javascript resources
    val jsRes = processTemplate("jsRes.txt", map)
    saveClientJsFile(modelName, jsRes, "Res")

    // 5. Generate Model
    val modelCode = processTemplate("model.txt", map)
    saveServerModelFile(modelName, modelCode, lang)

    // 6. Generate Model API
    val modelApi = processTemplate("modelApi.txt", map)
    saveServerModelApiFile(modelName, modelApi, lang)

    // 7. Generate Model API
    val modelRoutes = processTemplate("modelRoutes.txt", map)
    saveServerModelRoutesFile(modelName, modelRoutes, lang)

    success(asSql, Some("total : " + model.name))
  }


  @ApiAction(name = "", desc= "generates a model", roles= "@parent", verb = "@parent", protocol = "@parent")
  def generateModel(modelSpecFile:String, targetLang:String) : Result[String] = {
    success("generated model")
  }


  private def saveModelTableFile(name:String, content:String):Unit = {
    val path = _sqlFolder + File.separator + name + ".sql"
    Files.writeAllText(path, content)
  }


  private def saveClientJsFile(name:String, content:String, suffix:String = ""):Unit = {
    val path = _jsFolder + File.separator + name + suffix + ".js"
    Files.writeAllText(path, content)
  }


  private def saveServerModelFile(name:String, content:String, ext:String):Unit = {
    val path = _serverFolder + File.separator + name + s".${ext}"
    Files.writeAllText(path, content)
  }


  private def saveServerModelApiFile(name:String, content:String, ext:String):Unit = {
    val path = _serverFolder + File.separator + name + "Api" + s".${ext}"
    Files.writeAllText(path, content)
  }


  private def saveServerModelRoutesFile(name:String, content:String, ext:String):Unit = {
    val path = _serverFolder + File.separator + name + "Routes" + s".${ext}"
    Files.writeAllText(path, content)
  }


  private def saveModelDefFile(name:String, content:String):Unit = {
    val path = _defFolder + File.separator + name + ".txt"
    Files.writeAllText(path, content)
  }


  private def generateJavascript(model:Model):String = {

    val builder = new ObjectBuilderJson(true, "    ")
    builder.indentInc()
    for (field <- model.fields) {
      builder.putString(field.name, "")
    }
    builder.indentDec()
    val modelJs = builder.toString()
    processTemplate(model.name, modelJs, "", "jsNew.txt")
  }


  private def generatePhp(model:Model):String = {

    val builder = new ObjectBuilder(true, "    ")
    for (field <- model.fields) {
      // public $var = 'a default value';
      builder.putLine("public $" + field.name + " = '';")
    }
    val modelJs = builder.toString()
    processTemplate(model.name, modelJs, "", "phpModel.txt")
  }


  private def buildAssignments(model:Model):String = {

    val builder = new ObjectBuilder(true, "    ")
    for (field <- model.fields) {
      if(!field.isStandard() && field.name != "id") {
        builder.putLine("$model->" + field.name + " = " + "$data->" + field.name + ";")
      }
    }
    val modelAssign = builder.toString()
    modelAssign
  }


  private def buildJsResources(model:Model):String = {

    val builder = new ObjectBuilder(true, "    ")
    var count = 0
    for (field <- model.fields) {
      if(!field.isStandard() && field.name != "id") {
        val prefix = if(count > 0) "," else ""
        builder.putLine(s"${prefix} ${field.name} : { desc: '', eg: '', egmulti: '' }")
        count += 1
      }
    }
    val modelAssign = builder.toString()
    modelAssign
  }


  private def processTemplate(name:String, content:String, content2:String, file:String):String = {
    val template = Files.readAllText(_templatesFolder + File.separator + file)
    var finalJs = template.replaceAllLiterally("@{model.name}", name)
    finalJs = finalJs.replaceAllLiterally("@{model.nameLower}", name(0).toLower + name.substring(1))
    finalJs = finalJs.replaceAllLiterally("@{model.fieldsDef}", content )
    finalJs = finalJs.replaceAllLiterally("@{model.table}", content )
    finalJs = finalJs.replaceAllLiterally("@{model.fieldsInstance}", content2 )
    finalJs
  }


  private def processTemplate(file:String, replacements:ListMap[String,String]):String = {
    val template = Files.readAllText(_templatesFolder + File.separator + file)
    var finalJs = template
    for(key <- replacements.keys()){
      val value = replacements(key)
      finalJs = finalJs.replaceAllLiterally(key, value)
    }
    finalJs
  }


  private def createOutputDir():String = {
    val userHome = System.getProperty("user.home")
    val slateDir = Files.mkDir(userHome, "slatekit")
    val outputDir = Files.mkDir(slateDir, "outputs")
    outputDir
  }


  private def createFolders(outputDir:Option[String] = None): String = {
    val finalOutputDir = outputDir.getOrElse(createOutputDir())
    Files.mkDir(finalOutputDir, "sql")
    Files.mkDir(finalOutputDir, "def")
    Files.mkDir(finalOutputDir, "server")
    Files.mkDir(finalOutputDir, "js")
    Files.mkDir(finalOutputDir, "templates")
    finalOutputDir
  }


  private def getOutputFolder():String = {
    val path = this.context.dirs.fold[String](createFolders())( d => {
      createFolders(Some(d.pathToOutputs))
    })
    path
  }
}
