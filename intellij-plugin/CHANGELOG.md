<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Danger Kotlin IntelliJ Plugin Changelog

## [Unreleased]

## [2.0.7]

### Changed

- Updated to match underlying library.

## [2.0.6]

### Changed

- Updated to match underlying library.

## [2.0.5]

### Added

- New `@ImportDirectory` annotation for importing entire directories of Dangerfiles.

### Fixed

- Disabled `@Import` and `@ImportDirectory` annotations when editing a Dangerfile in the IDE to since these break the editor. See https://youtrack.jetbrains.com/issue/KTIJ-16352.

## [2.0.4]

### Fixed

- Fixed run actions not working on production IDE builds
- Fixed thread blocking on project start
- Fixed run actions not updating when branches change

## [2.0.3]

### Added

- Added settings panel for view `danger` an `danger-kotlin` installation information
- Added setting to custom provide source `danger-kotlin.jar` for script evaluation/editing

### Fixed

- Fixed how pull request URLs are searched in the GH api

## [2.0.2]

### Added

- Added 'Run' gutter action in *.df.kts files that will run the Dangerfile directly in the IDE
- Added integration with GitHub / Git4Idea plugins to automatically configure run actions.

## [2.0.1]

### Changed

- Bumping version danger-kotlin that doesn't break

## [2.0.0]

### Changed

- Initial release

[Unreleased]: https://github.com/r0adkll/danger-kotlin/compare/v2.0.2...HEAD
[2.0.2]: https://github.com/r0adkll/danger-kotlin/compare/v2.0.1...v2.0.2
[2.0.1]: https://github.com/r0adkll/danger-kotlin/compare/v2.0.0...v2.0.1
[2.0.0]: https://github.com/r0adkll/danger-kotlin/commits/v2.0.0
