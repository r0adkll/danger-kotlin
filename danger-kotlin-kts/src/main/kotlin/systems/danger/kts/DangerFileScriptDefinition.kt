package systems.danger.kts

import java.io.File
import kotlin.script.dependencies.ScriptContents
import kotlin.script.dependencies.ScriptDependenciesResolver
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.host.FileBasedScriptSource
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.impl.internalScriptingRunSuspend
import kotlin.script.experimental.jvm.compat.mapLegacyDiagnosticSeverity
import kotlin.script.experimental.jvm.compat.mapLegacyScriptPosition
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvmhost.jsr223.configureProvidedPropertiesFromJsr223Context
import kotlin.script.experimental.jvmhost.jsr223.importAllBindings
import kotlin.script.experimental.jvmhost.jsr223.jsr223
import kotlin.script.experimental.util.filterByAnnotationType
import org.jetbrains.kotlin.mainKts.*
import systems.danger.kts.annotations.ImportDirectory

@Suppress("unused")
@KotlinScript(
  fileExtension = "df.kts",
  compilationConfiguration = DangerFileScriptDefinition::class,
  evaluationConfiguration = MainKtsEvaluationConfiguration::class,
  hostConfiguration = MainKtsHostConfiguration::class,
)
abstract class DangerFileScript(val args: Array<String>)

object DangerFileScriptDefinition :
  ScriptCompilationConfiguration({
    defaultImports(
      ImportDirectory::class,
      DependsOn::class,
      Repository::class,
      Import::class,
      CompilerOptions::class,
      ScriptFileLocation::class,
    )
    jvm { dependenciesFromClassContext(DangerFileScriptDefinition::class, "danger-kotlin") }
    refineConfiguration {
      onAnnotations(
        ImportDirectory::class,
        DependsOn::class,
        Repository::class,
        Import::class,
        CompilerOptions::class,
        handler = DangerFileKtsConfigurator(),
      )
      onAnnotations(ScriptFileLocation::class, handler = ScriptFileLocationCustomConfigurator())
      beforeCompiling(::configureScriptFileLocationPathVariablesForCompilation)
      beforeCompiling(::configureProvidedPropertiesFromJsr223Context)
    }
    ide { acceptedLocations(ScriptAcceptedLocation.Everywhere) }
    jsr223 { importAllBindings(true) }
  })

class DangerFileKtsConfigurator : RefineScriptCompilationConfigurationHandler {
  private val externalDependenciesResolvers = setOf(MavenDependenciesResolver())
  private val resolvers =
    DANGER_DEFAULT_FLAT_DIRS.map { File(it) }
      .filter { it.exists() }
      .map { FileSystemDependenciesResolver(it) } +
      FileSystemDependenciesResolver() +
      externalDependenciesResolvers

  private val resolver = CompoundDependenciesResolver(resolvers)

  override operator fun invoke(
    context: ScriptConfigurationRefinementContext
  ): ResultWithDiagnostics<ScriptCompilationConfiguration> = processAnnotations(context)

  private fun processAnnotations(
    context: ScriptConfigurationRefinementContext
  ): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val diagnostics = arrayListOf<ScriptDiagnostic>()

    fun report(
      severity: ScriptDependenciesResolver.ReportSeverity,
      message: String,
      position: ScriptContents.Position?,
    ) {
      diagnostics.add(
        ScriptDiagnostic(
          ScriptDiagnostic.unspecifiedError,
          message,
          mapLegacyDiagnosticSeverity(severity),
          context.script.locationId,
          mapLegacyScriptPosition(position),
        )
      )
    }

    // Before we attempt to resolve ANY dependencies, lets set some system props to disable all
    // apache/maven logging
    System.setProperty("log4j.rootLogger", "OFF")
    System.setProperty(
      "org.apache.commons.logging.simplelog.log.org.apache.http.client.protocol.ResponseProcessCookies",
      "FATAL",
    )
    System.setProperty(
      "org.apache.commons.logging.simplelog.log.org.jetbrains.kotlin.org.apache.http.client.protocol.ResponseProcessCookies",
      "FATAL",
    )

    val annotations =
      context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf {
        it.isNotEmpty()
      } ?: return context.compilationConfiguration.asSuccess()

    val scriptBaseDir = (context.script as? FileBasedScriptSource)?.file?.parentFile
    val importedSources = linkedMapOf<String, Pair<File, String>>()
    var hasImportErrors = false
    val environment = getEnvironment(context)

