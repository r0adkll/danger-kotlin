package systems.danger.kotlin

import systems.danger.kotlin.sdk.DangerContext
import systems.danger.kotlin.sdk.DangerPlugin

/**
 * Register Helps to register a [DangerPlugin] before usage. contains the plugins to be registered
 * within [DangerContext]
 */
object Danger {
  private val plugins = mutableMapOf<String, DangerPlugin>()

  /**
   * Add a plugin to Danger Example code:
   * ```
   * Danger register DangerPluginName
   *
   * // Then
   *
   * danger(args) {
   *   ...
   * }
   * ```
   *
   * @param plugin the [DangerPlugin] to be registered
   */
  infix fun register(plugin: DangerPlugin) {
    checkNotRegistered(plugin)
    plugins[plugin.id] = plugin
  }

  /** We don't want to allow */
  private fun checkNotRegistered(plugin: DangerPlugin) {
    check(!plugins.containsKey(plugin.id)) {
      "A plugin with the id '${plugin.id}' has already been registered."
    }
  }

  /**
   * Register the [DangerContext] with all installed [DangerPlugin]s
   *
   * @param context the [DangerContext] implementation that all plugins use to communicate to the
   *   Danger instance.
   */
  internal fun installContext(context: DangerContext) {
    plugins.values.forEach { it.registeredContext = context }
  }
}

/**
 * Block that gave another option for registering plugins Example code:
 * ```
 * plugins {
 *     register(DangerPlugin1)
 *     register(DangerPlugin2)
 * }
 *
 * // Then
 *
 * danger(args) {
 * ...
 * }
 * ```
 *
 * @param block
 * @receiver [register] the registered plugins container
 */
inline fun plugins(block: Danger.() -> Unit) = Danger.run(block)
