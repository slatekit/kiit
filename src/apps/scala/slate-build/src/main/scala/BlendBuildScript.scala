

class BlendBuildScript extends BuildScript
{
  val ACTION = "none"
  
  
  override def init(): Unit =
  {
	APP_NAME = "blend";
    APP_ROOT = normalize(sys.env("SCALA_SLATE_HOME"))
    APP_RELEASE = "1.0"
    APP_BUILD = "12"
    APP_VERSION = s"${APP_RELEASE}.${APP_BUILD}"
	APP_BUILD_DIR = normalize( s"${APP_ROOT}/build/lib" )
    SCALA_VERSION = "2.11"
	SCALA_TARGET_FOLDER = "scala-2.11"
	SCALA_JAR_SUFFIX = "_2.11-1.0"
	SCALA_LIB_PATH = normalize(s"${APP_ROOT}/lib/ext/scala/scala-library.jar")
	SCALA_RFL_PATH = normalize(s"${APP_ROOT}/lib/ext/scala/scala-reflect.jar")
  }


  override def clean() : Unit =
  {
	println(s"cleaning ${APP_NAME}")
	
	cleanlib("Slate.Core" )
	cleanlib("Slate.Cloud")
	cleanlib("Slate.Ext"  )	
	cleanlib("Blend.Core" )
	cleanlib("Blend.Svc"  )
	cleanlib("Blend.Jobs" )
	cleanlib("Blend.Shl"  )
	cleanlib("Blend.Web"  )
  }
  
  
  override def exec(): Unit = 
  {
	println(s"executing ${APP_NAME}");
	compile2("slate-common", "Slate.Common", "lib", "src/lib/scala",  null)
	compile2("slate-core"  , "Slate.Core"  , "lib", "src/lib/scala",  projJars)
	
	var cloudLib = Project(this, "slate-cloud", "Slate.Cloud"  ,  "lib", "src/lib/scala" ).lib
	var awsjar = "aws-java-sdk-1.10.55.jar";
	copyfile(s"${APP_ROOT}/lib/ext/aws/${awsjar}", s"${cloudLib}/${awsjar}")
	
	compile2("slate-cloud" , "Slate.Cloud"  , "lib", "src/lib/scala"   , projJars)
	compile2("slate-ext"   , "Slate.Ext"    , "lib", "src/lib/scala"   , projJars)	
	compile2("blend-com"   , "Blend.Core"   , "lib", "src/server/scala", projJars)
	compile2("blend-svc"   , "Blend.Svc"    , "lib", "src/server/scala", projJars)
	compile2("blend-jobs"  , "Blend.Jobs"   , "lib", "src/server/scala", projJars)
	compile2("blend-shl"   , "Blend.Shl"    , "lib", "src/server/scala", projJars)
	compile2("blend-web"   , "Blend.Web"    , "lib", "src/server/scala", projJars)
	
  }
}
