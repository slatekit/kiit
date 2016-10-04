import java.nio.file.{StandardCopyOption, CopyOption, Path}

import sys.process._
import java.io.File
import java.net.URI
import scala.collection.mutable.{ListBuffer}

trait LogSupport {
	var level:Int = 1
	
	def debug(msg:String) :Unit = {
		performLog(1, msg)
	}
	
	
	def info(msg:String) :Unit = {
		performLog(2, msg)
	}
	
	
	def warn(msg:String) :Unit = {
		performLog(3, msg)
	}
	
	
	def error(msg:String) :Unit = {
		performLog(4, msg)
	}
	
	
	def log(msg:String) :Unit = {
		performLog(0, msg)
	}
	
	
	def performLog(logLevel:Int, msg:String) : Unit = {
		
		if(logLevel != 0 && logLevel < level )
			return
		
		if(logLevel == 0)
			println(msg)	
		else if(logLevel == 1 )
			println("DEBUG: " + msg)
		else if(logLevel == 2)
			println("INFO: " + msg)
		else if(logLevel == 3)
			println("WARN: " + msg)
		else if(logLevel == 4)
			println("ERROR: " + msg)			
	}
}


class Project()
{
  var id      = ""
  var name    = ""
  var folder  = ""
  var root    = ""
  var lib     = ""
  var jar     = ""
  var jarPath = ""
  var projectType = ""
  var depends = List[Project]()
  var libs    = List[String]()
  var app:BuildScript = null


  def this(script:BuildScript, id:String, name:String, projectType:String, folder:String) =
  {
    this()
	this.id = id
    this.app = script
    this.name = name
    this.folder = folder
	this.projectType = projectType
    this.root = normalize(s"${app.APP_ROOT}/${folder}/${name}")
    this.lib = normalize(s"${app.APP_ROOT}/${folder}/${name}/lib")
  }


  def targetJar(suffix:String) : Project =
  {
    this.jar = normalize(s"${id}_${suffix}")
    this.jarPath = normalize(s"${app.APP_ROOT}/${folder}/${name}/target/scala-${app.SCALA_VERSION}/${this.jar}")
    return this
  }


  def depends(dependencies:List[Project]) : Project =
  {
    depends = dependencies
    return this
  }
  
  
  def clean(): Unit = {
  
	if(!hasDependencies )
		return 
		
	for(proj <- depends ) {
		println(s"deleting file : ${lib}/${proj.jar}" )
		proj.clean()
	}
  }
  
  
  def eachDependency(callback:(Project) => Unit ): Unit = {
	if ( hasDependencies )
	{
		for(pd <- depends )
		{
			if(pd != null)
			{
				callback(pd)
			}
		}
	}
  }
  
  
  def eachLib(callback:(String) => Unit ): Unit = {
	if ( hasLibs )
	{
		for(lib <- libs )
		{
			callback(lib)
		}
	}
  }
  
  
  def hasDependencies : Boolean = depends != null && depends.size > 0
  def hasLibs : Boolean = libs != null && libs.size > 0
  

  def print() =
  {
    println("************************************************"    )
    println(Console.GREEN  + "PROJECT ID         = " + id         )
    println(Console.WHITE + "PROJECT NAME       = " + name        )
    println(Console.WHITE + "PROJECT TYPE       = " + projectType )
    println(Console.WHITE + "PROJECT FOLDER     = " + folder      )
    println(Console.WHITE + "PROJECT ROOT       = " + root        )
    println(Console.WHITE + "PROJECT JAR        = " + jar         )
    println(Console.WHITE + "PROJECT JAR PATH   = " + jarPath     )
	println(Console.WHITE + "  DEPENDS ON")
	for(proj <- depends ){
		println("  - " + proj.id)
	}
    println(Console.WHITE + "************************************************")
  }


  protected def normalize(path:String) : String =
  {
    val npath = path.replaceAllLiterally("\\", "/")
    return npath
  }
}


