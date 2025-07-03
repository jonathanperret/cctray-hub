package name.hennr.cctrayhub.cctray.controller

import mu.KotlinLogging
import name.hennr.cctrayhub.github.client.GithubClient
import name.hennr.cctrayhub.github.dto.GithubResponseCode.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class CctrayService(val githubClient: GithubClient) {

    val logger = KotlinLogging.logger { }

    fun getLatestWorkflowRun(
        githubGroup: String,
        githubRepo: String,
        githubWorkflowNameOrId: String
    ): Mono<String> {
      return getLatestWorkflowRun(githubGroup, githubRepo, githubWorkflowNameOrId, "main")
    }

    fun getLatestWorkflowRun(
        githubGroup: String,
        githubRepo: String,
        githubWorkflowNameOrId: String,
        branch: String
    ): Mono<String> {
        logger.debug("requested repo: $githubGroup/$githubRepo/$githubWorkflowNameOrId branch: $branch")
        return githubClient.getWorkflowRuns(githubGroup, githubRepo, githubWorkflowNameOrId, branch).map {
            // (cached) success http code from github && at least one existent run for the requested branch (already)
            if ((it.githubResponseCode == SUCCESS || it.githubResponseCode == CACHED) && it.latestWorkflowRun != null) {
                it.latestWorkflowRun.let { githubWorkflowRun ->
                    """<Projects> <Project name="${githubWorkflowRun.repository.name} - $githubWorkflowNameOrId - $branch" activity="${translateGithubStatusToCctrayActivity(githubWorkflowRun.status)}" lastBuildLabel="${githubWorkflowRun.run_number}" lastBuildStatus="${translateGithubConclusionToCctrayBuildStatus(githubWorkflowRun.conclusion)}" lastBuildTime="${githubWorkflowRun.created_at}" webUrl="${githubWorkflowRun.html_url}" /> </Projects>""".trimIndent()
                }
            } else if (it.githubResponseCode == NOT_FOUND) {
                    """<Projects> <Project name="$githubGroup/$githubRepo - $githubWorkflowNameOrId - $branch" activity="Sleeping" lastBuildLabel="0" lastBuildStatus="Failure" lastBuildTime="${Date()}" webUrl="https://github.com/$githubGroup/$githubRepo/actions/workflows/$githubWorkflowNameOrId?query=branch%3A$branch" /> </Projects>""".trimIndent()
            } else {
                """<Projects> <Project name="$githubRepo - $githubWorkflowNameOrId - $branch" activity="${translateGithubStatusToCctrayActivity("unknown")}" lastBuildLabel="0" lastBuildStatus="${translateGithubConclusionToCctrayBuildStatus("unknown")}" lastBuildTime="${Date()}" webUrl="https://github.com/$githubGroup/$githubRepo/actions/workflows/$githubWorkflowNameOrId?query=branch%3A$branch" /> </Projects>""".trimIndent()
            }
        }
    }

    fun translateGithubStatusToCctrayActivity(githubStatus: String): String {
        return when (githubStatus) {
            "queued" -> "Building"
            "in_progress" -> "Building"
            "completed" -> "Sleeping"
            else -> "CheckingModifications"
        }
    }

    fun translateGithubConclusionToCctrayBuildStatus(githubConclusion: String?): String {
        return when (githubConclusion) {
            "success" -> "Success"
            "failure" -> "Failure"
            "cancelled" -> "Exception"
            "timed_out" -> "Failure"
            else -> "Unknown"
        }
    }
}
