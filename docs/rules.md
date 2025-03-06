# Rules

Rules are a new concept in this fork of Danger Kotlin that allow you to modularize your rules and checks in an ordered way without having to crowd all your logic into a single file.

## Creating a rule

Use the top-level `rule(…)` function to register a new rule with an id, like so:

```kotlin
import systems.danger.kotlin.rule.*

rule("title-check") {
  if (!github.pullRequest.title.matches(TITLE_REGEX)) {
    fail("The title of this pull request must match the format \"JIRA-123: Description of Changes\"")
  }

  RuleResult.Continue
}
```

## Depending on other rules

You can also have rules depend on the execution of other rules. Each rule returns a `RuleResult` that dictates whether the rule chain
continues to execute, or exits entirely.

```kotlin
import systems.danger.kotlin.rule.*

rule("bot-check") {
  if (github.pullRequest.user.type == GitHubUserType.BOT) {
    RuleResult.Exit
  } else {
    RuleResult.Continue
  }
}

//...

rule(id = "title-check", "bot-check") {
  if (!github.pullRequest.title.matches(TITLE_REGEX)) {
    fail("The title of this pull request must match the format \"JIRA-123: Description of Changes\"")
  }

  RuleResult.Continue
}
```

If the `bot-check` rule returns `RuleResult.Exit` then the `title-check` rule will not execute.

## Applying rules

To apply your rule chain in your main Dangerfile just import your other rule script files add `applyRules()` inside the DangerDSL, like so:

```kotlin
@file:Import("rules/bot-check.df.kts")
@file:Import("rules/title-check.df.kts")

import systems.danger.kotlin.*
import systems.danger.kotlin.rule.*

danger(args) {
  applyRules()
}
```
