package com.github.baristageek.watermelonintellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ToolWindow


class GitBlameAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // open tool window programmatically
        val project = ProjectManager.getInstance().getOpenProjects()[0]
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow: ToolWindow? = toolWindowManager.getToolWindow("MyToolWindow")
        toolWindow?.show {}
    }

}

