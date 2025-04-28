package systems.danger.kotlin.tools.github

import com.spotify.github.v3.clients.GitHubClient
import java.net.URI

interface GithubClientFactory {

  fun create(): GitHubClient

  companion object {
    /** Override this to provide your own instantiation of the [GitHubClient] */
    var instance: GithubClientFactory = DefaultGithubClientFactory
  }
}

object DefaultGithubClientFactory : GithubClientFactory {

  private const val DEFAULT_GITHUB_URL = "https://api.github.com"
  private const val DEFAULT_GITHUB_GRAPHQL_URL = "https://api.github.com/graphql"

  override fun create(): GitHubClient {
    return GitHubClient.create(getBaseUrl(), getGraphqlUrl(), getToken())
  }

  private fun getToken(): String {
    return System.getenv("DANGER_GITHUB_API_TOKEN")
      ?: System.getenv("GITHUB_TOKEN")
      ?: error(
        "Unable to find GitHub API token. Please set the envvar 'DANGER_GITHUB_API_TOKEN' or 'GITHUB_TOKEN'"
      )
  }

  private fun getBaseUrl(): URI {
    return URI.create(System.getenv("DANGER_GITHUB_API_BASE_URL") ?: DEFAULT_GITHUB_URL)
  }

  private fun getGraphqlUrl(): URI {
    return URI.create(System.getenv("DANGER_GITHUB_GRAPHQL_BASE_URL") ?: DEFAULT_GITHUB_GRAPHQL_URL)
  }
}
