# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A JetBrains IDE plugin (targeting PHPStorm 2022.3+) that provides language support for Silverstripe's `.ss` template files. Written in Java using the IntelliJ Platform SDK.

## Common Commands

```bash
# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "parser.SilverstripeParserTest"

# Launch an IDE sandbox with the plugin installed (for manual testing)
./gradlew runIde

# Verify plugin configuration
./gradlew verifyPlugin
```

## Architecture

The plugin follows the standard IntelliJ Platform plugin architecture for a custom language:

### Lexer & Parser
- `parser/Silverstripe.flex` — JFlex grammar defining the lexer state machine. **If this file is modified, right-click it in the IDE and select "Run JFlex Generator"** to regenerate `src/main/gen/com/kinglozzer/silverstripe/parser/_Silverstripe.java`.
- `SilverstripeLexer.java` — wraps the generated FlexAdapter
- `SilverstripeMergingLexer.java` — merges adjacent tokens for better handling
- `SilverstripeParser.java` + `SilverstripeParserDefinition.java` — builds the PSI tree from tokens

Generated lexer code lives in `src/main/gen/` (a separate source root from `src/main/java/`).

### PSI (Program Structure Interface)
PSI elements represent the semantic structure of `.ss` files:
- `psi/SilverstripePsiFile.java` — root file element
- `psi/impl/` — concrete PSI element implementations (includes, lookup steps, etc.)
- `psi/references/` — reference providers that power click-to-navigate (e.g. `<% include %>` → template file)

### IDE Feature Integration (`ide/`)
Each subdirectory registers a specific IntelliJ extension:
- `completions/` — auto-completion for block keywords and include paths
- `highlighting/` — syntax highlighting, color settings, tag tree highlighting
- `inspections/` — annotators that flag malformed blocks/includes/translations
- `braces/` — brace matching
- `folding/` — code folding
- `comments/` — comment handler
- `actions/` — enter and typed handlers for smart editing
- `templates/` — live template context definitions

### Multi-language File Handling
`.ss` files are treated as a template language (extending `TemplateLanguage`, `InjectableLanguage`). The `files/SilverstripeFileViewProvider` handles the multi-language file structure where HTML and Silverstripe template syntax coexist.

### Key Utilities
- `util/SilverstripeFileUtil.java` — resolves include template paths; handles SS3 and SS4+ directory conventions
- `util/SilverstripeVersionUtil.java` — detects Silverstripe version from the project

### Tests
Parser tests use IntelliJ's `ParsingTestCase` pattern:
- Test class: `src/test/java/parser/SilverstripeParserTest.java`
- Test data: `src/test/testData/parser/` — paired `.ss` (input) and `.txt` (expected PSI tree output) files
- To add a parser test: add a `.ss` fixture file and a corresponding `.txt` expected output file, then add a `doTest("filename")` call in the test class.

### Plugin Descriptor
All extensions (language, file type, highlighter, completion contributors, annotators, actions, etc.) are registered in `src/main/resources/META-INF/plugin.xml`.

### Lexer Regeneration
The Grammar-Kit and PsiViewer IDE plugins are needed for development. After modifying `Silverstripe.flex`, regenerate the lexer via the IDE's "Run JFlex Generator" action — do not edit the generated `_Silverstripe.java` directly.
