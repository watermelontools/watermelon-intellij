package com.watermelon.context.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.vcsUtil.VcsUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    fun getGitBlame(): ArrayList<String> {
        // Get the file being currently edited
        val project = ProjectManager.getInstance().openProjects[0]
        val editor: Editor = FileEditorManager.getInstance(project).selectedTextEditor!!
        val document = editor.document
        val file = FileDocumentManager.getInstance().getFile(document!!)

        // Get file path
        val filePath = VcsUtil.getFilePath(file!!);

        // Get selection line numbers
        val selectionModel = editor.selectionModel
        val startLine = editor.document.getLineNumber(selectionModel.selectionStart)
        val endLine = editor.document.getLineNumber(selectionModel.selectionEnd)
        println("startLine $startLine")
        println("endLine $endLine")
        val history = git4idea.history.GitFileHistory;

        val blameResult = history.collectHistory(project, filePath)
        val commitHashes = ArrayList<String>()
        val commitMessages = ArrayList<String>()
        blameResult.forEach {
            val commitMessageWithAuthor = "${it.author}: ${it.commitMessage}"
            val stringElement = it.toString().split(":")[1]
            commitHashes.add(stringElement)
            commitMessages.add(commitMessageWithAuthor)
        }

        // TODO: Run a blame range correctly
        // val rangeBlame = blameResult.slice(start..end);
        // println("rangeblame: $rangeBlame");

        return (commitMessages);
    }

}
