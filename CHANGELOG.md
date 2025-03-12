<!--

// Please add your own contribution below inside the Master section, no need to
// set a version number, that happens during a deploy. Thanks!
//
// These docs are aimed at users rather than danger developers, so please limit technical
// terminology in here.

// Note: if this is your first PR, you'll need to add your URL to the footnotes
//       see the bottom of this file

-->
## Unreleased
- Fix an edgecase where DangerContext can be installed before plugins are registered

# 2.0.5
- Refactoring how the `danger-kotlin-sdk` integrates with the main library
- Simplified test plugin setup in the project
- Added test fixtures for `danger-kotlin-sdk`
- Add new `@file:ImportDirectory` kscript annotation for importing entire directories of scripts
- Disabled all `@Import` and `@ImportDirectory` annotations when loading the script in an IDE context. See https://youtrack.jetbrains.com/issue/KTIJ-16352.
- Adding support for `http_proxy` so that maven repositories/dependencies can be resolved in certain CI setups.

# 2.0.4
- Fixed ANR when project is loading in the IDE
- Fixed IJ run actions not working on production IDE builds.
- Fixed IJ plugin not updating run actions when branches change

# 2.0.3
- Added a new `Rules` feature that allows you to write multiple danger rules for a single run in a graphable order.
- Added IntelliJ Plugin settings panel for displaying current information and allow custom source jar for scripts
- Fixed how IJ Plugin searches PR urls for `danger-kotlin pr` runs.
- IJ Plugin will now auto-hydrate the run environment for GH tokens, base, api urls

# 2.0.2
- Added Run Configuration to IntelliJ Plugin so Dangerfiles can be run directly in the IDE

# 2.0.1
- Fixing how clikt parses arguments to prevent it from choking
- Setup publishing for IntelliJ Plugin
- More repo maintenance.

# 2.0.0
- Forked repository to make substantial improvements to build configurations
- Fixed bug in ScriptDefinition not loading jvm classpath correctly
- Added IntelliJ Plugin that autoloads the script definition into IDEA/AS
- Added GitHub client to script library that is hydrated based on the same env vars as danger-js

