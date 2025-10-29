<p align="center">
  <img width=200 height=200 src="assets/icon.svg"/></br>
  ⚠️ <i>Stop saying "you forgot to …" in code review in Kotlin</i>
</p>

# Danger Kotlin

This project is a middleware layer for [Danger][] that evaluates your Kotlin script based [Dangerfile][] against your pull requests.

## Installation

<details open>
<summary>Mac</summary>

```shell
homebrew install r0adkll/tap/danger-kotlin
```

</details>

<details open>
<summary>Linux</summary>

```shell
bash <(curl -s https://raw.githubusercontent.com/r0adkll/danger-kotlin/main/scripts/install.sh)
source ~/.bash_profile
```

</details>

<details>
<summary>Directly from source</summary>

```shell
git clone https://github.com/r0adkll/danger-kotlin.git
sudo make install
```

</details>

## Write your first Dangerfile

Check out the [Dangerfile > Basic][Dangerfile] page to get started!

## Running your Dangerfile's

Check out the [Usage](usage.md) page to get started!

## IDE Plugin

Check out the [IntelliJ Plugin](intellij-plugin.md) page to get started!

## Extending Danger Kotlin

Check out [Plugins](sdk.md) page for more information on extending Danger with custom plugins.

[Danger]: https://danger.systems/js
[Dangerfile]: basic.md
[IntelliJ Plugin]: intellij-plugin.md
