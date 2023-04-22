logLevel := Level.Warn

/* Quality control */
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

/* Publishing */
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.5.10")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")
