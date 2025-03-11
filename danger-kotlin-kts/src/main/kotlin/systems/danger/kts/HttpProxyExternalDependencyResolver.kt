package systems.danger.kts

import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.dependencies.ExternalDependenciesResolver
import kotlin.script.experimental.dependencies.RepositoryCoordinates

/**
 * This is a delegating Http Proxy dependency resolver. While a user might be editing a script
 * locally they will likely need to resolve dependencies from a maven repository using https schema.
 * However, when running this in CI they might be behind an HTTP proxy and can't make https
 * requests.
 *
 * This delegating resolver detects the system props `http_proxy` or `HTTP_PROXY` and swaps the
 * requesting schema out if it starts with `https`
 */
class HttpProxyExternalDependencyResolver(private val delegate: ExternalDependenciesResolver) :
  ExternalDependenciesResolver {

  override fun acceptsArtifact(artifactCoordinates: String): Boolean {
    return delegate.acceptsArtifact(artifactCoordinates)
  }

  override fun acceptsRepository(repositoryCoordinates: RepositoryCoordinates): Boolean {
    return delegate.acceptsRepository(repositoryCoordinates)
  }

  override fun addRepository(
    repositoryCoordinates: RepositoryCoordinates,
    options: ExternalDependenciesResolver.Options,
    sourceCodeLocation: SourceCode.LocationWithId?,
  ): ResultWithDiagnostics<Boolean> {
    val httpProxy = System.getenv("http_proxy") ?: System.getenv("HTTP_PROXY")
    if (httpProxy != null && repositoryCoordinates.string.startsWith("https")) {
      // We have entered a maven repository targeting https, but have a http proxy enabled
      // swap the scheme out so that the dependencies can resolve while under a proxy
      val newRepositoryCoordinates =
        RepositoryCoordinates(repositoryCoordinates.string.replace("https", "http"))
      return delegate.addRepository(newRepositoryCoordinates, options, sourceCodeLocation)
    } else {
      return delegate.addRepository(repositoryCoordinates, options, sourceCodeLocation)
    }
  }
}
