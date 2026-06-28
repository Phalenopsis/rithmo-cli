# Release Process

This project uses GitHub Actions to automatically publish releases to GitHub Packages.

---

# Preparing a Release

Update the project version:

```text
0.6.0-SNAPSHOT
        ↓
0.6.0
```

Update `CHANGELOG.md`:

* rename the `x.y.z-SNAPSHOT` section to `x.y.z`
* ensure the release notes are complete

Commit those changes and merge them into `main`.

---

# Publishing

Create the release tag:

```bash
git tag v0.6.0
git push origin v0.6.0
```

The local Git hook validates:

* version consistency
* changelog consistency
* successful `mvn verify`

If validation succeeds, GitHub Actions:

1. checks out the repository;
2. validates the release metadata;
3. runs `mvn verify`;
4. publishes the package to GitHub Packages.

---

# Preparing the Next Development Cycle

Immediately after the release:

```text
0.6.0
      ↓
0.6.1-SNAPSHOT
```

Create a new commit updating:

* `pom.xml`
* `CHANGELOG.md`

Development can then continue on the next snapshot version.
