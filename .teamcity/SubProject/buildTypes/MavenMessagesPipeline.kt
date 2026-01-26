package SubProject.buildTypes

import SubProject.vcsRoots.MavenUnbalancedRoot
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.pipelines.Pipeline
import jetbrains.buildServer.configs.kotlin.projectFeatures.dockerRegistry
import jetbrains.buildServer.configs.kotlin.triggers.schedule


object MavenMessagesPipeline : Pipeline ({
    name = "Maven: Messages Pipeline"

    repositories {
        main(MavenUnbalancedRoot)
    }

    triggers {
        schedule {
            schedulingPolicy = daily {
                hour = 15
                minute = 25
                timezone = "Europe/Amsterdam"
            }
            triggerBuild = always()
        }
    }

    job {
        id = "MavenJob"
        name = "Maven Job: parallel tests"
        steps {
            maven {
                name = "Test and deploy"
                goals = "clean test deploy -DaltDeploymentRepository=local-repo::file://local-repo"
                mavenVersion = defaultProvidedVersion()

                dockerImage = "maven:latest"
                dockerImagePlatform = MavenBuildStep.ImagePlatform.Linux
                dockerPull = true
                dockerRunParameters = "-e ENV=test"

                pomLocation = "pom.xml"
                runnerArgs = "-Dmaven.test.skip=true"
            }
        }
        parallelism = 2
        allowReuse = false
    }
    job {
        id = "ScriptInAzure"
        name = "Script with Azure Registry"
        steps {
            script {
                scriptContent = "echo 'Success'"
                dockerImage = "dkrupkinacontainerregistry.azurecr.io/app:latest"
            }
            script {
                scriptContent = "echo %remote_docker_password% %project_parameter% >> file.txt"
            }
        }
        integration("Docker", "AzureRegistry")
        integration("aws", "DockerRegistry")
        allowReuse = false
        filePublication("file.txt", publishArtifact = true, shareWithJobs = true)
    }
    importParameters {
        param("docker_io_password")
        param("remote_docker_password")
        param("project_parameter")
    }
    integrations {
        dockerRegistry {
            id = "DockerRegistry"
            userName = "dariakrup"
            password = "%docker_io_password%"
        }
        dockerRegistry {
            id = "AzureRegistry"
            url = "https://dkrupkinacontainerregistry.azurecr.io"
            userName = "17c5afa7-698f-4afd-b518-0db1fb4f0984"
            password = "credentialsJSON:e705338c-c9c9-42d4-a4bc-be4160bb969c"
        }
    }

})

object BuildMaven : BuildType({
    name = "Build Maven"

    steps {
        callMaven{
            name = "Build Maven"
            goals = "clean test deploy -DaltDeploymentRepository=local-repo::file://local-repo"
        }
    }
})

