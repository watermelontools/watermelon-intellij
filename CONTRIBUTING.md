# Contributing to Watermelon's IntelliJ extension development

For now, we discuss on [Discord](https://t.co/fMIlnb9egq).

Anyone is free to contribute changes to any file in this repository. You don't need to ask for permission or get in line. If you see an issue that's open and it seems interesting to you, feel free to pick it up. Your solution may be better. Open-source is beautiful. 

## Running the extension
To run:
1. Open the IntelliJ navbar
2. Go to Run Configurations -> Run Plugin

Make sure you:
1. Are using Java 17
2. Are using Corretto 19 as the Gradle JVM

When commiting, our all the tests will run to check nothing broke.

## Issues
If there's something you'd like to see please [open an issue](https://github.com/watermelontools/watermelon-intellij/issues/new).

## PRs

We love community contributions. Please fork the repo and send a PR our way.

Remember, we'll discuss it publicly, it's a great opportunity to learn.

### Sources

#### IntelliJ
- [The Offical IntelliJ Platform Plugin SDK docs](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [The IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

#### APIs of the integrations our backend currently supports

- [Octokit](https://octokit.github.io/)
- [GitLab API](https://docs.gitlab.com/ee/api/)
- [Bitbucket API](https://developer.atlassian.com/server/bitbucket/rest/v811/)
- [Jira API](https://developer.atlassian.com/cloud/jira/platform/rest/v3/intro/#about)
- [Linear API](https://developers.linear.app/docs/)
- [Notion API](https://developers.notion.com/)
- [Slack API](https://api.slack.com/)

## Brand

We prefer to use [Codicons](https://microsoft.github.io/vscode-codicons/dist/codicon.html) and [Primer Design](https://primer.style/) for our extension, but are elastic in UI decisions.

## Donations
[We have Github Sponsors](https://github.com/sponsors/watermelontools)  
Also star :star: the repo to help!

## Release

To release a new version of the extension you need to be part of the Watermelon JetBrains Marketplace organization. 

To build a new version run the Gradle Build task. This will generate a .zip located inside the /build/distributions directory. Upload that zip to the marketplace. 
