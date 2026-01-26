package SubProject.buildTypes

import jetbrains.buildServer.configs.kotlin.BuildSteps
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.maven

object BuildConfigurationPublish : BuildType({
    name = "Maven Configuration to publish"

    val buildModeBuild = "build"

    AllVCSTrigger

    steps {
        callMaven {
            name = "Build and deploy"
            goals = "clean deploy -DaltDeploymentRepository=local-repo::file://local-repo"
            conditions {
                equals("mode", buildModeBuild)
            }
        }
    }

})

fun BuildSteps.callMaven(init: MavenBuildStep.() -> Unit = {}) {
    maven {
        mavenVersion = defaultProvidedVersion()
        runnerArgs =
            "-DskipTests --batch-mode"
        localRepoScope = MavenBuildStep.RepositoryScope.MAVEN_DEFAULT
        jdkHome = "%env.JDK_21_0%"
        init(this)
    }
}