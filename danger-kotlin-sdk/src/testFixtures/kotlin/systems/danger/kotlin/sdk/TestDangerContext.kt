package systems.danger.kotlin.sdk

/** A testable implementation of [DangerContext] */
class TestDangerContext : DangerContext {

  override val fails = mutableListOf<Violation>()
  override val warnings = mutableListOf<Violation>()
  override val messages = mutableListOf<Violation>()
  override val markdowns = mutableListOf<Violation>()
  val suggestions = mutableListOf<Violation>()

  override fun message(message: String) {
    messages += Violation(message)
  }

  override fun message(message: String, file: String, line: Int) {
    messages += Violation(message, file, line)
  }

  override fun markdown(message: String) {
    markdowns += Violation(message)
  }

  override fun markdown(message: String, file: String, line: Int) {
    markdowns += Violation(message, file, line)
  }

  override fun warn(message: String) {
    warnings += Violation(message)
  }

  override fun warn(message: String, file: String, line: Int) {
    warnings += Violation(message, file, line)
  }

  override fun fail(message: String) {
    fails += Violation(message)
  }

  override fun fail(message: String, file: String, line: Int) {
    fails += Violation(message, file, line)
  }

  override fun suggest(code: String, file: String, line: Int) {
    suggestions += Violation(code, file, line)
  }
}
