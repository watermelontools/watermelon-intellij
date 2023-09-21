package com.watermelon.context.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowManager
import com.watermelon.context.toolWindow.MyToolWindowFactory
import com.watermelon.context.utils.PostHog
import com.intellij.openapi.application.PermanentInstallationID

class ContextMenuButton : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val editor = FileEditorManager.getInstance(project!!).selectedTextEditor
        if (editor != null) {
            val selectionModel = editor.selectionModel
            val startLine = selectionModel.selectionStartPosition?.line
            val endLine = selectionModel.selectionEndPosition?.line
            val toolWindowManager = ToolWindowManager.getInstance(project)
            val toolWindow: ToolWindow? = toolWindowManager.getToolWindow("🍉 Watermelon")
            val toolWindowFactory = MyToolWindowFactory()

            if (startLine != null) {
                if (endLine != null) {
                    println("Start line : $startLine")
                    println("End line : $endLine")
                    toolWindowFactory.createToolWindowContent(project, toolWindow!!, startLine, endLine)
                }
            }

            val uuid = PermanentInstallationID.get();
            PostHog.posthog.capture(uuid,
                "intelliJ:GetCodeContext");

            toolWindow?.show {}
        }
    }
}

