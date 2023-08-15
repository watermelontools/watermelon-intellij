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
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import javax.swing.BoxLayout
import javax.swing.BorderFactory

import java.awt.Dimension
import javax.swing.Box
import javax.swing.JPanel


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

//        fun getContent() = JBPanel<JBPanel<*>>().apply {
//            val commitsLabel = JBLabel(("Git"))
//            commitsLabel.font = commitsLabel.font.deriveFont(Font.BOLD)
//            commitsLabel.foreground = JBColor(Color(0x999999), Color(0x999999))
//            add(commitsLabel);
//
//            val commitHashes = service.getGitBlame();
//
//            commitHashes.forEach { commitHash ->
//                add(JBLabel(commitHash))
//            }
//        }

        fun getContent() = JBPanel<JBPanel<*>>().apply {

            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

            val titleLabel = JBLabel("Commit History").apply {
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