object Project
{
  def apply(script:BuildScript, id:String, name:String, projectType:String, folder:String): Project =
  {
    val proj = new Project(script, id, name, projectType, folder)
    return proj
  }
}


/**
  * Created by kreddy on 12/16/2015.
  * http://blog.bstpierre.org/scala-single-file-executable-jar
  * Running scala app: 
  * scala -classpath "slate-examples-app_2.11-1.0.jar;slate-lib-test_2.11-1.0.jar" samples.Examples
  * java -cp %SCALA_HOME%\lib\scala-library.jar;slate-examples-app_2.11-1.0.jar;slate-lib-test_2.11-1.0.jar;  samples.Examples
  */

class BuildScript extends LogSupport
{
  val COMPILE = "compile"
  val PACKAGE = "package"
  val BUILD   = "build"
  val CLEAN   = "clean"
  val PACK    = "pack"
  
  var SCALA_VERSION       = ""
  var SCALA_LIB_PATH      = ""
  var SCALA_RFL_PATH      = ""
  var SCALA_CFG_PATH      = ""
  var SCALA_JSN_PATH      = ""
  var SCALA_TARGET_FOLDER = ""
  var SCALA_JAR_SUFFIX    = ""
  
  var APP_NAME           = ""
  var APP_ROOT           = ""
  var APP_RELEASE_DIR    = ""
  var APP_RELEASE        = ""
  var APP_BUILD          = ""
  var APP_VERSION        = ""
  var APP_BUILD_DIR      = ""
  var APP_DEST_DIR       = ""

  var proj: Project = null
  var projects = new ListBuffer[Project]()
  var projJars = new ListBuffer[String]()

	
  def init() =
  {

  }


  def clean():Unit =
  {
	eachProject( None, p => {
		clean(p.id)
	})
  }
  
  
  def build() : Unit = {
	eachProject( None, p => {
		build(p.id)
	})
  }


  def exec() : Unit = {
	
  }
  
  
  def pack() : Unit = {
	  
  }
  
  
  def exec(action:String): Unit = {
	printProjects()	
	action match {
		case "package"  => pack()
		case "clean"    => clean()
		case "build"    => build()
		case _          => println("unknown build action")
	}
  }


  def shutdown() =
  {
  }


  def print() =
  {
    log("************************************************")
    log("NAME                = " + this.getClass().getSimpleName())
    log("SCALA_VERSION       = " + SCALA_VERSION      )
    log("SCALA_LIB_PATH      = " + SCALA_LIB_PATH     )
    log("SCALA_RFL_PATH      = " + SCALA_RFL_PATH     )
	log("SCALA_TARGET_FOLDER = " + SCALA_TARGET_FOLDER)
	log("SCALA_JAR_SUFFIX    = " + SCALA_JAR_SUFFIX   )
	
    log("APP_NAME           = " + APP_NAME            )
    log("APP_ROOT           = " + APP_ROOT            )
    log("APP_BUILD_DIR      = " + APP_BUILD_DIR       )
    log("APP_RELEASE_DIR    = " + APP_RELEASE_DIR     )
    log("APP_RELEASE        = " + APP_RELEASE         )
    log("APP_BUILD          = " + APP_BUILD           )
    log("APP_VERSION        = " + APP_VERSION         )
    log("************************************************")
  }


