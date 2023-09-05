package com.watermelon.context.toolWindow

import MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import java.awt.Font
import javax.swing.BoxLayout
import javax.swing.BorderFactory
import java.awt.Dimension
import javax.swing.Box
import javax.swing.JPanel

class MyToolWindowFactory : ToolWindowFactory {

    fun createToolWindowContent(project: Project, toolWindow: ToolWindow, startLine: Int, endLine: Int) {
        // this only runs once
        val myToolWindow = MyToolWindow(toolWindow)
        val content =
            ContentFactory.getInstance().createContent(myToolWindow.getContent(startLine, endLine), null, false)
        toolWindow.contentManager.removeAllContents(true)
        toolWindow.contentManager.addContent(content)
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {

        // this only runs once
        val myToolWindow = MyToolWindow(toolWindow)
        val content =
            ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.removeAllContents(true)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent(startLine: Int = 0, endLine: Int = 0) = JBPanel<JBPanel<*>>().apply {

            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

            val titleLabel = JBLabel("Commit history").apply {
                font = font.deriveFont(Font.BOLD, 16f)
            }
            add(titleLabel)

            add(Box.createRigidArea(Dimension(0, 10)))

            val commitHashes = if (startLine == 0 && endLine == 0) {
                service.getGitBlame()
            } else {
                service.getPartialGitBlame(startLine, endLine)
            }
            commitHashes?.forEach { commitHash ->

                val (hash, author, dateTime, lineNumber, message) = commitHash
                val commitLabel = JBLabel("$hash: $message").apply {
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
