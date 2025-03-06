# Dangerfile.df.kts

To get started create a new `Dangerfile.df.kts` script in the root of your project. In this script file you can use the PR / Git metadata to automate messaging, checks, and other aspects of your engineering culture.

```kotlin
import systems.danger.kotlin.*

danger(args) {

    val allSourceFiles = git.modifiedFiles + git.createdFiles
    val changelogChanged = allSourceFiles.contains("CHANGELOG.md")
    val sourceChanges = allSourceFiles.firstOrNull { it.contains("src") }

    onGitHub {
        val isTrivial = pullRequest.title.contains("#trivial")

        // Changelog
        if (!isTrivial && !changelogChanged && sourceChanges != null) {
            warn(WordUtils.capitalize("any changes to library code should be reflected in the Changelog.\n\nPlease consider adding a note there and adhere to the [Changelog Guidelines](https://github.com/Moya/contributors/blob/master/Changelog%20Guidelines.md)."))
        }

        // Big PR Check
        if ((pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0) > 300) {
            warn("Big PR, try to keep changes smaller if you can")
        }

        // Work in progress check
        if (pullRequest.title.contains("WIP", false)) {
            warn("PR is classed as Work in Progress")
        }
    }
}
```

Check out the docs on the Danger DSL here: _TBD_

## GitHub

You can access the pull request information from GitHub by using

```kotlin
danger(args) {
  github.pullRequest.//...
  github.reviews.//...
}
```

or use the `onGithub { … }` to conditionally run checks ONLY on GitHub if you support multiple CI providers

Additionally, Danger Kotlin also provides a hydrated GitHub API client via Spotify's [github-java-client][] library. It uses the same token/url environment variables as the underlying danger-js implementation:

```shell
DANGER_GITHUB_API_TOKEN=...
DANGER_GITHUB_API_BASE_URL=...
```

You can access this by calling the extension method

```kotlin
danger(args) {
  github.api.//...
}
```

[github-java-client]: https://github.com/spotify/github-java-client
