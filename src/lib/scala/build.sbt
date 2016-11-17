// http://central.sonatype.org/pages/ossrh-guide.html
// http://www.sonatype.org/nexus/2015/06/02/how-to-publish-software-artifacts-to-maven-central/
lazy val commonSettings = Seq(
  organization := "com.slatekit",
  version := "1.2.0",
  scalaVersion := "2.11.8",
  useGpg := true,
  homepage := Some(url("http://www.slatekit.com/")),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  //resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  scaladexKeywords := Seq("utils"),
  credentials in Scaladex += Credentials(Path.userHome / ".ivy2" / ".scaladex.credentials"),  
  pomExtra := (
    <url>https://github.com/code-helix/slatekit</url>
      <licenses>
        <license>
          <name>Apache 2.0</name>
          <url>https://github.com/code-helix/slatekit/blob/master/LICENSE</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:code-helix/slatekit.git</url>
        <connection>scm:git@github.com:code-helix/slatekit.git</connection>
      </scm>
      <developers>
        <developer>
          <id>kishorereddy</id>
          <name>Kishore Reddy</name>
          <url>http://github.com/kishorereddy</url>
        </developer>
      </developers>
  )
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
      

//, entities, core, tools, integration, cloud, shell, server  
lazy val root = (project in file(".")).
  aggregate(common).
  settings(commonSettings: _*).
  settings(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)
