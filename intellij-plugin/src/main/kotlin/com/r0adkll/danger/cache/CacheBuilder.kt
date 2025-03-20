package com.r0adkll.danger.cache

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.util.io.DigestUtil
import com.intellij.util.io.createDirectories
import com.r0adkll.danger.services.DANGER_PROJECT_DIR
import java.nio.file.Path
import kotlin.io.path.*

fun Project.cacheBuilder() = service<CacheBuilder>()

@Service(Service.Level.PROJECT)
class CacheBuilder(
  private val project: Project,
) {

  fun buildEnvironment(dangerFile: String): Map<String, String> {
    val dangerFilePath = Path.of(dangerFile)

    return getDangerCacheBasePath()
      ?.let { parentDir ->
        computeDangerFileChecksum(dangerFilePath)
          ?.let { checksum -> parentDir.resolve(checksum) }
      }?.let { cacheDir ->
        // Make sure this exists
        cacheDir.createDirectories()
        mapOf(KSCRIPT_CACHE_ENV_VAR to cacheDir.absolutePathString())
      } ?: emptyMap()
  }

  private fun computeDangerFileChecksum(dangerFilePath: Path): String? {
    if (dangerFilePath.exists()) {
      val digest = DigestUtil.md5()
      val buffer = ByteArray(BUFFER_SIZE)

      // Add danger file content to the hash
      DigestUtil.updateContentHash(digest, dangerFilePath, buffer)

      dangerFilePath.readLines()
        .mapNotNull {
          ImportRegex.find(it)
            ?.groupValues
            ?.getOrNull(2)
        }
        .map { dangerFilePath.parent.resolve(it) }
        .filter { it.exists() }
        .sorted()
        .forEach {
          if (it.isDirectory()) {
            // hash ONLY .kts files
            it.listDirectoryEntries("*.kts").forEach { childFile ->
              DigestUtil.updateContentHash(digest, childFile, buffer)
            }
          } else {
            // Hash this file content
            DigestUtil.updateContentHash(digest, it, buffer)
          }
        }

      return DigestUtil.digestToHash(digest)
    }

    return null
  }

  /**
   * This returns the base cache directory to use when running Danger via this plugin
   */
  private fun getDangerCacheBasePath(): Path? {
    return project.basePath
      ?.toNioPathOrNull()
      ?.resolve(DANGER_PROJECT_DIR)
      ?.resolve(DANGER_CACHE_DIR)
  }
}

// 512Kb just because it's default in DigestUtil.
// For comparison, the biggest jar in kotlin dist is kotlin-compiler.jar is 158Mb
private const val BUFFER_SIZE = 512 * 1024

private const val DANGER_CACHE_DIR = "cache"
private const val KSCRIPT_CACHE_ENV_VAR = "KOTLIN_MAIN_KTS_COMPILED_SCRIPTS_CACHE_DIR"

private val ImportRegex = "^@file:Import(Directory)?\\(\"(.+)\"\\)".toRegex()