    // Process @Rules annotation, disabling import if env var is false
    // Note: If editing this envar
    val disableImports = environment.getOrDefault("disableImports", false) == true
    if (!disableImports) {
      annotations.filterByAnnotationType<ImportDirectory>().forEach { scriptAnnotation ->
        scriptAnnotation.annotation.paths.forEach { rulePathDirectory ->
          val dir = scriptBaseDir?.resolve(rulePathDirectory) ?: File(rulePathDirectory).normalize()
          if (dir.exists()) {
            dir
              .listFiles { _, name -> name.endsWith(".df.kts") }
              ?.forEach { ruleFile ->
                val prevImport =
                  importedSources.put(
                    ruleFile.absolutePath,
                    ruleFile to "${dir.resolve(ruleFile.name)}",
                  )
                if (prevImport != null) {
                  diagnostics.add(
                    ScriptDiagnostic(
                      ScriptDiagnostic.unspecifiedError,
                      "Duplicate rule imports: \"${prevImport.second}\" and \"${dir.resolve(ruleFile.name)}\"",
                      sourcePath = context.script.locationId,
                      location = scriptAnnotation.location?.locationInText,
                    )
                  )
                  hasImportErrors = true
                }
              }
          } else {
            diagnostics.add(
              ScriptDiagnostic(
                ScriptDiagnostic.unspecifiedError,
                "Rules path does not exists @ ${dir.path}",
                sourcePath = context.script.locationId,
                location = scriptAnnotation.location?.locationInText,
              )
            )
            hasImportErrors = true
          }
        }
      }

      // Process @Import annotation
      annotations.filterByAnnotationType<Import>().forEach { scriptAnnotation ->
        scriptAnnotation.annotation.paths.forEach { sourceName ->
          val file = (scriptBaseDir?.resolve(sourceName) ?: File(sourceName)).normalize()
          val keyPath = file.absolutePath
          val prevImport = importedSources.put(keyPath, file to sourceName)
          if (prevImport != null) {
            diagnostics.add(
              ScriptDiagnostic(
                ScriptDiagnostic.unspecifiedError,
                "Duplicate imports: \"${prevImport.second}\" and \"$sourceName\"",
                sourcePath = context.script.locationId,
                location = scriptAnnotation.location?.locationInText,
              )
            )
            hasImportErrors = true
          }
        }
      }
    }

    if (hasImportErrors) return ResultWithDiagnostics.Failure(diagnostics)

    val compileOptions =
      annotations.filterByAnnotationType<CompilerOptions>().flatMap {
        it.annotation.options.toList()
      }

    val resolveResult =
      try {
        @Suppress("DEPRECATION_ERROR")
        internalScriptingRunSuspend {
          resolver.resolveFromScriptSourceAnnotations(
            annotations
              .filter { it.annotation is DependsOn || it.annotation is Repository }
              .map {
                if (it.annotation is Repository) {
                  ScriptSourceAnnotation(
                    annotation =
                      RepositoryHttpProxyFilter.filterRepository(it.annotation as Repository),
                    location = it.location,
                  )
                } else {
                  it
                }
              }
          )
        }
      } catch (e: Throwable) {
        diagnostics.add(e.asDiagnostics(path = context.script.locationId))
        ResultWithDiagnostics.Failure(diagnostics)
      }

    return resolveResult.onSuccess { resolvedClassPath ->
      ScriptCompilationConfiguration(context.compilationConfiguration) {
          updateClasspath(resolvedClassPath)
          if (importedSources.isNotEmpty()) {
            importScripts.append(importedSources.values.map { FileScriptSource(it.first) })
          }
          if (compileOptions.isNotEmpty()) {
            compilerOptions.append(compileOptions)
          }
        }
        .asSuccess()
    }
  }

  /**
   * This method is very hacky since we can't seem to access the actual Key classes for both
   * `hostConfiguration` and `getEnvironment` so we have to do some name checking and casting to
   * make it possible to fetch script host environment variables
   */
  @Suppress("UNCHECKED_CAST")
  private fun getEnvironment(context: ScriptConfigurationRefinementContext): Map<String, Any?> {
    val hostConfiguration =
      context.compilationConfiguration.entries().find { it.key.name == "hostConfiguration" }?.value
        as? ScriptingHostConfiguration

    val getEnvironment =
      hostConfiguration?.entries()?.find { it.key.name == "getEnvironment" }?.value
        as? () -> Map<String, Any?>

    return getEnvironment?.invoke() ?: emptyMap()
  }

  private companion object {
    val DANGER_DEFAULT_FLAT_DIRS =
      setOf(
          "/usr/local", // x86 location
          "/opt/local", // Arm
          "/opt/homebrew", // Homebrew Arm
          "/usr", // Fallback
        )
        .map { "$it/lib/danger/libs" }
  }
}
