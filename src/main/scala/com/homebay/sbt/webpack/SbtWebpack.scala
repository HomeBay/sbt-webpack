package com.homebay.sbt.webpack

import com.typesafe.sbt.jse.SbtJsTask
import com.typesafe.sbt.web._
import com.typesafe.sbt.web.pipeline.Pipeline
import sbt.Keys._
import sbt._

object Import {

  object WebpackKeys {
    val webpack = TaskKey[Pipeline.Stage]("webpack", "Invoke webpack on the asset pipeline.")
    val appDir = SettingKey[File]("webpack-app-dir", "The top level directory that contains your app js files. In effect, this is the source folder that webpack reads from.")
    val buildDir = SettingKey[File]("webpack-build-dir", "Where webpack should will write to.")
  }
}

object SbtWebpack extends AutoPlugin {

  override def requires = SbtJsTask

  override def trigger = AllRequirements

  val autoImport = Import

  import com.homebay.sbt.webpack.Import.WebpackKeys._
  import com.typesafe.sbt.jse.SbtJsEngine.autoImport.JsEngineKeys._
  import com.typesafe.sbt.jse.SbtJsTask.autoImport.JsTaskKeys._
  import com.typesafe.sbt.web.Import.WebKeys._
  import com.typesafe.sbt.web.SbtWeb.autoImport._

  override def projectSettings: Seq[Setting[_]] = Seq(
    appDir := (resourceManaged in webpack).value / "appdir",
    buildDir := (resourceManaged in webpack).value / "js-built",
    includeFilter in webpack := "*.js" || "*.jsx",
    deduplicators += SbtWeb.selectFileFrom((target in webpack).value),
    webpack := runWebpack.value
  )


  def runWebpack: Def.Initialize[Task[Pipeline.Stage]] = Def.task { mappings =>

    val include = (includeFilter in webpack).value

    val optimizerMappings = mappings.filter(f => !f._1.isDirectory && include.accept(f._1))

    SbtWeb.syncMappings(streams.value.cacheDirectory, optimizerMappings, appDir.value)

    val cacheDirectory = streams.value.cacheDirectory / webpack.key.label
    val nodeModulePaths = (nodeModuleDirectories in Plugin).value.map(_.getPath)
    val webpackExecutable = (webJarsNodeModulesDirectory in Plugin).value / "webpack" / "bin" / "webpack.js"
    val args = Seq("--output-path", buildDir.value.getAbsolutePath)

    val runUpdate = FileFunction.cached(cacheDirectory, FilesInfo.hash) { _ =>

      streams.value.log.info(s"Optimizing ${optimizerMappings.size} Javascript file(s) with Webpack")

      SbtJsTask.executeJs(
        state.value,
        (engineType in webpack).value,
        (command in webpack).value,
        nodeModulePaths,
        webpackExecutable,
        args,
        (timeoutPerSource in webpack).value * optimizerMappings.size
      )

      buildDir.value.***.get.toSet
    }

    val optimizedMappings = runUpdate(appDir.value.***.get.toSet).filter(_.isFile).pair(relativeTo(buildDir.value))
    (mappings.toSet -- optimizerMappings.toSet ++ optimizedMappings).toSeq
  }

}
