package SubProject.buildTypes

import SubProject.vcsRoots.GradleConnectionRoot
import SubProject.vcsRoots.MavenUnbalancedRoot
import jetbrains.buildServer.configs.kotlin.DslContext
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.pipelines.Pipeline
import jetbrains.buildServer.configs.kotlin.triggers.ScheduleTrigger
import jetbrains.buildServer.configs.kotlin.triggers.schedule
import jetbrains.buildServer.configs.kotlin.triggers.vcs



object GradlePipeline : Pipeline({
    name = "Gradle Pipeline"

    val parameterName = DslContext.getParameter("parameter_name")
    repositories {
        main(GradleConnectionRoot)
        additional(MavenUnbalancedRoot)
    }

    params {
        text("parameter", parameterName)
    }

    triggers {
        vcs { }
        /*schedule {
            schedulingPolicy = weekly {
                dayOfWeek = ScheduleTrigger.DAY.Monday
                hour = 12
                timezone = "Europe/Amsterdam"
            }
            triggerBuild = always()
        }*/
    }
    job {
        id = "GradleTests"
        name = "Gradle Tests"
        repositories {
            GradleConnectionRoot
        }
        steps {
            gradle {
                tasks = "clean test"
                enableDebug = true
                enableStacktrace = true
            }
        }
    }
})