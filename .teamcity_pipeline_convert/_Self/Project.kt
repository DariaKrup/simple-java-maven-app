package _Self

import _Self.buildTypes.*
import _Self.pipelines.*
import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({

    buildType(BuildMavenApp)

    pipeline(SimpleJavaMavenAppPipeline)
})
