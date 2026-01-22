package SubProject.buildTypes

import SubProject.vcsRoots.JSConnectionRoot
import jetbrains.buildServer.configs.kotlin.buildSteps.nodeJS
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.pipelines.Pipeline
import jetbrains.buildServer.configs.kotlin.triggers.vcs

object NodeJSPipeline : Pipeline({
    name = "NodeJSPipeline"

    repositories {
        main(JSConnectionRoot)
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }

    job {
        name = "NodeJS Job"
        id = "NodeJSJob"

        params {
            param("app_name", "app.js")
        }

        steps {
            script {
                name = "NPM install"
                scriptContent = "npm install"

                dockerImage = "node:22.14.0"
            }

            nodeJS {
                name = "NPM run"

                shellScript = """
                                npm ci
                                node %app_name%
                            """.trimIndent()

                dockerImage = "node:22.14.0"
            }
        }
        allowReuse = false
    }
})