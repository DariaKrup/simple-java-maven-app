package _Self

import _Self.pipelines.*
import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({

    pipeline(DSLMilestone3WithConvertation_PipelineWithMultipleJobs_AppPipeline)
    pipeline(SimpleJavaMavenAppPipeline)
})
