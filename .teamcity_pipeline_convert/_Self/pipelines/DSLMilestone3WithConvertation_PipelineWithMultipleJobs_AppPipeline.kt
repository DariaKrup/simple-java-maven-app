package _Self.pipelines

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.pipelines.*

object DSLMilestone3WithConvertation_PipelineWithMultipleJobs_AppPipeline : Pipeline({
    id("AppPipeline")
    name = "App: pipeline"

    repositories {
        repository(DslContext.settingsRoot)
    }

    job(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_AppPipeline_Job1)
})

object DSLMilestone3WithConvertation_PipelineWithMultipleJobs_AppPipeline_Job1 : Job({
    id("Job1")
    name = "Job 1"
    allowReuse = true
    enableDependencyCacheOptimization = false

    steps {
        script {
            name = "CMD"
            scriptContent = "echo 'Cool pipeline'"
        }
    }
})
