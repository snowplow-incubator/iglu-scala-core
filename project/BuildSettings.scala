/*
 * Copyright (c) 2016-2019 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
import bintray.BintrayPlugin._
import bintray.BintrayKeys._
import sbt._
import Keys._
import com.typesafe.tools.mima.plugin.MimaKeys._
import com.typesafe.tools.mima.plugin.MimaPlugin
import scoverage.ScoverageKeys._
import com.typesafe.sbt.sbtghpages.GhpagesPlugin.autoImport._
import com.typesafe.sbt.site.SitePlugin.autoImport._
import com.typesafe.sbt.SbtGit.GitKeys.{gitBranch, gitRemoteRepo}
import sbtunidoc.ScalaUnidocPlugin.autoImport._
import com.typesafe.sbt.site.preprocess.PreprocessPlugin.autoImport._

object BuildSettings {

  // Basic settings common for Iglu project and all its subprojects
  lazy val commonSettings = Seq[Setting[_]](
    organization                        := "com.snowplowanalytics",
    version                             := "0.5.1",
    scalaVersion                        := "2.13.1",
    crossScalaVersions                  := Seq("2.12.11", "2.13.1"),
    scalacOptions                       := compilerFlags.vallue,
    scalacOptions in (Compile, console) --=
      Seq("-Ywarn-unused:imports", "-Xfatal-warnings")
  )

  // Basic settings only for Iglu Core
  lazy val igluCoreBuildSettings = commonSettings ++ Seq[Setting[_]](
    description        := "Core entities for Iglu"
  )

  // Publish settings
  lazy val publishSettings = bintraySettings ++ Seq[Setting[_]](
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    bintrayOrganization := Some("snowplow"),
    bintrayRepository := "snowplow-maven"
  )

  // Maven Central publishing settings
  lazy val mavenCentralExtras = Seq[Setting[_]](
    pomIncludeRepository := { x => false },
    homepage := Some(url("http://snowplowanalytics.com")),
    scmInfo := Some(ScmInfo(url("https://github.com/snowplow-incubator/iglu-scala-core"), "scm:git@github.com:snowplow-incubator/iglu-scala-core.git")),
    pomExtra := (
      <developers>
        <developer>
          <name>Snowplow Analytics Ltd</name>
          <email>support@snowplowanalytics.com</email>
          <organization>Snowplow Analytics Ltd</organization>
          <organizationUrl>http://snowplowanalytics.com</organizationUrl>
        </developer>
      </developers>)
  )

  // All recommended compiler flags
  lazy val allScalacFlags = Seq(
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8",                // Specify character encoding used by source files.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:implicitConversions",     // Allow definition of implicit functions called views
    "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
    "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
    "-Xfuture",                          // Turn on future language features.
    "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
    "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
    "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
    "-Xlint:option-implicit",            // Option.apply used implicit view.
    "-Xlint:package-object-classes",     // Class or object defined in package object.
    "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match",              // Pattern match may not be typesafe.
    "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification",             // Enable partial unification in type constructor inference
    "-Ywarn-dead-code",                  // Warn when dead code is identified.
    "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen",              // Warn when numerics are widened.
    "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals",              // Warn if a local definition is unused.
    "-Ywarn-unused:params",              // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates",            // Warn if a private member is unused.
    "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
  )

  // All recommended compiler flags, working on particular version of Scalac
  lazy val compilerFlags = {
    scalaVersion.map { version =>
      if (version.startsWith("2.13."))
        allScalacFlags.diff(Seq(
          "-Xlint:by-name-right-associative", // not available
          "-Xlint:unsound-match", // not available
          "-Yno-adapted-args", // not available. Can be returned in future https://github.com/scala/bug/issues/11110
          "-Ypartial-unification", // enabled by default
          "-Ywarn-inaccessible", // not available. the same as -Xlint:inaccessible
          "-Ywarn-infer-any", // not available. The same as -Xlint:infer-any
          "-Ywarn-nullary-override", // not available. The same as -Xlint:nullary-override
          "-Ywarn-nullary-unit", // not available. The same as -Xlint:nullary-unit
          "-Xfuture",   // not available
          "-Ywarn-unused:imports"         // cats.syntax.either._
        ))
      else allScalacFlags
    }
  }

  lazy val buildSettings = igluCoreBuildSettings ++ publishSettings ++ mavenCentralExtras

  // If new version introduces breaking changes,
  // clear-out mimaBinaryIssueFilters and mimaPreviousVersions.
  // Otherwise, add previous version to set without
  // removing other versions.
  val mimaPreviousVersions = Set()

  val mimaSettings = Seq(
    mimaPreviousArtifacts := mimaPreviousVersions.map { organization.value %% name.value % _ },
    mimaBinaryIssueFilters ++= Seq(),
    test in Test := {
      mimaReportBinaryIssues.value
      (test in Test).value
    }
  )

  val scoverageSettings = Seq(
    coverageMinimum := 50,
    coverageFailOnMinimum := true,
    coverageHighlighting := false,
    (test in Test) := {
      (coverageReport dependsOn (test in Test)).value
    }
  )

  val ghPagesSettings = Seq(
    ghpagesPushSite := (ghpagesPushSite dependsOn makeSite).value,
    ghpagesNoJekyll := false,
    gitRemoteRepo := "git@github.com:snowplow-incubator/iglu-scala-core.git",
    gitBranch := Some("gh-pages"),
    siteSubdirName in ScalaUnidoc := version.value,
    preprocessVars in Preprocess := Map("VERSION" -> version.value),
    addMappingsToSiteDir(mappings in (ScalaUnidoc, packageDoc), siteSubdirName in ScalaUnidoc),
    excludeFilter in ghpagesCleanSite := new FileFilter {
      def accept(f: File) = true
    }
  )
}
