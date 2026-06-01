---
title: "API"
sidebar_position: 7
---

This page is for developers who want to hook into EcoArmor from their own plugin, e.g. to read a player's set or react to armor events. EcoArmor is open-source, so you can also read the implementation directly.

## Source code

The source code is on [GitHub](https://github.com/Auxilor/EcoArmor).

## Adding the dependency

1. Add the Auxilor repository and the EcoArmor dependency to your `build.gradle.kts`:

   ```kotlin
   repositories {
       maven("https://repo.auxilor.io/repository/maven-public/")
   }

   dependencies {
       compileOnly("com.willfp:EcoArmor:<version>")
   }
   ```

The latest version available on the repo can be found [here](https://github.com/Auxilor/EcoArmor/tags).

<hr/>

## Where to go next

- **Shared APIs:** the [eco framework](https://github.com/Auxilor/eco) is where the shared effects, conditions, and triggers APIs live.
- **Config side:** [How to Make an Armor Set](how-to-make-a-custom-set) for the config that backs the API.