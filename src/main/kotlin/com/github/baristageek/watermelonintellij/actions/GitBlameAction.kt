package com.github.baristageek.watermelonintellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.vcsUtil.VcsUtil
import com.intellij.openapi.wm.ToolWindow


class GitBlameAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // get current file
        val editor = e.getRequiredData(PlatformDataKeys.EDITOR)
        val file = e.getRequiredData(PlatformDataKeys.VIRTUAL_FILE)
        val filePath = VcsUtil.getFilePath(file);

        // Get selection line numbers
        val selectionModel = editor.selectionModel
        val startLine = editor.document.getLineNumber(selectionModel.selectionStart)
        val endLine = editor.document.getLineNumber(selectionModel.selectionEnd)

        gitBlame(filePath, startLine, endLine)
    }
    fun gitBlame(filePath: FilePath, start: Int, end: Int): ArrayList<String> {
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