  def setProj(proj:Project) : Project =
  {
    this.proj = proj
    return proj
  }
  
  
  def sbt(p:Project, action:String) = 
  {
    debug("calling sbt : " + action)
    p.print()
    val sbt_home = sys.env("SBT_HOME")
    var sbtexec = sbt_home + "sbt.bat"
    val output = Process(sbtexec + " " + action, new File(p.root)).!
    println(output)
	println()
	println()
	projJars.append(p.jar)
    //"sbt package".!!
  }
  
  
  def cleanlib(projectName:String):Unit = {
	println("cleaning lib")
  }
  
  
  def add(id:String, name:String, projectType:String, folder:String, dependencies:List[String], libs:List[String] = null): BuildScript = {
	val proj = Project(this, id, name, projectType, folder).targetJar(s"${SCALA_VERSION}-${APP_RELEASE}.jar")
	proj.libs = libs
	projects.append(proj)
	if(dependencies != null ) {
		var projectDependencies = new ListBuffer[Project]()
		for(dependency <- dependencies) {
			var dependentProj = find(dependency)
			if (dependentProj.isDefined ){
				projectDependencies.append(dependentProj.get)
			}
		}
		
		proj.depends(projectDependencies.toList)
	}
	this
  }
  
  
  def find(id:String):Option[Project] = {
	
	val matches = projects.filter( p => p.id == id )
	if ( matches == null || matches.size == 0 ) {
		debug(s"${id} not found")
		return None 
	}
	debug(s"${id} found")
	Some(matches(0))
  }
  
  
  def build(id:String): BuildScript = {
  
	// get project by id not name ( e.g. slate-core )
	val p = find(id).get	
	debug("building : " + p.id)
	
	// copy scala library jars to project lib 
	debug("copying scala jars to : " + p.id)
	copyScalaJars(p)
    
	// each dependency 
	p.eachDependency( (pd) => {	
		debug(s"project requires dependency: ${pd.id}")
		debug(s"copying ${pd.jar} to : ${p.lib}/${pd.jar}")
		copyfile(normalize(s"${APP_BUILD_DIR}/${pd.jar}") , normalize(s"${p.lib}/${pd.jar}"))
	})
	
	// each library
	p.eachLib( (lib) => {
		debug(s"project requires lib: ${lib}")
		if(lib == "aws") {
			copyAWSJars(p)
		}
	})
	
	// now package the app 
	proj = p
	sbt(p, PACKAGE)
	
	// copy the project output jars to build lib directory
	debug(s"copying : ${p.jarPath} to ${APP_BUILD_DIR}/${p.jar}")
    copyfile(p.jarPath, normalize(s"${APP_BUILD_DIR}/${p.jar}"))
	this
  }
  
  
  def printProjects():Unit = {
	println("print projects")
	for(proj <- projects){
		proj.print()
	}
  }
  
  
  def clean(id:String): BuildScript = {
	println(s"cleaning ${id}")
	val p = find(id).get 
	
	// each dependency 
	p.eachDependency( (pd) => {
		deleteFile(s"${p.lib}/${pd.jar}" )
	})
	
	// from build/lib 
	deleteFile(s"${APP_BUILD_DIR}/${p.jar}")
	
	sbt(p, CLEAN)
	
	this
  }
  
  
  def cleanAll(): BuildScript = {
	println("clean all")
	eachProject( None, p => {
		clean(p.id)
	})
	this
  }


