package com.github.baristageek.watermelonintellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import git4idea.*
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcsUtil.VcsUtil;

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
    fun gitBlame(filePath: FilePath, start: Int, end: Int) {
        val project = ProjectManager.getInstance().getOpenProjects()[0]
        val history = git4idea.history.GitFileHistory;

        val blameResult = history.collectHistory(project, filePath)
        val rangeBlame = blameResult.slice(start..end)
        
        println("rangeblame: $rangeBlame");
    }

}

