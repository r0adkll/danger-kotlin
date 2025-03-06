# Usage

Danger has a few run commands, but we will focus on the main ones you'll use here.

## Local
The `local` command runs Danger without the PR metadata which is helpful for local testing checks and rules against code/commit changes. This can be helpful in git hooks or just for local testing.

### Command
```shell
danger-kotlin local
```

### Options
```shell
Options:
  -s, --staging                             Just use staged changes.
  -b, --base [branch_name]                  Use a different base branch (default: "master")
  -j, --outputJSON                          Outputs the resulting JSON to STDOUT
  -v, --verbose                             Verbose output of files
  -d, --dangerfile [filePath | remote url]  Specify a custom dangerfile path, remote urls only work with github
  -i, --id [danger_id]                      Specify a unique Danger ID for the Danger run
```

## Pull Requests
The `pr` command emulates running Danger against an existing GitHub PR. This will allow you to test your rules and checks against the PR metadata.

### Command

```shell
danger-kotlin pr https://github.com/user/repo/pulls/10
```

### Options

```shell
Options:
  -J, --json                                Output the raw JSON that would be passed into `danger process` for this PR.
  -j, --js                                  A more human-readable version of the JSON.
  -v, --verbose                             Verbose output of files
  -d, --dangerfile [filePath | remote url]  Specify a custom dangerfile path, remote urls only work with github
  -i, --id [danger_id]                      Specify a unique Danger ID for the Danger run
  -c, --external-ci-provider [modulePath]   Specify custom CI provider
```
