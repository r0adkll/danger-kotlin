# Plugin Development

Danger Kotlin provides an SDK for developing integrations using a lightweight Danger context without having to depend
on the full Danger library.

## Setup

[![Maven Central](https://img.shields.io/maven-central/v/com.r0adkll.danger/danger-kotlin-sdk.svg)](https://search.maven.org/search?q=g:com.r0adkll.danger)
[![Sonatype Snapshot](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.r0adkll.danger/danger-kotlin-sdk.svg)](https://oss.sonatype.org/content/repositories/snapshots/com/r0adkll/danger/)

Add the sdk as a dependency to your project:

```kotlin
dependencies {
  implementation("com.r0adkll.danger:danger-kotlin-sdk:<latest_version>")
}
```

Next, create your main plugin class that you will later register and use.

```kotlin
package com.example.plugin

import systems.danger.kotlin.sdk.DangerPlugin

object ExamplePlugin : DangerPlugin() {

  override val id: String = "com.example.plugin.ExamplePlugin"

  fun doStuff() {
    context.message("👋 Hello from my Danger plugin!")
  }
}
```

## Installation

To use your custom plugin you'll either need to:

### Publish your SDK to a maven repository

Then use the following annotations to import

```kotlin
@file:Repository("http://url.to.maven.repo/repository")
@file:DependsOn("com.example.plugin:example-plugin:version")

import com.example.plugin.ExamplePlugin

//…
```

### Manually copy output JAR

Copy our plugins compiled JAR file to one of these dirs:

* `/usr/local/lib/danger/libs`
* `/opt/local/lib/danger/libs`
* `/opt/homebrew/lib/danger/libs`
* `/usr/lib/danger/libs`

Then use the following annotation to import

```kotlin
@file:DependsOn("example-plugin-0.0.1.jar")

import com.example.plugin.ExamplePlugin

//…
```

## Usage

Once you have your plugin installed, register and use it like so

```kotlin
// @file:…

import com.example.plugin.ExamplePlugin
import systems.danger.kotlin.*

Danger register ExamplePlugin

danger(args) {
  // Do the thing!
  ExamplePlugin.doStuff()
}
```

or if you want to register multiple plugins at a time

```kotlin
// @file:…

import com.example.plugin.ExamplePlugin
import systems.danger.kotlin.*

plugins {
  register(ExamplePlugin)
  register(SomeOtherPlugin)
}

danger(args) {
  // Do the thing!
  ExamplePlugin.doStuff()
  SomeOtherPlugin.doOtherStuff()
}
```

## Testing

The SDK provides test fixtures to make it easier to test your plugins. Just import them like so:

```kotlin
dependencies {
  testImplementation(testFixtures("com.r0adkll.danger:danger-kotlin-sdk:<latest_version>"))
}
```

and then setup your test

```kotlin
class FailWithRetryMessageTest {
  private val dangerContext = TestDangerContext()

  @BeforeEach
  fun setUp() {
    ExamplePlugin.registeredContext = dangerContext
  }

  @Test
  fun `failWithRetryMessage only posts message once`() {
    // given
    val fail1 = "Test failure 1"
    val fail2 = "Test failure 2"

    // when
    ExamplePlugin.failWithRetryMessage(fail1)
    ExamplePlugin.failWithRetryMessage(fail2)

    // then
    expectThat(dangerContext.messages)
      .hasSize(1)
      .containsExactly(Violation(ExamplePlugin.retryMessage))

    expectThat(dangerContext.fails)
      .hasSize(2)
      .containsExactly(Violation(fail1), Violation(fail2))
  }
}
```