# 1.3.3
- Update README.md with guidance to enable auto-complete in Android Studio [@gianluz] - [#242](https://github.com/danger/kotlin/pull/242)
- Update install script with Kotlin compiler 1.7.0 [@gianluz] - [#241](https://github.com/danger/kotlin/pull/241)
- Add accessors for Danger reports [@417-72KI] - [#245](https://github.com/danger/kotlin/pull/245)

# 1.2.0
- Update `Kotlin` to `1.7.0` and added support for Apple Silicon Chipset [@gianluz] - [#231](https://github.com/danger/kotlin/pull/231)
- Make user property nullable for cases when non BB user did a commit [@vchernyshov]
- Make GitLab approvals_before_merge variable nullable [#227](https://github.com/danger/kotlin/pull/227)

# 1.1.0

- Add support of BitBucketCloud [@vchernyshov] - [#214](https://github.com/danger/kotlin/pull/214)
- Make `force_remove_source_branch` nullable in GitLab Merge request entity [@davidbilik] - [#197](https://github.com/danger/kotlin/pull/197)
- Make `lastReviewedCommit` nullable on BitBucket Server [@f-meloni] - [#211](https://github.com/danger/kotlin/pull/211)
- Update `GitLabMergeRequest` model: add `squash` field [@sonulen] - [#212](https://github.com/danger/kotlin/pull/212)
- Add Gitlab extensions for url's: to project, to file diff, to current version of file [@sonulen] - [#212](https://github.com/danger/kotlin/pull/212)
- Upgrade action to use node14 [@eygraber] - [#215](https://github.com/danger/kotlin/pull/215)

# 1.0.0-beta4, 1.0.0

- Create the Danger main instance only once [@f-meloni] - [#185](https://github.com/danger/kotlin/pull/185)

# 1.0.0-beta3

- Coroutines compatibility [@gianluz] - [#177](https://github.com/danger/kotlin/pull/177)
- Improving error message for when a DangerPlugin was not registered [@rojanthomas] - [#181](https://github.com/danger/kotlin/pull/181)
- Fix body parameter in github models  [@tegorov] - [#175](https://github.com/danger/kotlin/pull/175)

# 1.0.0-beta2

- Update kotlinx-datetime to 0.1.1 [@f-meloni] - [@gianluz] - [#167](https://github.com/danger/kotlin/pull/167)
- Support GitLab different time zones on the JSON [@f-meloni] - [#169](https://github.com/danger/kotlin/pull/169)
- Update Kotlin to 1.5.0 [@gianluz] - [#171](https://github.com/danger/kotlin/pull/171)

# 1.0.0-beta

- Support --help parameter [@f-meloni] - [#153](https://github.com/danger/kotlin/pull/155)
- Update Kotlin to 1.4.10 [@gianluz] - [#140](https://github.com/danger/kotlin/pull/140)
- Migrate from moshi to kotlinx serialization [@gianluz] - [#141](https://github.com/danger/kotlin/pull/141)
- Fix incorrect url in install.sh script and in Dockerfile [@davidbilik] - [#144](https://github.com/danger/kotlin/pull/144)
- Road to 1.0 - Refactor project structure [@gianluz] - [#142](https://github.com/danger/kotlin/pull/142)
- Handle danger-js custom paths with parameter `--danger-js-path` [@f-meloni] - [#153](https://github.com/danger/kotlin/pull/153)
- Update Kotlin to 1.4.20 [@gianluz] - [#148](https://github.com/danger/kotlin/pull/148)
- Fix gitlab defaults following kotlinx serialisation [@gianluz] - [#146](https://github.com/danger/kotlin/pull/146)
- Road to 1.0 - Migrate from java.util.Date to kotlinx.datetime [@gianluz] - [#147](https://github.com/danger/kotlin/pull/147)
- Fix typo in Github Milestone serialization [@doodeec] - [#151](https://github.com/danger/kotlin/pull/151)
- Use fixed commit of danger/kotlin repository in install.sh script [@davidbilik]- [#152](https://github.com/danger/kotlin/pull/152)
- Update Kotlin to 1.4.31 [@gianluz] - [#160](https://github.com/danger/kotlin/pull/160)
- Library resolver and plugin installer gradle plugin [@gianluz] - [#158](https://github.com/danger/kotlin/pull/158)

# 0.7.1

- Make milestone description optional [@f-meloni] - [#136](https://github.com/danger/kotlin/pull/136)
- Optimise Dockerfile layers to make Danger-Kotlin faster when the image is pulled on CI [@f-meloni] - [#129](https://github.com/danger/kotlin/pull/129)
- Add action.yml to make it possible to run locally through [act](https://github.com/nektos/act) [@mariusgreve] - [#135](https://github.com/danger/kotlin/pull/135)

# 0.7.0

- Add logger [@f-meloni] - [#126](https://github.com/danger/kotlin/pull/126)
- Fix DangerKotlinScriptDefinition [@gianluz] - [#121](https://github.com/danger/kotlin/pull/121)
- Update Kotlin to 1.4.0 [@uzzu][] - [#116](https://github.com/danger/kotlin/pull/116)
- Fix crash at milestone.dueOn [@anton46][] - [#108](https://github.com/danger/kotlin/pull/119)

# 0.6.1

- Fix crash on milestone.closedAt [@anton46][] - [#108](https://github.com/danger/kotlin/pull/112)
- Add abstraction for executing shell commands via `ShellExecutor` [@davidbilik][] - [#105](https://github.com/danger/kotlin/pull/105)

# 0.6.0

- Fix to allow for large GitHub id values [@brentwatson][] - [#108](https://github.com/danger/kotlin/pull/108)
- Fix invalid parsing of changes in diff [@davidbilik][] - [#106](https://github.com/danger/kotlin/pull/106)
- Add extensions for changed lines in Git [@davidbilik][] - [#102](https://github.com/danger/kotlin/pull/102)
- Add exec function [@f-meloni][] - [#97](https://github.com/danger/kotlin/pull/97)
- Add readFile function [@f-meloni][] - [#93](https://github.com/danger/kotlin/pull/93)
- Github exposing user avatar [@gianluz] - [#96](https://github.com/danger/kotlin/pull/96)

[@f-meloni]: https://github.com/f-meloni
[@gianluz]: https://github.com/gianluz
[@davidbilik]: https://github.com/davidbilik
[@brentwatson]: https://github.com/brentwatson
[@anton46]: https://github.com/anton46
[@uzzu]: https://github.com/uzzu
[@mariusgreve]: https://github.com/mariusgreve
[@tegorov]: https://github.com/tegorov
[@rojanthomas]: https://github.com/rojanthomas
[@eygraber]: https://github.com/eygraber
[@417-72KI]: https://github.com/417-72KI
