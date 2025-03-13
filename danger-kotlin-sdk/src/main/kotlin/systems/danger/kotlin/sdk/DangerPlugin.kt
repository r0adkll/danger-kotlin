package systems.danger.kotlin.sdk
/**
 * A DangerPlugin is a special object that contains utils methods to be executed into the Danger
 * File. To create a new plugin you need to extend this class as per example:
 * ```
 * object MyDangerPlugin: DangerPlugin() {
 *     override val id = "MyUniquePluginId"
 *
 *     fun myUtilMethod() {
 *         context.warn("This is a util method")
 *     }
 * }
 * ```
 *
 * your plugin can be registered and executed into your DangerFile with:
 * ```
 * Danger register MyDangerPlugin
 * ```
 *
 * and then used:
 * ```
 * danger(args) {
 *     MyDangerPlugin.myUtilMethod()
 * }
 * ```
 *
 * with this example a warning message is published on your Pull Request.
 *
 * @constructor Create empty Danger plugin
 */
abstract class DangerPlugin {

  // The plugin unique id
  abstract val id: String

  /**
   * The context that will be set by the danger runner. This wil be null if the plugin has not been
   * registered in the Dangerfile.
   */
  var registeredContext: DangerContext? = null

  /**
   * Provides a backing getter around registeredContext to avoid null checks if we know the plugin
   * must be registered in the Dangerfile.
   *
   * @throws NullPointerException if the plugin was not registered in the Dangerfile.
   */
  val context: DangerContext
    get() =
      registeredContext
        ?: throw NullPointerException(
          "DangerContext is null! Have you registered this plugin in your Dangerfile e.g. " +
            "'Danger register ${this::class.simpleName}'?"
        )
}
