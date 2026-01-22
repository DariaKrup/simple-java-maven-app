package SubProject.buildTypes

import SubProject.vcsRoots.NetVcsRoot
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.pipelines.Pipeline

object NetTestsPipeline : Pipeline ({
    name = ".NET Tests Pipeline"

    repositories {
        main(NetVcsRoot)
    }

    job {
        steps {
            nodeJS {

            }
        }
    }
})