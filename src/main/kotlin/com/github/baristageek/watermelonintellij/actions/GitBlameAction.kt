package com.github.baristageek.watermelonintellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile

import com.intellij.openapi.actionSystem.PlatformDataKeys

class GitBlameAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        // get current file
        val editor = e.getRequiredData(PlatformDataKeys.EDITOR)
        val file = e.getRequiredData(PlatformDataKeys.VIRTUAL_FILE)


        // Get selection line numbers
        val selectionModel = editor.selectionModel
        val startLine = editor.document.getLineNumber(selectionModel.selectionStart)
        val endLine = editor.document.getLineNumber(selectionModel.selectionEnd)

        gitBlame(file.path, startLine, endLine)

    }

    fun gitBlame(filePath: String, start: Int, end: Int) {
        println("selected file, start and end line: $filePath $start $end")
    }
}

