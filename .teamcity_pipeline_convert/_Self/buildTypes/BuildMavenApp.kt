package _Self.buildTypes

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.triggers.vcs

object BuildMavenApp : BuildType({
    name = "Build: Maven app"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            id = "Maven2"
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            mavenVersion = bundled_3_9()
            jdkHome = "%env.JDK_21_0%"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:<default>"
        }
    }
})
