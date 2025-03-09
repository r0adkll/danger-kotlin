import java.io.ByteArrayOutputStream

plugins { kotlin("jvm") }

group = "system.danger"

version = "sample"

/**
 * A custom task to take the 'jar' outputs from this project and copy them to the locally installed
 * danger-kotlin libs directory so the `Dangerfile_ci.df.kts` script can resolve and test the sample
 * plugin in CI.
 *
 * TODO: Create publishable SDK gradle plugin that can automatically add this dependency and install
 *  the 'jar' task outputs to the locally installed danger-kotlin libs directory
 */
tasks.register("installDangerPlugin") {
  outputs.doNotCacheIf("We shouldn't cache this task") { true }

  dependsOn("jar")

  val jarOutputs = project.tasks.getByName("jar").outputs
  lateinit var dangerKotlinLocation: String

  // Calculate and store the location of `danger-kotlin` executable
  doFirst {
    ByteArrayOutputStream().use { os ->
      exec {
        commandLine("which", "danger-kotlin")
        standardOutput = os
      }
      dangerKotlinLocation = os.toString()
    }
  }

  // Copy jar outputs to configured danger-kotlin lib dir
  doLast {
    val dangerDir = dangerKotlinLocation.trim().replace("bin/danger-kotlin", "").let(::File)
    val sdkLibDir = File(dangerDir, "lib/danger/libs")

    val result = copy {
      from(jarOutputs)
      into(sdkLibDir)
      include("*.jar")
    }

    logger.quiet(
      "Copied [${jarOutputs.files.joinToString { it.name }}] to $sdkLibDir with Result(${result.didWork})"
    )
  }
}

dependencies {
  implementation(libs.kotlin.stdlib)
  implementation(project(":danger-kotlin-sdk"))
}
