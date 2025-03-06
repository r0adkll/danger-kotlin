package com.r0adkll.danger.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level.PROJECT
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.openapi.vfs.LocalFileSystem
import git4idea.GitBranch
import git4idea.GitDisposable
import git4idea.GitRemoteBranch
import git4idea.GitUtil
import git4idea.commands.Git
import git4idea.repo.GitRepoInfo
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryStateChangeListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun Project.gitService(): GitService = service()

@Service(PROJECT)
class GitService(
  private val project: Project,
  private val scope: CoroutineScope,
) {

  var gitRepository: GitRepository? = null
  var gitState: GitState? = null

  private val logger = thisLogger()

  init {
    project.messageBus.connect(GitDisposable.getInstance(project))
      .subscribe(GitRepository.GIT_REPO_STATE_CHANGE, object : GitRepositoryStateChangeListener {
        override fun repositoryChanged(repository: GitRepository, previousInfo: GitRepoInfo, info: GitRepoInfo) {
          // Reload local git information
          gitRepository = repository
          gitState = calculateGitState(repository)

          scope.launch(Dispatchers.IO) {
            // Reload/Load pull request URL
            project.gitHubService().findCurrentPullRequestUrl()
          }
        }
      })
  }

  fun currentGitRepository(force: Boolean = false): GitRepository? {
    if (gitRepository != null && !force) return gitRepository

    // TODO: This seems extra clunky. Is there a better way to do this?
    val projectRoot =
      project.basePath?.toNioPathOrNull()?.let {
        LocalFileSystem.getInstance().findFileByNioFile(it)
      } ?: error("Unable to load project root directory information")

    return GitUtil.getRepositoryForRoot(project, projectRoot).also { gitRepository = it }
  }

  suspend fun load(): GitState? =
    withContext(Dispatchers.IO) {
      gitRepository = currentGitRepository(force = true)
      gitRepository?.let { repo ->
        calculateGitState(repo)
          .also { gitState = it }
      }
    }

  private fun calculateGitState(repo: GitRepository): GitState{
    val trackedBranch = repo.currentBranch?.findTrackedBranch(repo)
    val hasStagedChanges = !repo.stagingAreaHolder.isEmpty
    val baseBranch =
      repo.branches.localBranches.firstOrNull { it.name in DEFAULT_BASE_BRANCHES }
        ?: repo.branches.remoteBranches.firstOrNull { it.name in DEFAULT_BASE_BRANCHES }
    val isBaseBranch = repo.currentBranchName in DEFAULT_BASE_BRANCHES
    val hasBaseBranch = repo.branches.localBranches.any { it.name in DEFAULT_BASE_BRANCHES }

    val diff =
      if (!isBaseBranch && hasBaseBranch) {
        val baseBranch =
          repo.branches.localBranches.first { it.name in DEFAULT_BASE_BRANCHES }!!
        GitUtil.getPathsDiffBetweenRefs(
          Git.getInstance(),
          repo,
          baseBranch.fullName,
          repo.currentBranch!!.fullName,
        )
      } else emptyList()

    val hasDiffWithBase = diff.isNotEmpty()

    logger.debug(
      """
        GitRepository(
          currentBranch = {
            rev = ${repo.currentRevision},
            ref = ${repo.currentBranchName},
            hasStagedChanges = $hasStagedChanges,
            isBaseBranch = $isBaseBranch,
          },
          trackedBranch = ${trackedBranch?.name} @ ${trackedBranch?.remote?.firstUrl},
          localBranches = [
            ${repo.branches.localBranches.joinToString { it.name }}
          ],
          hasBaseBranch = $hasBaseBranch,
          remoteBranches = [
            ${repo.branches.remoteBranches.joinToString { it.name }}
          ],
        )
      """
        .trimIndent()
    )

    return GitState(
      repo = repo,
      trackedBranch = trackedBranch,
      baseBranch = baseBranch,
      hasStagedChanges = hasStagedChanges,
      isBaseBranch = isBaseBranch,
      hasBaseBranch = hasBaseBranch,
      hasDiffWithBase = hasDiffWithBase,
      diffs = diff.toList(),
    )
  }
}

data class GitState(
  val repo: GitRepository,
  val trackedBranch: GitRemoteBranch?,
  val baseBranch: GitBranch?,
  val hasStagedChanges: Boolean,
  val isBaseBranch: Boolean,
  val hasBaseBranch: Boolean,
  val hasDiffWithBase: Boolean,
  val diffs: List<String>,
)

private val DEFAULT_BASE_BRANCHES = arrayOf("main", "master", "develop")
