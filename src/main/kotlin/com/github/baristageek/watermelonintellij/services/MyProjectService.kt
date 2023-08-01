package com.github.baristageek.watermelonintellij.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.github.baristageek.watermelonintellij.MyBundle
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.vcsUtil.VcsUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.application.ApplicationManager

@Service(Service.Level.PROJECT)
class MyProjectService(project: Project) {

    init {
        thisLogger().info(MyBundle.message("projectService", project.name))
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    fun getRandomNumber() = (1..100).random()
    fun getGitBlame() : ArrayList<String> {
        // Get virtual file
//        val editor: Editor = PlatformDataKeys.EDITOR
        val editor: Editor = EditorFactory.getInstance().allEditors[0]
        val document = editor?.document
        val file = FileDocumentManager.getInstance().getFile(document!!)

        // Get file path
        val filePath = VcsUtil.getFilePath(file!!);


        // Get selection line numbers
        val selectionModel = editor.selectionModel
        val startLine = editor.document.getLineNumber(selectionModel.selectionStart)
        val endLine = editor.document.getLineNumber(selectionModel.selectionEnd)

        val project = ProjectManager.getInstance().getOpenProjects()[0]
        val history = git4idea.history.GitFileHistory;

        val blameResult = history.collectHistory(project, filePath)
        val commitHashes = ArrayList<String>()
        blameResult.forEach {
            val stringElement = it.toString().split(":")[1]
            commitHashes.add(stringElement)
            println("stringElement: $stringElement")
        }

        // TODO: Run a blame range correctly
        // val rangeBlame = blameResult.slice(start..end);
//        println("rangeblame: $rangeBlame");

        // open tool window programmatically
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow: ToolWindow? = toolWindowManager.getToolWindow("MyToolWindow")
        toolWindow?.show {}
        return (commitHashes);
    }

}
