package com.watermelon.context.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.vcsUtil.VcsUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitCommandResult
import git4idea.commands.GitLineHandler

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {
    // Get the file being currently edited
    private val project = ProjectManager.getInstance().openProjects[0]
    private val editor: Editor = FileEditorManager.getInstance(project).selectedTextEditor!!
    private val document = editor.document
    private val file = FileDocumentManager.getInstance().getFile(document)
    private val directory = file?.parent
    private val filePath = VcsUtil.getFilePath(file!!);
    fun getGitBlame(): ArrayList<String> {
        val commitMessages = ArrayList<String>()
        // print changes for current revision
        val blameRun = directory?.let {
            git4idea.commands.GitLineHandler(
                project, it, GitCommand.BLAME,
            ).apply {
                addParameters("$filePath")
            }
        }

        fun runCommand(lineHandler: GitLineHandler): GitCommandResult {
            return Git.getInstance().runCommand(lineHandler)
        }

        val blameCommandResponse = blameRun?.let { runCommand(it) }
        println("blameCommandResponse: $blameCommandResponse");

        return (commitMessages);
    }

    fun getPartialGitBlame(startLine: Int, endLine: Int): ArrayList<String> {
        // Get the file being currently edited

        val commitMessages = ArrayList<String>()
        // print changes for current revision
        val blameRun = directory?.let {
            git4idea.commands.GitLineHandler(
                project, it, GitCommand.BLAME,
            ).apply {
                addParameters("-L$startLine,$endLine")
                addParameters("$filePath")
            }
        }

        fun runCommand(lineHandler: GitLineHandler): GitCommandResult {
            return Git.getInstance().runCommand(lineHandler)
        }

        val blameCommandResponse = blameRun?.let { runCommand(it) }
        println("blameCommandResponse: $blameCommandResponse");

        return (commitMessages);
    }
}
