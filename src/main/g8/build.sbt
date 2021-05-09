import BuildHelper._

inThisBuild(
  List(
    organization := "dev.zio",
    homepage := Some(url("https://zio.github.io/$name;format="norm"$/")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "jdegoes",
        "John De Goes",
        "john@degoes.net",
        url("http://degoes.net")
      )
    ),
    pgpPassphrase := sys.env.get("PGP_PASSWORD").map(_.toArray),
    pgpPublicRing := file("/tmp/public.asc"),
    pgpSecretRing := file("/tmp/secret.asc")
  )
)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fix", "; all compile:scalafix test:scalafix; all scalafmtSbt scalafmtAll")
addCommandAlias("check", "; scalafmtSbtCheck; scalafmtCheckAll; compile:scalafix --check; test:scalafix --check")

addCommandAlias(
  "testJVM",
  ";$name;format="space,camel"$JVM/test"
)
addCommandAlias(
  "testJS",
  ";$name;format="space,camel"$JS/test"
)
addCommandAlias(
  "testNative",
  ";$name;format="space,camel"$Native/test:compile"
)

val zioVersion = "1.0.7"

lazy val root = project
  .in(file("."))
  .settings(
    publish / skip := true,
    unusedCompileDependenciesFilter -= moduleFilter("org.scala-js", "scalajs-library")
  )
  .aggregate(
    $name;format="space,camel"$JVM,
    $name;format="space,camel"$JS,
    $name;format="space,camel"$Native,
    docs
  )

lazy val $name;format="space,camel"$ = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .in(file("$name;format="space,hyphen,lower"$"))
  .settings(stdSettings("$name;format="space,hyphen,lower"$"))
  .settings(crossProjectSettings)
  .settings(buildInfoSettings("$name;format="space,package,lower"$"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % zioVersion,
      "dev.zio" %% "zio-test"     % zioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % zioVersion % Test
    )
  )
  .settings(testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"))

lazy val $name;format="space,camel"$JS = $name;format="space,camel"$.js
  .settings(jsSettings)
  .settings(scalaJSUseMainModuleInitializer := true)

lazy val $name;format="space,camel"$JVM = $name;format="space,camel"$.jvm
  .settings(dottySettings)

lazy val $name;format="space,camel"$Native = $name;format="space,camel"$.native
  .settings(nativeSettings)

lazy val docs = project
  .in(file("$name;format="norm"$-docs"))
  .settings(stdSettings("$name;format="space,hyphen,lower"$"))
  .settings(
    publish / skip := true,
    moduleName := "$name;format="norm"$-docs",
    scalacOptions -= "-Yno-imports",
    scalacOptions -= "-Xfatal-warnings",
    ScalaUnidoc / unidoc / unidocProjectFilter := inProjects($name;format="space,camel"$JVM),
    ScalaUnidoc / unidoc / target := (baseDirectory in LocalRootProject).value / "website" / "static" / "api",
    cleanFiles += (ScalaUnidoc / unidoc / target).value,
    docusaurusCreateSite := docusaurusCreateSite.dependsOn(Compile / unidoc).value,
    docusaurusPublishGhpages := docusaurusPublishGhpages.dependsOn(Compile / unidoc).value
  )
  .dependsOn($name;format="space,camel"$JVM)
  .enablePlugins(MdocPlugin, DocusaurusPlugin, ScalaUnidocPlugin)
