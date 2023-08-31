package com.watermelon.context.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.watermelon.context.services.MyProjectService
import java.awt.Font
import javax.swing.BoxLayout
import javax.swing.BorderFactory
import java.awt.Dimension
import javax.swing.Box
import javax.swing.JPanel

class MyToolWindowFactory : ToolWindowFactory {
    init {
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // this only runs once
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {

            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

            val titleLabel = JBLabel("Commit history").apply {
                font = font.deriveFont(Font.BOLD, 16f)
            }
            add(titleLabel)

            add(Box.createRigidArea(Dimension(0, 10)))

            val commitHashes = service.getGitBlame();

            commitHashes.forEach { commitHash ->
                val commitLabel = JBLabel(commitHash).apply {
                    font = font.deriveFont(Font.PLAIN, 14f)
                }
                add(commitLabel)

                // Add a panel with vertical flow layout
                // This will force each label onto a new line
                val panel = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                }
                add(panel)
            }
        }
    }
}
