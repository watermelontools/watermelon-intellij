package com.github.baristageek.watermelonintellij.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.github.baristageek.watermelonintellij.MyBundle
import com.github.baristageek.watermelonintellij.services.MyProjectService
import javax.swing.JButton
import java.awt.Font
import com.intellij.ui.JBColor
import java.awt.Color
import com.github.baristageek.watermelonintellij.actions.GitBlameAction
import com.intellij.openapi.vcs.FilePath


class MyToolWindowFactory : ToolWindowFactory {
    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val myToolWindow = MyToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(myToolWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class MyToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<MyProjectService>()

        fun getContent() = JBPanel<JBPanel<*>>().apply {
            // The scope of the commit hashes must be here
            val commitsLabel = JBLabel(("Commit Hashes"))
            add(commitsLabel);

            // TODO: Run val gitBlameAction = GitBlameAction(); here
            val mockedCommitHashes = arrayOf(
                "abc123def456789ghijklmn0pqrstuvwx",
                "def456789ghijklmn0pqrstuvwxabc123",
                "789ghijklmn0pqrstuvwxabc123def456",
                "ijklmn0pqrstuvwxabc123def456789g",
                "mn0pqrstuvwxabc123def456789ghijk",
                "rstuvwxabc123def456789ghijklmn0p",
                "vwxabc123def456789ghijklmn0pqrstu"
            )

            mockedCommitHashes.forEach { commitHash ->
                add(JBLabel(commitHash))
            }

        }
    }
}
