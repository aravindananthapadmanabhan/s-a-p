Project naming & style guide

Purpose
- Small, focused guide for this Android/Kotlin repository to keep naming and resources consistent.

General
- Use full, descriptive names. Prefer clarity over cleverness.
- One public class per file; filename matches the class name.
- Package names: lowercase dot-separated (e.g., `com.example.sap`).

Kotlin / Java
- Types (classes, interfaces, enums): PascalCase (e.g., `MainActivity`, `UserRepository`).
- Functions and methods: lowerCamelCase (e.g., `fetchUsers`, `onCreate`).
- Variables and properties: lowerCamelCase (e.g., `userName`, `retryCount`).
- Constants: UPPER_SNAKE_CASE (e.g., `DEFAULT_TIMEOUT_MS`). Use `const val` for compile-time constants.
- Backing properties: use `_` prefix for private backing fields (e.g., `_binding`).
- Test names: descriptive; use either camelCase with underscores or Kotlin backticks.

Android resources
- Resource filenames (layouts, drawables, values): lowercase with underscores (snake_case).
  - Layouts: `activity_main.xml`, `fragment_profile.xml`.
  - Drawables: `ic_user.png`, `bg_splash.xml`.
  - Values: `colors.xml`, `strings.xml`.
- Resource IDs: snake_case (e.g., `@+id/btn_submit`, `@+id/tv_title`).
- Prefix drawable/resource names for clarity: `ic_` for icons, `bg_` for backgrounds, `btn_` for button drawables.

XML
- IDs: lowercase_underscore (e.g., `@+id/btn_login`).
- Use descriptive content descriptions for accessibility (`android:contentDescription`).

Gradle / Build
- Properties in `gradle.properties` use dot notation (e.g., `org.gradle.jvmargs`).
- Keep task names readable; follow Gradle's camelCase defaults.

Git
- Branch names: short, hyphenated, scoped by type: `feature/add-login`, `fix/crash-on-startup`, `chore/update-deps`.

Examples
- `MainActivity.kt` → class `MainActivity` (PascalCase)
- `res/layout/activity_login.xml` → id `@+id/btn_login` → Kotlin: `val loginButton = findViewById<Button>(R.id.btn_login)`

Enforcement (optional)
- Use `ktlint` and `detekt` for Kotlin style and lint checks.
- Add pre-commit hook to run `ktlintFormat`.

If you want, I can add `ktlint` + `detekt` configs and a pre-commit hook. Let me know which enforcement level you'd like.
