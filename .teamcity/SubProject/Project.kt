package SubProject

import SubProject.buildTypes.BuildConfigurationPublish
import jetbrains.buildServer.configs.kotlin.Project

object Project : Project({
    id("ProjectWithPipelinesAndBuilds")
    name = "Project with pipelines and build configurations"

    buildType(BuildConfigurationPublish)
})