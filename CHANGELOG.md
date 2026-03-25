# Changelog

## [Unreleased]

### Added

### Changed

### Deprecated

### Removed

### Fixed

## 1.1.1

### Fixed
- Fixed live templates/surrounds

## 1.1.0

### Changed
- Updated plugin build tooling
- Improved Silverstripe version support detection

### Fixed
- Rare exception when PhpStorm attempted to read Silverstripe files mid-index

## 1.0.4

### Changed
- Updated Silverstripe icons with new open source icons

### Fixed
- Fixed deprecated APIs, updated minimum supported version to 2022.3

## 1.0.3

### Changed
- More flexible <% require %> syntax
- Replaced usage of IntelliJ internal API
- Replaced usage of deprecated APIs
- Removed usage of experimental APIs

### Fixed
- Issue with lookups annotations

## 1.0.2

### Fixed
- Fixed issue with parsing of float arguments in lookups (#21)

## 1.0.1

### Changed
- Improved `BraceMatcher` to highlight start / end of tags as well as braces/parentheses (#18)

### Fixed
- Slightly more permissive lexing of translation entities (#20)

## 1.0.0
Initial stable release

## 0.3.3

### Changed
- Block stack in parser shouldn’t be static (#16)

## 0.3.2

### Fixed
- Intermittent issues indexing projects (#14)

## 0.3.1

### Fixed
- Issues with numbers in translation keys (#12)

## 0.3.0

### Added
- Support for generic open & closed block statements (e.g. cacheinclude cache statements)

### Changed
- Significant internal refactor of parser logic
- Renamed a number of token types to better indicate what they represent
- Added a new token type for "nested" statements (inside closed blocks)
- Removed unused token type for $ThemeDir variable
- Simplified comment lexing

### Fixed
- Issue with background indexing of templates (#3)

## 0.2.3

### Fixed
- Further issues with how $Variables without {} braces were handled

## 0.2.2

### Fixed
- Regression in how $Variables without {} braces were handled

## 0.2.1

### Fixed
- Issues with typing `$` or `>` in an empty template
- Includes with numbers in file name were broken
- Annotations for includes using double-backslash separators were broken

## 0.2.0

### Added
- Support for renaming/moving include files (auto-update references)

### Fixed
- Broken click to navigate to include
- Stale references to includes when include statement is changed

## 0.1.0

### Added
- Live templates for code completion/surrounding

### Fixed
- Issues with include statements/references triggering exceptions

## 0.0.2

### Fixed
- Some include files wouldn’t be recognised as valid in SS4 projects

## 0.0.1
- Initial release
