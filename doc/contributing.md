# Contributing

## Requirements

* Java 21
* Maven 3.9+
* Git

---

# Clone

```bash
git clone ...
cd rithmo-engine
```

Enable the project's Git hooks:

```bash
git config core.hooksPath .githooks
```
Enable the project's Git hooks:

```bash
git config core.hooksPath .githooks
```

Make the required scripts executable:

```bash
chmod +x .githooks/pre-commit
chmod +x .githooks/pre-push
chmod +x scripts/validate-release.sh
```

**Note**: On Unix-like systems, Git normally preserves executable permissions. These commands are mainly useful after downloading the project as an archive or when executable bits have been lost.

---

# Code Style

Formatting is managed using Spotless with Google Java Format.

Apply formatting:

```bash
mvn spotless:apply
```

Validate formatting:

```bash
mvn spotless:check
```

---

# Git Hooks

## pre-commit

Automatically:

* formats the project
* rejects accidental debug helpers
* warns about remaining debug traces

---

## pre-push

### Feature branches

Runs:

```bash
mvn verify
```

If verification fails, the push is still allowed, but a warning is displayed because CI is expected to fail.

### Release tags

For version tags (`vX.Y.Z`), the hook additionally:

* validates the release metadata
* checks version consistency between `pom.xml`, the Git tag and `CHANGELOG.md`

Unlike feature branches, release tags are rejected if validation or verification fails.

---

# Continuous Integration

Every pull request targeting `main` and every push to `main` automatically executes:

* Maven Verify
* Spotless Check

A pull request should only be merged once all checks are green.

---

# Development Workflow

Typical workflow:

1. Create a feature branch.
2. Implement the feature.
3. Commit regularly.
4. Push the branch.
5. Open a Pull Request.
6. Merge into `main`.
