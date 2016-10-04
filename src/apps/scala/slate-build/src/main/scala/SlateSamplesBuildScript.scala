
class SlateSamplesBuildScript extends BuildScript
{
  val ACTION = "none"
  var LIBS_EXT = List[(String,String)]()
  var LIBS_SCA = List[(String,String)]()
  var LIBS_AKKA = List[(String,String)]()
  var LIBS_SLATE = List[(String,String)]()
	
  def setPaths(): Unit = {	  
	// Constants ( names, folders, etc )
	APP_NAME = "slatekit";
    APP_ROOT = normalize(sys.env("SCALA_SLATE_HOME"))
    APP_RELEASE = "1.0"
    APP_BUILD = "12"
    APP_VERSION = s"${APP_RELEASE}.${APP_BUILD}"
	APP_BUILD_DIR = normalize( s"${APP_ROOT}/build/lib" )
	APP_RELEASE_DIR = normalize( s"${APP_ROOT}/dist/${APP_NAME}/releases/${APP_RELEASE}" )
    SCALA_VERSION = "2.11"
	SCALA_TARGET_FOLDER = "scala-2.11"
	SCALA_JAR_SUFFIX = "_2.11-1.0"
	SCALA_LIB_PATH = normalize(s"${APP_ROOT}/lib/ext/scala/scala-library.jar")
	SCALA_RFL_PATH = normalize(s"${APP_ROOT}/lib/ext/scala/scala-reflect.jar")
	SCALA_CFG_PATH = normalize(s"${APP_ROOT}/lib/ext/scala/config-1.3.0.jar")
	SCALA_JSN_PATH = normalize(s"${APP_ROOT}/lib/ext/java/json_simple-1.1.jar")
	
	LIBS_SCA = List[(String,String)](
	  ("scala-library.jar"              , s"${APP_ROOT}/lib/ext/scala/scala-library.jar"           ),
	  ("scala-reflect.jar"              , s"${APP_ROOT}/lib/ext/scala/scala-reflect.jar"           )
	)
	
	LIBS_EXT = List[(String,String)](
	  ("mysql-connector-java-5.1.38-bin.jar" , s"${APP_ROOT}/lib/ext/mysql/mysql-connector-java-5.1.38-bin.jar" ),
	  ("config-1.3.0.jar"                    , s"${APP_ROOT}/lib/ext/scala/config-1.3.0.jar"                    ),
	  ("commons-codec-1.9.jar"               , s"${APP_ROOT}/lib/ext/java/commons-codec-1.9.jar"                ),
	  ("commons-logging-1.2.jar"             , s"${APP_ROOT}/lib/ext/java/commons-logging-1.2.jar"              ),
	  ("httpclient-4.5.1.jar"                , s"${APP_ROOT}/lib/ext/java/httpclient-4.5.1.jar"                 ),
	  ("httpcore-4.4.3.jar"                  , s"${APP_ROOT}/lib/ext/java/httpcore-4.4.3.jar"                   ),
	  ("jackson-annotations-2.7.0.jar"       , s"${APP_ROOT}/lib/ext/java/jackson-annotations-2.7.0.jar"        ),
	  ("jackson-core-2.7.6.jar"              , s"${APP_ROOT}/lib/ext/java/jackson-core-2.7.6.jar"               ),
	  ("jackson-databind-2.7.6.jar"          , s"${APP_ROOT}/lib/ext/java/jackson-databind-2.7.6.jar"           ),
	  ("joda-time-2.8.1.jar"                 , s"${APP_ROOT}/lib/ext/java/joda-time-2.8.1.jar"                  ),
	  ("json_simple-1.1.jar"                 , s"${APP_ROOT}/lib/ext/java/json_simple-1.1.jar"                  )
	)
	
	LIBS_AKKA = List[(String,String)](
	  ("akka-actor_2.11-2.4.10.jar"                     , s"${APP_ROOT}/lib/ext/akka/akka-actor_2.11-2.4.10.jar"                      ),                  
      ("akka-http-core_2.11-4.10.jar"                   , s"${APP_ROOT}/lib/ext/akka/akka-http-core_2.11-4.10.jar"                    ),
      ("akka-http-experimental_2.11-4.10.jar"           , s"${APP_ROOT}/lib/ext/akka/akka-http-experimental_2.11-4.10.jar"            ),
      ("akka-http-spray-json-experimental_2.11-4.10.jar", s"${APP_ROOT}/lib/ext/akka/akka-http-spray-json-experimental_2.11-4.10.jar" ),
      ("akka-parsing_2.11-2.4.10.jar"                   , s"${APP_ROOT}/lib/ext/akka/akka-parsing_2.11-2.4.10.jar"                    ),
      ("akka-stream-experimental_2.11-4.10.jar"         , s"${APP_ROOT}/lib/ext/akka/akka-stream-experimental_2.11-4.10.jar"          ),
      ("akka-stream_2.11-2.4.10.jar"                    , s"${APP_ROOT}/lib/ext/akka/akka-stream_2.11-2.4.10.jar"                     ),
      ("reactive-streams-1.0.0.jar"                     , s"${APP_ROOT}/lib/ext/akka/reactive-streams-1.0.0.jar"                      ),
      ("scala-java8-compat_2.11-0.7.0.jar"              , s"${APP_ROOT}/lib/ext/akka/scala-java8-compat_2.11-0.7.0.jar"               ),
      ("scala-parser-combinators_2.11-1.0.4.jar"        , s"${APP_ROOT}/lib/ext/akka/scala-parser-combinators_2.11-1.0.4.jar"         ),
      ("spray-json_2.11-1.3.2.jar"                      , s"${APP_ROOT}/lib/ext/akka/spray-json_2.11-1.3.2.jar"                       ),
      ("ssl-config-akka_2.11-0.2.1.jar"                 , s"${APP_ROOT}/lib/ext/akka/ssl-config-akka_2.11-0.2.1.jar"                       ),
      ("ssl-config-core_2.11-0.2.1.jar"                 , s"${APP_ROOT}/lib/ext/akka/ssl-config-core_2.11-0.2.1.jar"                       )
	)
	
	/*
	LIBS_AKKA = List[(String,String)](
	  ("akka-actor_2.11-2.4.4.jar"                         , s"${APP_ROOT}/lib/ext/akka/akka-actor_2.11-2.4.4.jar"                        ),                  
      ("akka-http-core_2.11-2.4.4.jar"                     , s"${APP_ROOT}/lib/ext/akka/akka-http-core_2.11-2.4.4.jar"       ),
      ("akka-http-experimental_2.11-2.4.4.jar"             , s"${APP_ROOT}/lib/ext/akka/akka-http-experimental_2.11-2.4.4.jar"            ),
      ("akka-http-spray-json-experimental_2.11-2.4.4.jar"  , s"${APP_ROOT}/lib/ext/akka/akka-http-spray-json-experimental_2.11-2.4.4.jar" ),
      ("akka-parsing_2.11-2.4.4.jar"                       , s"${APP_ROOT}/lib/ext/akka/akka-parsing_2.11-2.4.4.jar"         ),
      ("akka-stream_2.11-2.4.4.jar"                        , s"${APP_ROOT}/lib/ext/akka/akka-stream_2.11-2.4.4.jar"          ),
      ("reactive-streams-1.0.0.jar"                        , s"${APP_ROOT}/lib/ext/akka/reactive-streams-1.0.0.jar"                       ),
      ("spray-json_2.11-1.3.2.jar"                         , s"${APP_ROOT}/lib/ext/akka/spray-json_2.11-1.3.2.jar"                        )
	)
	LIBS_AKKA = List[(String,String)](
	  ("akka-actor_2.11-2.4.10.jar"                         , s"${APP_ROOT}/lib/ext/akka/akka-actor_2.11-2.4.10.jar"                         ),                  
      ("akka-http-core_2.11-2.4.10.jar"                     , s"${APP_ROOT}/lib/ext/akka/akka-http-core_2.11-2.4.10.jar"                     ),
      ("akka-http-experimental_2.11-2.4.10.jar"             , s"${APP_ROOT}/lib/ext/akka/akka-http-experimental_2.11-2.4.10.jar"             ),
      ("akka-http-spray-json-experimental_2.11-2.4.10.jar"  , s"${APP_ROOT}/lib/ext/akka/akka-http-spray-json-experimental_2.11-2.4.10.jar"  ),
      ("akka-parsing_2.11-2.4.10.jar"                       , s"${APP_ROOT}/lib/ext/akka/akka-parsing_2.11-2.4.10.jar"                       ),
      ("akka-stream_2.11-2.4.10.jar"                        , s"${APP_ROOT}/lib/ext/akka/akka-stream_2.11-2.4.10.jar"                        ),
      ("reactive-streams-1.0.0.jar"                         , s"${APP_ROOT}/lib/ext/akka/reactive-streams-1.0.0.jar"                         ),
      ("spray-json_2.11-1.3.2.jar"                          , s"${APP_ROOT}/lib/ext/akka/spray-json_2.11-1.3.2.jar"                          )
	)
	
	*/
	
	
	
	LIBS_SLATE = List[(String,String)](
	  ( "slate-cloud_2.11-1.0.jar"       , s"${APP_ROOT}/build/lib/slate-cloud_2.11-1.0.jar"        ),                  
      ( "slate-common_2.11-1.0.jar"      , s"${APP_ROOT}/build/lib/slate-common_2.11-1.0.jar"       ),
      ( "slate-core_2.11-1.0.jar"        , s"${APP_ROOT}/build/lib/slate-core_2.11-1.0.jar"         ),
      ( "slate-entities_2.11-1.0.jar"    , s"${APP_ROOT}/build/lib/slate-entities_2.11-1.0.jar"     ),
      ( "slate-ext_2.11-1.0.jar"         , s"${APP_ROOT}/build/lib/slate-ext_2.11-1.0.jar"          ),
      ( "slate-integration_2.11-1.0.jar" , s"${APP_ROOT}/build/lib/slate-integration_2.11-1.0.jar"  ),
      ( "slate-server_2.11-1.0.jar"      , s"${APP_ROOT}/build/lib/slate-server_2.11-1.0.jar"       ),
      ( "slate-shell_2.11-1.0.jar"       , s"${APP_ROOT}/build/lib/slate-shell_2.11-1.0.jar"        ),
	  ( "slate-tools_2.11-1.0.jar"       , s"${APP_ROOT}/build/lib/slate-tools_2.11-1.0.jar"        )
	  
	)
  }
  
  
  override def init(): Unit =
  {	
	setPaths()
	val awsJars = List[String]("aws")	
	add("sampleapp-core"  , "SampleApp.Core"  , "lib", "src/apps/scala/Slate.SampleApp",  List[String]( ))
	add("sampleapp-batch" , "SampleApp.Batch" , "app", "src/apps/scala/Slate.SampleApp",  List[String]("sampleapp-core"))
	add("sampleapp-cli"   , "SampleApp.CLI"   , "app", "src/apps/scala/Slate.SampleApp",  List[String]("sampleapp-core"))
	add("sampleapp-server", "SampleApp.Server", "app", "src/apps/scala/Slate.SampleApp",  List[String]("sampleapp-core"))
  }


