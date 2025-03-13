package systems.danger.kts

import kotlin.script.experimental.dependencies.Repository

/**
 * This is a delegating Http Proxy dependency resolver. While a user might be editing a script
 * locally they will likely need to resolve dependencies from a maven repository using https schema.
 * However, when running this in CI they might be behind an HTTP proxy and can't make https
 * requests.
 *
 * This delegating resolver detects the system props `http_proxy` or `HTTP_PROXY` and swaps the
 * requesting schema out if it starts with `https`
 */
object RepositoryHttpProxyFilter {

  fun filterRepository(annotation: Repository): Repository {
    val httpProxy =
      System.getenv("http_proxy")
        ?: System.getenv("HTTP_PROXY")
        ?: System.getenv("ENABLE_DANGER_MAVEN_PROXY")
    if (httpProxy != null && annotation.repositoriesCoordinates.any { it.startsWith("https") }) {
      // We have entered a maven repository targeting https, but have a http proxy enabled
      // swap the scheme out so that the dependencies can resolve while under a proxy
      val newRepositoryCoordinates =
        annotation.repositoriesCoordinates.map { it.replace("https", "http") }
      return Repository(*newRepositoryCoordinates.toTypedArray(), options = annotation.options)
    } else {
      return annotation
    }
  }
}
