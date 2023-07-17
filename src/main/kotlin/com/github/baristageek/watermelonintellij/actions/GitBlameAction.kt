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
        val vfPath = file.canonicalPath;
        val weirdThing = VcsUtil.getFilePath(file.getPath())
//        FilePath filePath = VcsUtil.getFilePath(selectedFile);
        val filePath = VcsUtil.getFilePath(file);

        println("file path after parsing: $filePath")

        println("file: ${file}, file.path: ${file.path}, canonical path: $vfPath")
        // Convert VirtualFile to FilePath
        println("weird thing: $weirdThing")

        // Get selection line numbers
        val selectionModel = editor.selectionModel
        val startLine = editor.document.getLineNumber(selectionModel.selectionStart)
        val endLine = editor.document.getLineNumber(selectionModel.selectionEnd)

//        gitBlame(file.path, startLine, endLine)
//        gitBlame(file.path as VirtualFile, startLine, endLine)
        gitBlame(filePath, startLine, endLine)

    }

    //  fun gitBlame(filePath: String, start: Int, end: Int) {
    //      println("selected file, start and end line: $filePath $start $end")
    //  }

    fun gitBlame(filePath: FilePath, start: Int, end: Int) {
//         println("selected file, start and end line: $vfPath $start $end")

        val project = ProjectManager.getInstance().getOpenProjects()[0]
//        println("project: $project")

        val history = git4idea.history.GitFileHistory;
//        val blameResult = history.collectHistory(project, filePath, start, end)
//        println("history: $history")


        println("blame result will try to execute here: ")
//        val blameResult = history.collectHistory(project, filePath, start.toString(), end.toString())
        val blameResult = history.collectHistory(project, filePath)


        println("Blame result: $blameResult");
    }

}

