import sbt.Keys._

name := "tranzactio"

inThisBuild(
  List(
    organization := "st.alzo",
    scalaVersion := "3.2.2",
    description  := "ZIO wrapper for Doobie for Scala 3",
    licenses     := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),
    developers := List(
      Developer(
        "gaelrenoux",
        "Gaël Renoux",
        "gael.renoux@gmail.com",
        url("https://github.com/gaelrenoux")
      ),
      Developer("alzo", "Sergey Rublev", "alzo@alzo.space", url("https://github.com/narma/"))
    ),
    scmInfo := Some(
      ScmInfo(url("https://github.com/narma/tranzactio"), "git@github.com:narma/tranzactio.git")
    ),
    homepage               := Some(url("https://github.com/narma/tranzactio")),
    sonatypeCredentialHost := "s01.oss.sonatype.org"
  )
)

scalacOptions ++= Seq(
  "-encoding",
  "utf-8",        // Specify character encoding used by source files.
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-unchecked",   // Enable additional warnings where generated code depends on assumptions.

  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-source:3.0-migration",
  "-rewrite",
  "-explain",
  "-explain-types"
)

val ZioVersion     = "2.0.13"
val ZioCatsVersion = "23.0.0.4"
val DoobieVersion  = "1.0.0-RC2"
val izumiReflect   = "2.3.2"
val H2Version      = "1.4.200"

libraryDependencies ++= Seq(
  /* ZIO */
  "dev.zio" %% "zio"              % ZioVersion,
  "dev.zio" %% "zio-streams"      % ZioVersion,
  "dev.zio" %% "zio-interop-cats" % ZioCatsVersion,
  "dev.zio" %% "izumi-reflect"    % izumiReflect,

  /* Doobie */
  "org.tpolecat" %% "doobie-core" % DoobieVersion % "optional",

  /* ZIO test */
  "dev.zio" %% "zio-test"          % ZioVersion % "test",
  "dev.zio" %% "zio-test-sbt"      % ZioVersion % "test",
  "dev.zio" %% "zio-test-magnolia" % ZioVersion % "test",

  /* H2 for tests */
  "com.h2database" % "h2" % H2Version % "test"
)

Test / fork               := true
Test / testForkedParallel := true // run tests in parallel on the forked JVM
Test / testOptions += Tests.Argument("-oD") // show test duration

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

/* Adds samples as test sources */
Test / unmanagedSourceDirectories ++= Seq(
  new File("src/samples/scala")
)

/* Makes processes is SBT cancelable without closing SBT */
Global / cancelable := true
