package com.github.baristageek.watermelonintellij.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.baristageek.watermelonintellij.MyBundle
import com.intellij.openapi.project.ProjectManager
import com.intellij.vcsUtil.VcsUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.commands.Git;
import git4idea.repo.GitRepositoryManager


@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun getGitBlame() : ArrayList<String> {
        // Get the file being currently edited
        val project = ProjectManager.getInstance().openProjects[0]
        val editor: Editor = FileEditorManager.getInstance(project).selectedTextEditor!!
        val document = editor?.document
        val file = FileDocumentManager.getInstance().getFile(document!!)

        // Get file path
        val filePath = VcsUtil.getFilePath(file!!);

        // Get selection line numbers
        val selectionModel = editor.selectionModel
        val startLine = editor.document.getLineNumber(selectionModel.selectionStart)
        val endLine = editor.document.getLineNumber(selectionModel.selectionEnd)

        val history = git4idea.history.GitFileHistory;

        //     old approach   val blameResult = history.collectHistory(project, filePath)
        val repository = GitRepositoryManager.getInstance(project).repositories[0]
        println("repo root: ${repository.root}")
        val h = GitLineHandler(project, repository.root, GitCommand.BLAME)
        h.addParameters("-p", "-L", "$startLine,$endLine")
        h.endOptions()
        h.addRelativeFiles(listOf(filePath.virtualFile))

        val result = Git.getInstance().runCommand(h)
        val output = result.getOutputOrThrow()
        println(output)
        // fix blame range above


        val commitHashes = ArrayList<String>()
//        val commitMessages = ArrayList<String>()
        val commitMessages = arrayListOf("jskfljsf")

//        blameResult.forEach {
//            val commitMessageWithAuthor = "${it.author}: ${it.commitMessage}"
//            val stringElement = it.toString().split(":")[1]
//            commitHashes.add(stringElement)
//            commitMessages.add(commitMessageWithAuthor)
//        }

        return (commitMessages);
    }

}