  def copyfile(file:String, dest:String): Unit =
  {
	debug(s"copying file: ${file} to ${dest}")
    java.nio.file.Files.copy(new File(file).toPath(), new File(dest).toPath(), StandardCopyOption.REPLACE_EXISTING)
  }
  
  
  def mkDir(path:String): Unit = {
	  debug(s"creating directory: ${path}")
	  val file = new File(path)
	  if(!file.exists){
		  file.mkdir()
	  }
  }
  
  
  def deleteFile(path: String) = {
	debug(s"deleting file: ${path}")
    val fileTemp = new File(path)
    if (fileTemp.exists) {
       fileTemp.delete()
    }
  }
  
  
  def copyScalaJars(proj:Project):Unit = {
    copyfile(SCALA_RFL_PATH , normalize(s"${proj.lib}/scala-reflect.jar"  ) )
    copyfile(SCALA_CFG_PATH , normalize(s"${proj.lib}/config-1.3.0.jar"   ) )
    copyfile(SCALA_JSN_PATH , normalize(s"${proj.lib}/json_simple-1.1.jar") )
  }
  
  
  def copyAWSJars(proj:Project):Unit = {  
    copyfile(normalize(s"${APP_ROOT}/lib/ext/java/commons-codec-1.9.jar"        ) , normalize(s"${proj.lib}/commons-codec-1.9.jar"          ))
    copyfile(normalize(s"${APP_ROOT}/lib/ext/java/commons-logging-1.2.jar"      ) , normalize(s"${proj.lib}/commons-logging-1.2.jar"        ))
    copyfile(normalize(s"${APP_ROOT}/lib/ext/java/httpclient-4.5.1.jar"         ) , normalize(s"${proj.lib}/httpclient-4.5.1.jar"           ))
	copyfile(normalize(s"${APP_ROOT}/lib/ext/java/httpcore-4.4.3.jar"           ) , normalize(s"${proj.lib}/httpcore-4.4.3.jar"             ))
	copyfile(normalize(s"${APP_ROOT}/lib/ext/java/jackson-annotations-2.7.0.jar") , normalize(s"${proj.lib}/jackson-annotations-2.7.0.jar"  ))
	copyfile(normalize(s"${APP_ROOT}/lib/ext/java/jackson-core-2.7.6.jar"       ) , normalize(s"${proj.lib}/jackson-core-2.7.6.jar"         ))
	copyfile(normalize(s"${APP_ROOT}/lib/ext/java/jackson-databind-2.7.6.jar"   ) , normalize(s"${proj.lib}/jackson-databind-2.7.6.jar"     ))
	copyfile(normalize(s"${APP_ROOT}/lib/ext/java/joda-time-2.8.1.jar"          ) , normalize(s"${proj.lib}/joda-time-2.8.1.jar"            ))
  }
  
  
  def copyJars(jars:List[(String,String)], destination:String): Unit = {
	 for(jar <- jars) {
		copyfile(normalize(s"${jar._2}") , normalize(s"${destination}/${jar._1}"))
	} 
  }
  
  
  def copyProjectLibs(destination:String): Unit = {
	 eachProjectLib( proj => {
		copyfile(normalize(s"${APP_BUILD_DIR}/${proj.jar}") , normalize(s"${destination}/${proj.jar}"))
	})
  }
  
  
  def compile2(id:String, name:String, projectType:String, folder:String, dependencies:ListBuffer[String]): Unit = {
    
	// setup project
    setProj(Project(this, id, name, projectType, folder)
			.targetJar(s"${SCALA_VERSION}-${APP_RELEASE}.jar"))
    
	// copy scala library jars to project lib 
	copyScalaJars(this.proj)
    
	if ( dependencies != null && dependencies.size > 0)
	{
		for(dpath <- dependencies)
		{
			if(dpath != null && dpath != "")
			{
				copyfile(normalize(s"${APP_BUILD_DIR}/${dpath}"), normalize(s"${proj.lib}/${dpath}") )
			}
		}
	}
	// no package the app 
	sbt(proj, PACKAGE)
	
	// copy the project output jars to build lib directory
    copyfile(proj.jarPath, normalize(s"${APP_BUILD_DIR}/${proj.jar}"))
  }
  
  
  
  
  def eachProjectLib(callback:(Project) => Unit ): Unit = {
	eachProject(Some("lib"), callback)
  }
  
  
  def eachProject(projectType:Option[String], callback:(Project) => Unit ): Unit = {
	if ( projects != null && projects.size > 0 )
	{
		for(proj <- projects )
		{
			if(proj != null)
			{
				if(projectType.isDefined && proj.projectType == projectType.get ){
					callback(proj)
				}
				else {
					callback(proj)
				}
			}
		}
	}
  }


  protected def normalize(path:String) : String =
  {
    val npath = path.replaceAllLiterally("\\", "/")
    return npath
  }
}


object BuildScript
{
  def run(script:BuildScript) =
  {
    println("starting...")
    script.init()
    script.print()
    script.exec()
    script.shutdown()
    println("ended")
  }
}

