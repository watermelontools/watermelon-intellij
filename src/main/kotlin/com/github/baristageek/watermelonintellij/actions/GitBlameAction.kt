package com.watermelon.context.actions

import com.watermelon.context.services.MyProjectService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ToolWindow
import com.watermelon.context.toolWindow.MyToolWindowFactory
import com.intellij.openapi.components.service
import com.intellij.ui.content.ContentFactory

class GitBlameAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // open tool window programmatically
        // we removeAllContents() and then createToolWindowContent once again
        val project = ProjectManager.getInstance().openProjects[0]
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow: ToolWindow? = toolWindowManager.getToolWindow("🍉 Watermelon")

        val service = toolWindow?.project?.service<MyProjectService>()
        toolWindow?.contentManager?.removeAllContents(true)

        service?.getGitBlame();

        val toolWindowFactory = MyToolWindowFactory()
        toolWindowFactory.createToolWindowContent(project, toolWindow!!)

        toolWindow.show {}
    }

}

