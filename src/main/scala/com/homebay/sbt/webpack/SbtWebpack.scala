package com.homebay.sbt.stylus

import sbt._
import sbt.Keys._
import com.typesafe.sbt.web._
import com.typesafe.sbt.jse.SbtJsTask
import spray.json._

object Import {

  object WebpackKeys {
    val webpack = TaskKey[Seq[File]]("webpack", "Invoke webpack.")
  }
}

object SbtWebpack extends AutoPlugin {

  override def requires = SbtJsTask

  override def trigger = AllRequirements

  val autoImport = Import

  import SbtWeb.autoImport._
  import WebKeys._
  import SbtJsTask.autoImport.JsTaskKeys._
  import autoImport.WebpackKeys._

  override def projectSettings = {

    inTask(webpack)(SbtJsTask.jsTaskSpecificUnscopedSettings ++
      Seq(
        moduleName := "webpack",
        shellFile := getClass.getClassLoader.getResource("webpack-shell.js"),
        includeFilter in Assets := "*.js" || "*.jsx",
        includeFilter in TestAssets := (jsFilter in TestAssets).value,
        taskMessage in Assets := "Webpack running",
        taskMessage in TestAssets := "Webpack test running"
      )
    ) ++ SbtJsTask.addJsSourceFileTasks(webpack)
  }

}
