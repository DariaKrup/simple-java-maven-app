package SubProject

import SubProject.buildTypes.*
import SubProject.vcsRoots.GradleConnectionRoot
import SubProject.vcsRoots.JSConnectionRoot
import SubProject.vcsRoots.MavenUnbalancedRoot
import SubProject.vcsRoots.NetVcsRoot
import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.projectFeatures.githubConnection
import jetbrains.buildServer.configs.kotlin.projectFeatures.hashiCorpVaultConnection
import jetbrains.buildServer.configs.kotlin.remoteParameters.hashiCorpVaultParameter

object Project : Project({
    id("ProjectWithPipelinesAndBuilds")
    name = "Project with pipelines and build configurations"

    vcsRoot(MavenUnbalancedRoot)
    vcsRoot(NetVcsRoot)
    vcsRoot(GradleConnectionRoot)
    vcsRoot(JSConnectionRoot)


    features {
        hashiCorpVaultConnection {
            id = "VaultConnection"
            name = "HashiCorp Vault"

            url = "https://localhost:8200"
            authMethod = appRole{
                roleId = "e0d9ef3e-a837-c70c-ea96-46e9870e6567"
                secretId = "credentialsJSON:48cd3827-a9c5-420c-ab72-3957ed2da18a"
            }
        }
    }

    params {
        param("project_parameter", "text")
        hashiCorpVaultParameter {
            vaultId = "VaultConnection"
            name = "remote_docker_password"
            query = "passwords_storage_v1/docker!/password"
        }
        password("docker_io_password","credentialsJSON:82cbcea7-18a1-4a18-9e08-c383d88d5f4f")
    }

    buildType(BuildConfigurationPublish)
    pipeline(MavenMessagesPipeline)
    pipeline(GradlePipeline)
    pipeline(NetTestsPipeline)
    pipeline(NodeJSPipeline)
})