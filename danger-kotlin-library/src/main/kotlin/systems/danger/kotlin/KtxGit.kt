package systems.danger.kotlin

import systems.danger.kotlin.models.git.FilePath
import systems.danger.kotlin.models.git.Git
import systems.danger.kotlin.models.git.GitCommit
import systems.danger.kotlin.tools.shell.ShellExecutorFactory

// extensions over [Git] object

/** Changed lines in this PR */
val Git.changedLines: PullRequestChangedLines
  get() {
    if (headSha == null || baseSha == null) return PullRequestChangedLines(0, 0)
    val shellExecutor = ShellExecutorFactory.get()
    val commandRawOutput = shellExecutor.execute("git diff --numstat $baseSha $headSha")
    val additionDeletionPairs =
      commandRawOutput
        .lines()
        .filter { it.isNotEmpty() }
        .map { line ->
          val parts = line.split("\\s+".toRegex())
          (parts[0].toIntOrNull() ?: 0) to (parts[1].toIntOrNull() ?: 0)
        }
    val additions = additionDeletionPairs.fold(0) { acc, (addition, _) -> acc + addition }
    val deletions = additionDeletionPairs.fold(0) { acc, (_, deletion) -> acc + deletion }
    val commandRawDiffOutput = shellExecutor.execute("git diff $baseSha $headSha")
    return PullRequestChangedLines(additions, deletions, commandRawDiffOutput)
  }

/**
 * The changed lines for a specific file in this pr
 *
 * @param filePath the path to the file
 * @return the Diff for the file at [filePath]
 */
fun Git.diffForFile(filePath: FilePath): DiffForFile {
  if (headSha == null || baseSha == null) return DiffForFile(filePath)
  val shellExecutor = ShellExecutorFactory.get()
  val rawDiffOutput = shellExecutor.execute("git diff $baseSha $headSha -- $filePath")

  val additions = mutableListOf<String>()
  val deletions = mutableListOf<String>()

  var hasSeenChunkHeader = false
  for (line in rawDiffOutput.lines()) {
    if (line.startsWith("@@")) {
      hasSeenChunkHeader = true
    } else if (hasSeenChunkHeader) {
      if (line.startsWith("+")) {
        additions += line
      } else if (line.startsWith("-")) {
        deletions += line
      }
    }
  }

  return DiffForFile(filePath, additions, deletions)
}

/** Number of changed lines */
val Git.linesOfCode: Int
  get() = additions + deletions

/** Number of added lines */
val Git.additions: Int
  get() = changedLines.additions

/** Number of deleted lines */
val Git.deletions: Int
  get() = changedLines.deletions

/** Reference to a SHA of head commit of this PR */
val Git.headSha: String?
  get() = commits.sortChronologically().lastOrNull()?.sha

/** Reference to a SHA of base commit of this PR */
val Git.baseSha: String?
  get() = commits.sortChronologically().firstOrNull()?.sha?.let { "$it^1" }

/** Unified diff of this PR */
val Git.diff: String?
  get() = changedLines.diff

/**
 * Wrapper for number of additions and deletions in currently processed Pull (or Merge) Request
 *
 * @param additions the number of additions
 * @param deletions the number of deletions
 * @param diff unified diff of the pr
 * @constructor Create empty PullRequestChangedLines
 */
data class PullRequestChangedLines(
  val additions: Int,
  val deletions: Int,
  val diff: String? = null,
)

/**
 * Wrapper for added and deleted lines in the diff of a specific file
 *
 * @param file the file the diff is for
 * @param additions the lines added in this diff
 * @param deletions the lines deleted in this diff
 */
data class DiffForFile(
  val file: FilePath,
  val additions: List<String> = emptyList(),
  val deletions: List<String> = emptyList(),
)

private fun List<GitCommit>.sortChronologically(): List<GitCommit> {
  return sortedBy { it.author.date }
}
