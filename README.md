# Watermelon for IntelliJ

[![Report an issue](https://img.shields.io/badge/-Report%20an%20issue-critical)](https://github.com/watermelontools/watermelon-intellij/issues)

![GitHub commit activity (branch)](https://img.shields.io/github/commit-activity/m/watermelontools/watermelon-intellij?style=flat-square)
[![GitHub Repo stars](https://img.shields.io/github/stars/watermelontools/watermelon-intellij?style=flat-square)](https://github.com/watermelontools/watermelon-intellij/stargazers)
[![Contributors](https://img.shields.io/github/contributors/watermelontools/watermelon-intellij?style=flat-square)](https://github.com/watermelontools/watermelon-intellij/graphs/contributors)
![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/22251-watermelon-intellij-template?style=flat-square)
[![OpenSSF Scorecard](https://api.securityscorecards.dev/projects/github.com/watermelontools/watermelon-intellij/badge)](https://securityscorecards.dev/viewer/?uri=github.com/watermelontools/watermelon-intellij)
[![Twitter Follow](https://img.shields.io/twitter/follow/WatermelonTools?style=flat-square)](https://twitter.com/intent/follow?screen_name=WatermelonTools)
[![Discord](https://img.shields.io/discord/933846506438541492?style=flat-square)](https://discord.com/invite/H4AE6b9442)

<!-- Plugin description -->
**Watermelon is an Open Source Copilot For Code Review**. Our GitHub application allows developers to pre-review GitHub Pull Requests by tracing their code context and performing static code analysis. Using LLMs to detect errors, compare intent to implementation, and give the PR a first health check.

We've built a search algorithm that indexes the most relevant [code context](https://www.watermelontools.com/post/what-is-passive-code-documentation-why-is-it-hard-to-scale-what-to-do-about-it) for a given block of code.

To obtain code context in your IDE, use this extension.

To obtain code context in your CI/CD, take a look at our [GitHub Application](https://github.com/watermelontools/watermelon)
<!-- Plugin description end -->

## Integrations

We currently support the following integrations

| Watermelon Product | Git                                     | Project Management | Messaging | Documentation |
|:-------------------|:----------------------------------------| :----------------- | :-------- | :-----------  |
| IntelliJ           | GitHub, GitLab (Beta), Bitbucket (Beta)                          | Jira               | Slack     | Notion, Confluence              |
| VS Code            | GitHub, GitLab (Beta), Bitbucket (Beta) | Jira               | Slack     |               |
| GitHub App         | GitHub.                                 | Jira, Linear               | Slack     | Notion, Confluence        |

## Features

Watermelon's IntelliJ plugin allows you to obtain business context for a file or block of code via:

- Right click menu button "Run Watermelon"
- Hover to get context (coming soon)

[//]: # (TODO: Product screenshot here)

## Running the plugin
- You must have Gradle installed
- For best results, use Java 17
- The Gradle JDK we use is Corretto 19
- If you build the plugin and it fails, it will be common that running `gradle clean` on your CLI will be the solution
- To publish the plugin to the JetBrains marketplace: Run the Gradle build task that generates a .zip of the build, then go to https://plugins.jetbrains.com/plugin/add#intellij and upload

## Requirements
- You must have Git locally installed (try `git --version` or [install it now](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git))

## Installation
Download from the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/22720-watermelon-context).

## Contributing
Check out [Contributing.md](CONTRIBUTING.md) and be aware of the [Code of Conduct](CODE_OF_CONDUCT.md)!

We're an early-stage project, therefore we still have the luxury to coordinate via short chats with our contributors. If you're interested in contributing, please join our [Discord](https://discord.com/invite/H4AE6b9442) community.
Alternatively, comment on our issues if you plan to solve one.

[![Report an issue](https://img.shields.io/badge/-Report%20an%20issue-critical)](https://github.com/watermelontools/watermelon-intellij/issues)

## Analytics
Watermelon [doesn't store your code](https://www.watermelontools.com/post/building-a-code-archeology-toolbox-without-storing-your-code). In our commitment to transparency, we made our API (search engine) source-available. 

## Supporters

[![Stargazers repo roster for @watermelontools/watermelon-intellij](https://reporoster.com/stars/watermelontools/watermelon-intellij)](https://github.com/watermelontools/watermelon-intellij/stargazers)

[![Forkers repo roster for @watermelontools/watermelon-intellij](https://reporoster.com/forks/watermelontools/watermelon-intellij)](https://github.com/watermelontools/watermelon-intellij/network/members)

---

#### About Watermelon

Watermelon is built by a globally distributed team of developers devoted to making software development easier. Join our [Discord](https://discord.com/invite/H4AE6b9442) community, follow us on [Twitter](https://twitter.com/WatermelonTools) and go to the [Watermelon blog](https://watermelon.tools/blog) to get the best programming tips.

### License

- [Apache License](LICENSE.md)
