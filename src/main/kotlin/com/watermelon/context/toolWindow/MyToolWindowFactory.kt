package com.watermelon.context.toolWindow

import MyProjectService
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.content.ContentFactory
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.ui.components.JBScrollPane
import kotlinx.serialization.json.*
import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.HttpURLConnection
import java.net.URL
import javax.swing.*

class MyToolWindowFactory : ToolWindowFactory {

    data class ServiceData(val title: String, val body: String, val link: String? = null)

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
        val backendUrl = "https://app.watermelontools.com"

        private val service = toolWindow.project.service<MyProjectService>()

        class ExpandablePanel(title: String, body: String) : JPanel() {
            private val cardLayout = CardLayout()
            override fun getMaximumSize(): Dimension {
                return Dimension(parent?.width ?: super.getMaximumSize().width, super.getMaximumSize().height)
            }

            init {
                // Assuming the rest of your code remains the same...

                val titleButton = JButton(title)

                val bodyTextArea = JTextArea(body).apply {
                    wrapStyleWord = true
                    lineWrap = true
                    isEditable = false
                    isOpaque = false
                    border = null
                    background = null
                    font = UIManager.getFont("Button.font")
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                }


                val titlePanel = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    maximumSize = Dimension(10, 10)
                }
                val expandedPanel = JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.Y_AXIS)
                    maximumSize = Dimension(10, 10)
                }

                titlePanel.add(titleButton)
                expandedPanel.add(bodyTextArea)


                // Use CardLayout for ExpandablePanel
                layout = cardLayout
                add(titlePanel, "TitleOnly")
                add(expandedPanel, "Expanded")

                val switchPanelListener = ActionListener {
                    remove(titlePanel)
                    add(expandedPanel)
                    revalidate()
                    repaint()
                }

                titleButton.addActionListener(switchPanelListener)
                bodyTextArea.addMouseListener(object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        remove(expandedPanel)
                        add(titlePanel)
                        revalidate()
                        repaint()
                    }
                })
            }
        }


        private fun setupServiceUI(serviceDataArray: List<ServiceData>, serviceName: String): JBPanel<JBPanel<*>> {
            return JBPanel<JBPanel<*>>().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

                // Create a panel with FlowLayout to hold the titleLabel
                val titlePanel = JPanel(FlowLayout(FlowLayout.LEFT))
                val titleLabel = JBLabel(serviceName).apply {
                    font = font.deriveFont(Font.BOLD, 16f)
                }
                titlePanel.add(titleLabel)
                add(titlePanel)

                serviceDataArray.forEach { data ->
                    val expandablePanel = ExpandablePanel(data.title, data.body)
                    add(expandablePanel)
                }
            }
        }


        private fun makeApiCall(commitMessages: List<String>, email: String?, id: String?): JsonObject? {
            val apiUrl = "$backendUrl/api/extension/getContext"
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Charset", "UTF-8")

                val commitListJson = commitMessages.joinToString(prefix = "[\"", separator = "\",\"", postfix = "\"]")
                val payload = """
        {
            "email": $email,
            "id": $id,
            "repo": "watermelon",
            "owner": "watermelontools",
            "commitList": $commitListJson
        }
    """.trimIndent()
                connection.outputStream.write(payload.toByteArray())
                val responseCode = connection.responseCode
                return if (responseCode == HttpURLConnection.HTTP_OK) {
                    Json.parseToJsonElement(connection.inputStream.reader().readText()).jsonObject
                } else {
                    null
                }
            } finally {
                connection.disconnect()
            }
        }


        fun getContent(startLine: Int = 0, endLine: Int = 0): JComponent = JBPanel<JBPanel<*>>().apply {
            val mainPanel = JBPanel<JBPanel<*>>()
            mainPanel.layout = BoxLayout(mainPanel, BoxLayout.Y_AXIS)  // For vertical stacking
            val passwordSafe = PasswordSafe.instance
            val id = passwordSafe.getPassword(CredentialAttributes("WatermelonContext.id"))
            val email = passwordSafe.getPassword(CredentialAttributes("WatermelonContext.email"))
            var servicePanels = emptyList<JBPanel<*>>()
            val commitHashes = if (startLine == 0 && endLine == 0) {
                service.getGitBlame()
            } else {
                service.getPartialGitBlame(startLine, endLine)
            }

            val commitMessages = commitHashes?.map { commitHash -> commitHash.message } ?: listOf<String>()

            val commitList = commitHashes?.map { commitHash ->
                ServiceData(
                    title = commitHash.message,
                    body = "${commitHash.hash} - ${commitHash.author} - ${commitHash.date}"
                )
            }!!
            servicePanels = servicePanels + (setupServiceUI(commitList, "Commits"))

            if (email.isNullOrEmpty() && !id.isNullOrEmpty()) {
                servicePanels.forEach { servicePanel ->
                    mainPanel.add(servicePanel)
                }

                return JBScrollPane(mainPanel)
            }

            val apiResponse = makeApiCall(commitMessages, email, id)
            val data = apiResponse?.get("data")?.jsonObject
            val serviceNames = data?.keys?.asSequence()?.map { key: String -> key }?.toList()
            if (serviceNames != null) {
                for (serviceName in serviceNames) {
                    when (val serviceData = data[serviceName]) {
                        is JsonObject -> {
                            // Handle JsonObject case
                        }

                        is JsonArray -> {
                            if (serviceData.isEmpty()) {
                                val commitPanel = setupServiceUI(emptyList(), serviceName)
                                servicePanels = servicePanels + (commitPanel)
                                add(commitPanel)
                            } else {

                                val returnArray = serviceData.map { serviceDataElement ->
                                    val serviceDataValueJson = serviceDataElement.jsonObject
                                    val title = serviceDataValueJson["title"]?.jsonPrimitive?.content
                                    val body = serviceDataValueJson["body"]?.jsonPrimitive?.content
                                    val link = serviceDataValueJson["link"]?.jsonPrimitive?.content
                                    ServiceData(title ?: "", body ?: "", link)
                                }
                                servicePanels = servicePanels + (setupServiceUI(
                                    returnArray,
                                    "$serviceName (${serviceData.size})"
                                ))

                            }
                        }

                        is JsonPrimitive -> {
                            val message = serviceData.content
                            if (message.contains(Regex("no .* token"))) {

                                servicePanels = servicePanels + (setupServiceUI(emptyList(), serviceName ?: ""))
                            }
                        }

                        else -> {
                            // Handle any other cases if needed
                        }
                    }
                }
            }
            // add the servicePanels to the UI
            servicePanels.forEach { servicePanel ->
                mainPanel.add(servicePanel)
            }

            return JBScrollPane(mainPanel)
        }
    }
}

