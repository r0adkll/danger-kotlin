# Advanced Dangerfile

You can perform more intensive checks such as network requests, disk reads, etc by using coroutines via `runBlocking { … }`

**Example**

```kotlin
danger(args) {
  //...

  runBlocking {
    val result = client.get("https://example.com/api/do-stuff?q=foo")
    if (result.isSuccess) {
      message("I did the thing!")
    }
  }
}
```

Or you can even run concurrent tasks using `async {}`

```kotlin
danger(args) {
  // ...

  runBlocking {
    async { /* Do stuff */ }
    async { /* Do stuff */ }
    async { /* Do stuff */ }
  }

  message("All async tasks have finished!")
}
```
