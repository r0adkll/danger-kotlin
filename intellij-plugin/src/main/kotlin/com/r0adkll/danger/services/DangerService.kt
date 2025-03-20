package com.r0adkll.danger.services

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.util.ExecUtil
import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level.PROJECT
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.getCanonicalPath
import com.intellij.openapi.util.io.toNioPathOrNull
import com.r0adkll.danger.DangerScriptDefinitionsSource
import com.r0adkll.danger.settings.settings
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.outputStream
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.kotlin.idea.core.script.ScriptDefinitionsManager
import org.jetbrains.kotlin.idea.core.script.loadDefinitionsFromTemplatesByPaths
import org.jetbrains.kotlin.idea.core.script.settings.KotlinScriptingSettings
import org.jetbrains.kotlin.scripting.definitions.ScriptDefinition
import org.jetbrains.kotlin.scripting.definitions.getEnvironment

fun Project.danger(): DangerService = service()

@Service(PROJECT)
class DangerService(private val project: Project, private val scope: CoroutineScope) {

  var dangerVersion: String? = null
  var dangerLocation: String? = null

  var dangerConfig: DangerConfig? = null
  var dangerScriptDefinition: ScriptDefinition? = null

  private val logger: Logger = thisLogger()

  fun reloadAsync() = scope.launch { load() }

  suspend fun load() {
    // This should 1:1 match our danger-kotlin version for consistency
    val pluginVersion =
      PluginManager.getInstance().findEnabledPlugin(PluginId.getId("com.r0adkll.danger"))?.version
        ?: error("Unable to load this plugins version")

    logger.info("Loading Danger for $pluginVersion")

    // First search for a system installed danger source jar and use it,
    // if not default back to the local project installed jar.
    val dangerKotlinJar =
      getCustomDangerSourceJarPath()
        ?: getSystemDangerSourceJarPath()
        ?: getProjectDangerSourceJarPath(pluginVersion)

    if (dangerKotlinJar.exists()) {
      logger.info("danger-kotlin.jar found @ ${dangerKotlinJar.absolutePathString()}")
      dangerConfig = DangerConfig(dangerKotlinJar)
      dangerScriptDefinition = scriptDefinition(dangerConfig!!)
      reloadScriptDefinitions()
    } else {
      logger.warn("danger-kotlin.jar could not be found in this project, copy from plugin")
      copyDangerJar(pluginVersion)
      reloadScriptDefinitions()
    }

    // Now attempt to fetch the current installed danger version / locations
    try {
      dangerVersion =
        ExecUtil.execAndReadLine(GeneralCommandLine("danger-kotlin", "--version"))
          ?.replace("danger-kotlin version ", "")
          ?.trim()
      dangerLocation = ExecUtil.execAndReadLine(GeneralCommandLine("which", "danger-kotlin"))

      thisLogger().warn("Danger ($dangerVersion, $dangerLocation)")
    } catch (e: Exception) {
      thisLogger().error("Unable to fetch current danger-kotlin information")
    }
  }

  private fun scriptDefinition(config: DangerConfig): ScriptDefinition? {
    return loadDefinitionsFromTemplatesByPaths(
        templateClassNames = listOf(config.className),
        templateClasspath = listOf(config.classPath),
        baseHostConfiguration =
          ScriptingHostConfiguration(defaultJvmScriptingHostConfiguration) {
            getEnvironment {
              mapOf("projectRoot" to (project.basePath)?.let(::File), "disableImports" to true)
            }
          },
      )
      .firstOrNull()
  }

  // TODO: Update this to support k2 mode at some point in the future
  private fun reloadScriptDefinitions() {
    ScriptDefinitionsManager.getInstance(project).apply {
      reloadDefinitionsBy(DangerScriptDefinitionsSource(project))

      dangerScriptDefinition?.let { definition ->
        KotlinScriptingSettings.getInstance(project).apply {
          // Make sure the DF script def is first, or else it won't load
          setOrder(definition, -100)

          // Make sure auto-reload is on to automatically react to changes
          setAutoReloadConfigurations(definition, true)
        }
      }

      reorderDefinitions()
    }
  }

  private suspend fun copyDangerJar(version: String) =
    withContext(Dispatchers.IO) {
      val packagedJar =
        DangerService::class.java.classLoader.getResourceAsStream("jar/danger-kotlin.jar")
          ?: error("Error loading packaged jar")

      getDangerSourceDirectory(version)?.createDirectories()
      val outputPath = getProjectDangerSourceJarPath(version)

      packagedJar.copyTo(outputPath.outputStream())

      logger.info("Finished copying packaged jar to ${outputPath.absolutePathString()}")

      dangerConfig = DangerConfig(outputPath)
      dangerScriptDefinition = scriptDefinition(dangerConfig!!)
    }

  private fun getDangerSourceDirectory(version: String): Path? {
    return project.basePath
      ?.toNioPathOrNull()
      ?.resolve(DANGER_PROJECT_DIR)
      ?.resolve(DANGER_SOURCE_DIR)
      ?.resolve(version)
  }

  private fun getProjectDangerSourceJarPath(version: String): Path {
    return getDangerSourceDirectory(version)?.resolve(DANGER_SOURCE_JAR_NAME)
      ?: error("Unable to resolve danger source jar file path")
  }

  private fun getSystemDangerSourceJarPath(): Path? {
    return platformExpectedLibLocations
      .map { "$it/lib/danger/danger-kotlin.jar" }
      .map { Path.of(it) }
      .firstOrNull { path ->
        path.exists().also {
          if (it) logger.info("System danger-kotlin.jar found! ${path.absolutePathString()}")
        }
      }
  }

  private fun getCustomDangerSourceJarPath(): Path? {
    return project
      .settings()
      .customDangerJarSourcePath
      ?.takeIf { it.isNotBlank() }
      ?.let { Path.of(getCanonicalPath(it)) }
      ?.takeIf { it.exists() }
  }
}

internal const val DANGER_PROJECT_DIR = ".danger"
private const val DANGER_SOURCE_DIR = "dist"
private const val DANGER_SOURCE_JAR_NAME = "danger-kotlin.jar"

private val platformExpectedLibLocations =
  setOf(
    "/usr/local", // x86 location
    "/opt/local", // Arm
    "/opt/homebrew", // Homebrew Arm
    "/usr", // Fallback
  )

data class DangerConfig(
  val classPath: Path,
  val className: String = "systems.danger.kts.DangerFileScript",
)
