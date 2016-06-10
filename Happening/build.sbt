name := """Happening"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.webjars" % "bootstrap" % "3.0.0",
  "org.webjars" % "angularjs" % "1.2.16",
  "org.webjars" % "angular-leaflet-directive" % "0.8.2",
  "com.typesafe.akka" %% "akka-actor" % "2.3.3",
  "org.webjars" % "Leaflet.heat" % "0.2.0",
  "org.twitter4j"% "twitter4j-core"% "4.0.4",
  "org.json"%"org.json"%"chargebee-1.0",
  "org.apache.spark" %% "spark-core" % "1.6.1",
  "org.apache.spark" %% "spark-streaming-twitter" % "1.6.1"
)

dependencyOverrides ++= Set(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.0" ,
  "com.fasterxml.jackson.module" % "jackson-module-scala" % "2.6.0"


)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)


//fork in run := true