package com.homebay.sbt.webpack

import com.typesafe.sbt.jse.SbtJsTask
import com.typesafe.sbt.web._
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt.Keys._
import sbt._

object Import {

  val webpack = TaskKey[Seq[File]]("webpack", "Run the webpack module bundler.")

  object WebpackKeys {

    //val sourceDir = SettingKey[File]("webpack-source-dir", "The top level directory that contains your app js files. This is the source folder that webpack reads from.")
    //val buildDir = SettingKey[File]("webpack-build-dir", "Where webpack should will write to.")
  }
}

object SbtWebpack extends AutoPlugin {

  override def requires = SbtJsTask

  override def trigger = AllRequirements

  val autoImport = Import

  import com.homebay.sbt.webpack.Import._
  import com.homebay.sbt.webpack.Import.WebpackKeys._
  import com.typesafe.sbt.jse.SbtJsEngine.autoImport.JsEngineKeys._
  import com.typesafe.sbt.jse.SbtJsTask.autoImport.JsTaskKeys._
  import com.typesafe.sbt.web.Import.WebKeys._
  import com.typesafe.sbt.web.SbtWeb.autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    includeFilter in webpack := "*.js" || "*.jsx",
    (nodeModuleDirectories in webpack in Plugin) += baseDirectory.value / "node_modules",
    webpack in Assets := runWebpack(Assets).dependsOn(webJarsNodeModules in Plugin).value,
    webpack in TestAssets := runWebpack(TestAssets).dependsOn(webJarsNodeModules in Plugin).value,
    resourceGenerators in Assets <+= webpack in Assets,
    resourceGenerators in TestAssets <+= webpack in TestAssets,
    resourceManaged in webpack in Assets := webTarget.value / webpack.key.label / "js-built",
    resourceManaged in webpack in TestAssets := webTarget.value / webpack.key.label / "test-js-built"
  )

  private def runWebpack(config: Configuration): Def.Initialize[Task[Seq[File]]] = Def.task {

    val sourceDir = (sourceDirectory in webpack in config).value
    val outputDir = (resourceManaged in webpack in config).value

    val inputFiles = (sourceDir ** (includeFilter in webpack).value).get

    // FIXME support all of the arguments mentioned here: http://webpack.github.io/docs/cli.html
    val args = Seq("--output-path", outputDir.getAbsolutePath)

    // Running webpack as a node module for now
    val nodeModulePaths: Seq[String] = (nodeModuleDirectories in webpack in Plugin).value.map(_.getPath)

    // TODO Currently can't use the webjar because there are a ton of transitive dependency issues. See
    // https://groups.google.com/forum/#!topic/play-framework/m2X8NQFk5bk for a discussion on the topic
    //val webpackExecutable = (webJarsNodeModulesDirectory in Plugin).value / "webpack" / "bin" / "webpack.js"
    val webpackExecutable = baseDirectory.value / "node_modules" / "webpack" / "bin" / "webpack.js"

    streams.value.log.info(s"Optimizing ${inputFiles.size} Javascript file(s) with Webpack")

    try {

      SbtJsTask.executeJs(
        state.value,
        (engineType in webpack).value,
        (command in webpack).value,
        nodeModulePaths,
        webpackExecutable,
        args,
        (timeoutPerSource in webpack).value * inputFiles.size
      )
    } catch {

      // FIXME get error handling to work
      case failure: SbtJsTask.JsTaskFailure =>
        failure.printStackTrace()
        //CompileProblems.report(reporter.value, problems)
    }

    outputDir.***.get
  }

}
