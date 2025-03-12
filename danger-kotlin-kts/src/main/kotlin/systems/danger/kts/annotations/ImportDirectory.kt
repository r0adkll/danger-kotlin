package systems.danger.kts.annotations

/**
 * Add this annotation to import a directory of script files. This is convenient if you have a
 * multi-file Danger setup and don't want to have to include _every_ added file by name.
 *
 * For example just add
 *
 * ```
 * @file:ImportDirectory("rules")
 *
 * import …
 *
 * danger(args) {
 *   //…
 * }
 * ```
 *
 * instead of
 *
 * ```
 * @file:Import("rules/rule1.df.kts")
 * @file:Import("rules/rule2.df.kts")
 * @file:Import("rules/rule3.df.kts")
 *
 * import …
 *
 * danger(args) {
 *   //…
 * }
 * ```
 *
 * Both this annotation and [org.jetbrains.kotlin.mainKts.Import] are disabled when loading a script
 * into the IDE by the IntelliJ plugin as doing so breaks custom script handling. See
 * [https://youtrack.jetbrains.com/issue/KTIJ-16352](https://youtrack.jetbrains.com/issue/KTIJ-16352)
 * for more details.
 *
 * @param paths the list of directories to import
 * @param excludes a list of file name exceptions to NOT import
 */
@Target(AnnotationTarget.FILE)
@Repeatable
@Retention(AnnotationRetention.SOURCE)
annotation class ImportDirectory(vararg val paths: String, val excludes: Array<String> = [])
