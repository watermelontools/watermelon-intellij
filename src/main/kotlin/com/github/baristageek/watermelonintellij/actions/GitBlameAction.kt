package com.github.baristageek.watermelonintellij.actions

import com.github.baristageek.watermelonintellij.services.MyProjectService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ToolWindow
import com.github.baristageek.watermelonintellij.toolWindow.MyToolWindowFactory
import com.intellij.openapi.components.service
import com.intellij.ui.content.ContentFactory

class GitBlameAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {

        // open tool window programmatically
        val project = ProjectManager.getInstance().getOpenProjects()[0]
        val toolWindowManager = ToolWindowManager.getInstance(project)
        val toolWindow: ToolWindow? = toolWindowManager.getToolWindow("🍉 Watermelon")

//         val service = toolWindow.project.service<MyProjectService>()
        val service = toolWindow?.project?.service<MyProjectService>()

        toolWindow?.contentManager?.removeAllContents(true)
        println("GitBlmaeAction - removeAllContents()")

        service?.getGitBlame();


        val toolWindowFactory = MyToolWindowFactory()
        toolWindowFactory.createToolWindowContent(project, toolWindow!!)


        toolWindow?.show {}
    }

}

