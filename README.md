# Rithmo CLI

## Overview

`rithmo-cli` is a lightweight console application built on top of the Rithmomachia project.

It provides a playable terminal interface for experimenting with the game engine and validating gameplay features without requiring a graphical user interface.

The CLI is primarily intended for:

* Manual gameplay testing
* Rule validation
* Victory condition verification
* Debugging and development of the Core and Engine modules

---

# 🧩 Project Structure

The Rithmomachia project is split into multiple modules:

* [rithmo-engine](https://github.com/Phalenopsis/rithmo-engine?utm_source=chatgpt.com) → Pure game rules and mathematical computations
* [rithmo-core](https://github.com/Phalenopsis/rithmo-core?utm_source=chatgpt.com) → Turn orchestration, game lifecycle and DTO projections
* `rithmo-cli` → Console-based user interface

Architecture overview:

```text
CLI → Core → Engine
```

---

# 🎮 Features

The CLI currently supports:

* Starting predefined games
* Displaying the board in the terminal
* Listing available player actions
* Executing moves and captures
* Displaying capture justifications
* Detecting victories
* Displaying victory conditions and detailed victory explanations

Example victory output:

```text
Et le gagnant est WHITE !

Conditions de victoire remplies :
    - de bien

Raisons de la victoire :
    Valeur totale capturée/requise : 36/30
```

More complex victory configurations are also supported:

```text
Conditions de victoire remplies :
    - de corps et de bien
```

---

# 📚 Game Rules

The complete game rules are documented in the Engine repository:

[Rithmomachia Rules (French)](https://github.com/Phalenopsis/rithmo-engine/blob/main/doc/rules.fr.md?utm_source=chatgpt.com)

---

# 🚀 Running the CLI

Build the project:

```bash
mvn clean package
```

Run the application:

```bash
java -jar target/rithmo-cli.jar

```

---

# 🧪 Purpose

This project is intentionally simple.

It serves as:

* A manual integration test client
* A development playground
* A reference implementation showing how to consume the Core API

The CLI is not intended to be a full-featured user interface.

---

# 🔗 Related Projects

* [rithmo-engine](https://github.com/Phalenopsis/rithmo-engine?utm_source=chatgpt.com)
* [rithmo-core](https://github.com/Phalenopsis/rithmo-core?utm_source=chatgpt.com)

---

# 📄 License

TBD

