import sbt.Keys._

name := "tranzactio"

inThisBuild(
  List(
    organization := "st.alzo",
    scalaVersion := "2.13.10",
    description := "ZIO wrapper for Doobie",
    licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))),
    developers   := List(
      Developer("gaelrenoux", "Gaël Renoux", "gael.renoux@gmail.com", url("https://github.com/gaelrenoux")),
      Developer("alzo", "Sergey Rublev", "alzo@alzo.space", url("https://github.com/narma/"))
    ),
    scmInfo      := Some(ScmInfo(url("https://github.com/narma/tranzactio"), "git@github.com:narma/tranzactio.git")),
    homepage     := Some(url("https://github.com/narma/tranzactio")),
    sonatypeCredentialHost := "s01.oss.sonatype.org"
  )
)



val scala213Version = "2.13.10"
val scala3Version = "3.2.2"
val supportedScalaVersions = List(scala213Version, scala3Version)

scalaVersion := scala3Version
crossScalaVersions := supportedScalaVersions

val commonScalacOptions = Seq(
 "-encoding", "utf-8", // Specify character encoding used by source files.
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.

  "-feature", // Emit warning and location for usages of features that should be imported explicitly. 
)

val scala2Options = Seq(
  "-explaintypes", // Explain type errors in more detail.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views

  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.

  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", //  A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
)

val scala213Options = Seq(
  "-Ymacro-annotations",

  "-Wdead-code", // Warn when dead code is identified.
  "-Wextra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Wnumeric-widen", // Warn when numerics are widened.
  // "-Woctal-literal", // Warn on obsolete octal syntax. // false positive on 0 since Scala 2.13.2
  "-Wunused:explicits", // Warn if an explicit parameter is unused.
  "-Wunused:implicits", // Warn if an implicit parameter is unused.
  "-Wunused:imports", // Warn when imports are unused.
  "-Wunused:locals", // Warn if a local definition is unused.
  "-Wunused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Wunused:privates", // Warn if a private member is unused.
  "-Wvalue-discard", // Warn when non-Unit expression results are unused.

  "-Xlint:deprecation", // Enable linted deprecations.
  "-Xlint:eta-sam", // Warn on eta-expansion to meet a Java-defined functional interface that is not explicitly annotated with @FunctionalInterface.
  "-Xlint:eta-zero", // Warn on eta-expansion (rather than auto-application) of zero-ary method.
  "-Xlint:implicit-not-found", // Check @implicitNotFound and @implicitAmbiguous messages.
  // "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`. // Happens too much. See https://github.com/zio/zio/pull/6455.
  "-Xlint:nonlocal-return", // A return statement used an exception for flow control.
  "-Xlint:serial", // @SerialVersionUID on traits and non-serializable classes.
  "-Xlint:unused", // Enable -Ywarn-unused:imports,privates,locals,implicits.
  "-Xlint:valpattern", // Enable pattern checks in val definitions.
)

val scala3Options = Seq(
   "-source:3.0-migration",
    "-rewrite", 
    "-explain",
    "-explain-types"
)

scalacOptions ++= commonScalacOptions ++ {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 13)) => scala2Options ++ scala213Options
    case Some((3, _)) => scala3Options
    case _ => Nil
  }
}


val ZioVersion = "2.0.13"
val ZioCatsVersion = "23.0.0.4"
val DoobieVersion = "1.0.0-RC2"
val izumiReflect = "2.3.2"
val H2Version = "1.4.200"

libraryDependencies ++= Seq(
  /* ZIO */
  "dev.zio" %% "zio" % ZioVersion,
  "dev.zio" %% "zio-streams" % ZioVersion,
  "dev.zio" %% "zio-interop-cats" % ZioCatsVersion,
  "dev.zio" %% "izumi-reflect" % izumiReflect,

  /* Doobie */
  "org.tpolecat" %% "doobie-core" % DoobieVersion % "optional",

  /* ZIO test */
  "dev.zio" %% "zio-test" % ZioVersion % "test",
  "dev.zio" %% "zio-test-sbt" % ZioVersion % "test",
  "dev.zio" %% "zio-test-magnolia" % ZioVersion % "test",

  /* H2 for tests */
  "com.h2database" % "h2" % H2Version % "test"
)


Test / fork := true
Test / testForkedParallel := true // run tests in parallel on the forked JVM
Test / testOptions += Tests.Argument("-oD") // show test duration

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

/* Adds samples as test sources */
Test / unmanagedSourceDirectories ++= Seq(
  new File("src/samples/scala")
)

/* Makes processes is SBT cancelable without closing SBT */
Global / cancelable := true
