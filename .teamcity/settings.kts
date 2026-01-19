import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.pipelines.Pipeline
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2025.11"

project {
    pipeline(PipelineBuilder)
    subProject(SubProject.Project)
}


object PipelineBuilder : Pipeline ({
    id("PipelineBuilder")
    name = "Pipeline Builder"


    repositories {
        main(DslContext. settingsRoot)
    }

    triggers {
        vcs {
            branchFilter = "+:"
        }
    }

    job {
        id = "TestJob"
        name = "Test Maven"

        steps{
            maven {
                id = "MavenStep"
                goals = "clean test"
                runnerArgs = "-Dmaven.test.failure.ignore=true"
                mavenVersion = defaultProvidedVersion()
                jdkHome = "%env.JDK_21_0%"
            }
            script {
                id = "CmdStep"
                scriptContent = "echo 'Successful tests!'"
            }
        }
        outputParam("tests_agent_OS", "%teamcity.agent.jvm.os.arch%")
    }
    job {
        id = "BuildJob"
        name = "Build Maven"
        steps {
            maven {
                id = "MavenBuild"
                goals = "clean package"
                runnerArgs = "-Dmaven.test.failure.ignore=true"
                mavenVersion = defaultProvidedVersion()
                jdkHome = "%env.JDK_21_0%"
            }
            script {
                id = "ParameterSharing"
                scriptContent = "echo %job_param%, %job.TestJob.tests_agent_OS%"
            }
        }
        dependency("TestJob")
        params {
            text("job_param", "job")
        }
        filePublication("target/*.jar")
    }
})