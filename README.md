<p align="center">
<img width=200 src=".idea/icon.svg" /></br>
⚠️ Stop saying "you forgot to …" in code review in Kotlin
</p>

# 🧪 Project status 🧪
This project is currently in **_Beta_**

This was forked from [danger/kotlin](https://github.com/danger/kotlin) to undergo extensive project maintenance and opinionated changes that felt should
stand in its own repository.  I don't plan on changing the namespacing of classes, except for artifact targets, to maintain
as much compatibility between the two. However, there may be a point at which I deviate from this as the project continues to
grow.

---

[![Current
Version](https://img.shields.io/badge/r0adkll/danger%20kotlin-v2.0.4-orange)](https://danger.systems/kotlin/)
[![Maven Central - SDK](https://img.shields.io/maven-central/v/com.r0adkll.danger/danger-kotlin-sdk.svg?label=danger-kotlin-sdk)](https://search.maven.org/search?q=g:%22com.r0adkll%22%20AND%20a:%22danger-kotlin-sdk%22)
[![Sonatype Snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.r0adkll.danger/danger-kotlin-sdk.svg)](https://oss.sonatype.org/content/repositories/snapshots/com/r0adkll/danger/)

### :warning: https://r0adkll.github.io/danger-kotlin/

# Setup

**Mac - Apple Silicon**

```shell
homebrew install r0adkll/tap/danger-kotlin
```

**Mac - Intel**

```shell
homebrew install r0adkll/tap/danger-kotlin-intel
```

**Linux**

```shell
bash <(curl -s https://raw.githubusercontent.com/r0adkll/danger-kotlin/main/scripts/install.sh)
source ~/.bash_profile
```

---

# IntelliJ Plugin
This repository boasts an [IntelliJ Plugin](intellij-plugin/) that you can install to automatically load the
custom Danger kotlin script definition into the IDE for syntax highlighting and autocomplete.

Install the latest version via https://plugins.jetbrains.com/plugin/26699-danger-kotlin

or

Directly in your IDE via

```
Plugins --> Marketplace --> Danger Kotlin
```


> [!NOTE]
> Custom Kotlin script definitions are still a bit wonky in the IDE so sometimes it can take a restart, or another file resolving for it to "settle down"

### Run Configurations
The plugin will also automatically detect `danger(args) {…}` in your Dangerfiles and provide a ▶︎ icon in the gutter for
testing your scripts directly in the IDE

### GitHub Plugin Integration
The plugin is also integrates with the built-in [GitHub Plugin][] to automatically detect open PRs for your current branch.
This allows you to run a `danger-kotlin pr` command directly from the IDE without having to copy the PR url. It will also
automatically hydrate the runtime environment with the auth token and host/api url information based on the remote you have configured
in `git`.

Additionally, it will use git to determine if you are on a branch that is not a base branch (i.e. `master`, `main`, `develop`) and
give you an option to run `danger local` against your base.

---

# Github Actions / CI

<!--
You can add danger/kotlin to your actions

Parameters:
* `dangerfile`: Path to danger file,  required: `false`,  default: `Dangerfile.df.kts`
* `run-mode`: Run mode: `ci`, `local`, `pr`, required: `false`  default: `ci`
* `job-id:` Reported CI job ID, required: `false`, default: `danger/kotlin`
* `args`: Extra custom arguments like "--failOnErrors --no-publish-check" and etc, required: `false`

```yml
jobs:
  build:
    runs-on: ubuntu-latest
    name: "Run Danger"
    steps:
      - uses: actions/checkout@v4
      - name: Danger
        uses: r0adkll/danger-kotlin@2.0.3
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```
-->

Danger has prebuilt images that you can use in your workflows

https://github.com/r0adkll/danger-kotlin/pkgs/container/danger-kotlin

In order to import one of those use the `docker://` prefix, like so:

```yml
jobs:
  build:
    runs-on: ubuntu-latest
    name: "Run Danger"
    container:
      image: docker://ghcr.io/r0adkll/danger-kotlin:<latest_version>
    steps:
      - uses: actions/checkout@v4
      - name: Run Danger
        run: danger-kotlin ci --failOnErrors --no-publish-check
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

---

# Authors
`danger-kotlin` was originally developed by [@gianluz][] and [@f-meloni][] @ [danger/kotlin](https://github.com/danger/kotlin)

and forked by [@r0adkll][]

[@f-meloni]: https://github.com/f-meloni
[@gianluz]: https://github.com/gianluz
[@r0adkll]: https://github.com/r0adkll

[GitHub Plugin]: https://plugins.jetbrains.com/plugin/13115-github
