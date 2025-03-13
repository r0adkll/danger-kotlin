package systems.danger.kotlin.sdk

/**
 * Violation is any comment on your Pull Request
 *
 * @param message the violation message
 * @param file the path to the target file
 * @param line the line number into the target file
 * @constructor Create empty Violation
 */
data class Violation(val message: String, val file: String? = null, val line: Int? = null)
