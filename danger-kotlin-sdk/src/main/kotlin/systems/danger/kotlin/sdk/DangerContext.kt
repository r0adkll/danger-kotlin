package systems.danger.kotlin.sdk

/**
 * Defines the API to post the Danger results on your Pull Request
 *
 * @constructor Create empty Danger context
 */
interface DangerContext {

  /**
   * Adds an inline message message to the Danger report
   *
   * @param message the standard message
   */
  fun message(message: String)

  /**
   * Adds an inline message message to the Danger report
   *
   * @param message the standard message
   * @param file the path to the target file
   * @param line the line number into the target file
   */
  fun message(message: String, file: String, line: Int)

  /**
   * Adds an inline markdown message to the Danger report
   *
   * @param message the markdown formatted message
   */
  fun markdown(message: String)

  /**
   * Adds an inline markdown message to the Danger report
   *
   * @param message the markdown formatted message
   * @param file the path to the target file
   * @param line the line number into the target file
   */
  fun markdown(message: String, file: String, line: Int)

  /**
   * Adds an inline warning message to the Danger report
   *
   * @param message the warning message
   */
  fun warn(message: String)

  /**
   * Adds an inline warning message to the Danger report
   *
   * @param message the warning message
   * @param file the path to the target file
   * @param line the line number into the target file
   */
  fun warn(message: String, file: String, line: Int)

  /**
   * Adds an inline fail message to the Danger report
   *
   * @param message the fail message
   */
  fun fail(message: String)

  /**
   * Adds an inline fail message to the Danger report
   *
   * @param message the fail message
   * @param file the path to the target file
   * @param line the line number into the target file
   */
  fun fail(message: String, file: String, line: Int)

  /**
   * Adds an inline suggested code message to the Danger report
   *
   * @param code the suggested code
   * @param file the path to the target file
   * @param line the line number into the target file
   */
  fun suggest(code: String, file: String, line: Int)

  val fails: List<Violation>
  val warnings: List<Violation>
  val messages: List<Violation>
  val markdowns: List<Violation>
}
