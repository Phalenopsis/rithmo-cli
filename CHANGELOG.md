## [0.1.0] - 2026-06-07

### Added

* Initial playable command-line client
* Interactive turn selection and decision handling
* Board and asset rendering
* Capture explanation formatting
* Victory condition and justification display
* In-memory repositories for local gameplay
* View model layer decoupled from Core DTOs
* **Release Automation**: Added a dedicated GitHub Actions workflow to automatically validate and publish releases to GitHub Packages when pushing version tags.
* **Release Validation**: Added reusable release-validation tooling ensuring version consistency between `pom.xml`, Git tags and `CHANGELOG.md` before publication.