  override def clean() : Unit =
  {
	info(s"cleaning ${APP_NAME}")
	// clean all the projects in list 
	super.clean()
  }
  
  
  override def build(): Unit = 
  {
	info(s"building ${APP_NAME}")
	// build all the projects in list 
	super.build()
  }
  
  
  override def exec(): Unit = 
  {
	info(s"executing ${APP_NAME}")
	
	copyJars(LIBS_SLATE , s"${APP_ROOT}/src/apps/scala/Slate.SampleApp/SampleApp.Batch/lib" )
	copyJars(LIBS_SLATE , s"${APP_ROOT}/src/apps/scala/Slate.SampleApp/SampleApp.CLI/lib"   )
	copyJars(LIBS_SLATE , s"${APP_ROOT}/src/apps/scala/Slate.SampleApp/SampleApp.Core/lib"  )
	copyJars(LIBS_SLATE , s"${APP_ROOT}/src/apps/scala/Slate.SampleApp/SampleApp.Server/lib")
	
	super.exec(BUILD)
  }
  
  
  override def pack(): Unit = {
	mkDir(s"${APP_ROOT}/dist/${APP_NAME}")
	mkDir(s"${APP_ROOT}/dist/${APP_NAME}/releases")
	mkDir(s"${APP_ROOT}/dist/${APP_NAME}/releases/${APP_RELEASE}")
	mkDir(s"${APP_ROOT}/dist/${APP_NAME}/releases/${APP_RELEASE}/samples")
  }
}
