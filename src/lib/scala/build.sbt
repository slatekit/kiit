// http://central.sonatype.org/pages/ossrh-guide.html
// http://www.sonatype.org/nexus/2015/06/02/how-to-publish-software-artifacts-to-maven-central/
lazy val commonSettings = Seq(
  organization := "com.slatekit",
  version := "1.2.0",
  scalaVersion := "2.11.8",
  homepage := Some(url("http://www.slatekit.com/")),
  scaladexKeywords := Seq("api", "arguments", "cli", "crypto", "database", "server", "shell", "utils"),
  credentials in Scaladex := Seq(Credentials(Path.userHome / ".ivy2" / ".scaladex.credentials"))
)

lazy val common = (project in file("Slate.Common")).
  settings(commonSettings: _*).
  settings(
    name := "slate-common",
	libraryDependencies ++= Seq(
	   "org.scala-lang"             % "scala-reflect" % scalaVersion.value,
	   "com.typesafe"               % "config"        % "1.3.0",
	   "com.googlecode.json-simple" % "json-simple"   % "1.1"
	)
  )
  
lazy val entities = (project in file("Slate.Entities")).
  settings(commonSettings: _*).
  settings(    
    name := "slate-entities",
	libraryDependencies ++= Seq(
	   "org.scala-lang"             % "scala-reflect"        % scalaVersion.value,
	   "com.typesafe"               % "config"               % "1.3.0",
	   "com.googlecode.json-simple" % "json-simple"          % "1.1",
	   "mysql"                      % "mysql-connector-java" % "5.1.40"
	)
  ).
  dependsOn(common)
  
  
lazy val core = (project in file("Slate.Core")).
  settings(commonSettings: _*).
  settings(    
    name := "slate-core",
	libraryDependencies ++= Seq(
	   "org.scala-lang"             % "scala-reflect" % scalaVersion.value,
	   "com.typesafe"               % "config"        % "1.3.0",
	   "com.googlecode.json-simple" % "json-simple"   % "1.1"
	)
  ).
  dependsOn(common,entities)
  
 
lazy val tools = (project in file("Slate.Tools")).
  settings(commonSettings: _*).
  settings(    
    name := "slate-tools",
	libraryDependencies ++= Seq(
	   "org.scala-lang"             % "scala-reflect" % scalaVersion.value,
	   "com.typesafe"               % "config"        % "1.3.0",
	   "com.googlecode.json-simple" % "json-simple"   % "1.1"
	)
  ).
  dependsOn(common,entities,core)

 
lazy val integration = (project in file("Slate.Integration")).
  settings(commonSettings: _*).
  settings(    
    name := "slate-integration",
	libraryDependencies ++= Seq(
	   "org.scala-lang"             % "scala-reflect" % scalaVersion.value,
	   "com.typesafe"               % "config"        % "1.3.0",
	   "com.googlecode.json-simple" % "json-simple"   % "1.1"
	)
  ).
  dependsOn(common,entities,core)  


lazy val cloud = (project in file("Slate.Cloud")).
  settings(commonSettings: _*).
  settings(    
    name := "slate-cloud",
	libraryDependencies ++= Seq(
	   "org.scala-lang"             % "scala-reflect"     % scalaVersion.value,
	   "com.typesafe"               % "config"            % "1.3.0",
	   "com.googlecode.json-simple" % "json-simple"       % "1.1",
	   "com.amazonaws"              % "aws-java-sdk-core" % "1.10.55",
	   "com.amazonaws"              % "aws-java-sdk-s3"   % "1.10.55",
	   "com.amazonaws"              % "aws-java-sdk-sqs"  % "1.10.55"
	)
  ).
  dependsOn(common,entities,core,integration)  
  
/*
lazy val ext = (project in file("Slate.Ext")).
  settings(commonSettings: _*).
  settings(    
    name := "slate-ext",
	libraryDependencies ++= Seq(
	   "org.scala-lang"             % "scala-reflect"        % scalaVersion.value,
	   "com.typesafe"               % "config"               % "1.3.0",
	   "com.googlecode.json-simple" % "json-simple"          % "1.1",
	   "com.amazonaws"              % "aws-java-sdk-core"    % "1.10.55",
	   "com.amazonaws"              % "aws-java-sdk-s3"      % "1.10.55",
	   "com.amazonaws"              % "aws-java-sdk-sqs"     % "1.10.55",
	   "mysql"                      % "mysql-connector-java" % "5.1.40"
	)
  ).
  dependsOn(common,entities,core,integration,cloud)
*/

lazy val shell = (project in file("Slate.Shell")).
  settings(commonSettings: _*).
  settings(    
    name := "slate-shell",
	libraryDependencies ++= Seq(
	   "org.scala-lang"             % "scala-reflect"        % scalaVersion.value,
	   "com.typesafe"               % "config"               % "1.3.0",
	   "com.googlecode.json-simple" % "json-simple"          % "1.1",
	   "mysql"                      % "mysql-connector-java" % "5.1.40"
	)
  ).
  dependsOn(common,entities,core,tools,integration)      


lazy val server = (project in file("Slate.Server")).
  settings(commonSettings: _*).
  settings(    
    name := "slate-server",
	libraryDependencies ++= Seq(
	   "org.scala-lang"             % "scala-reflect"                % scalaVersion.value,
	   "com.typesafe"               % "config"                       % "1.3.0",
	   "com.googlecode.json-simple" % "json-simple"                  % "1.1"
	   //"com.typesafe.akka"          % "akka-actor_2.11 "             % "2.4.4",
	   //"com.typesafe.akka"          % "akka-http-experimental_2.11 " % "2.4.10"
	)
  ).
  dependsOn(common,entities,core,tools,integration)      

//, entities, core, tools, integration, cloud, shell, server  
lazy val root = (project in file(".")).
  aggregate(common, entities, core, tools, integration, cloud, shell, server).
  settings(commonSettings: _*).
  settings(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)