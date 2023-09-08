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
import java.awt.Font
import java.awt.Dimension
import com.intellij.ide.passwordSafe.PasswordSafe
import kotlinx.serialization.json.*
import java.awt.CardLayout
import java.net.HttpURLConnection
import java.net.URL
import javax.swing.*
import javax.swing.*

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


        class ExpandablePanel(title: String, body: String) : JPanel() {
            init {
                println("ExpandablePanel $title $body")
                val titleButton = JButton(title)
                val bodyLabel = JLabel(body)

                val titlePanel = JPanel()
                titlePanel.add(titleButton)

                val expandedPanel = JPanel()
                expandedPanel.layout = BoxLayout(expandedPanel, BoxLayout.Y_AXIS)
                expandedPanel.add(titleButton)
                expandedPanel.add(bodyLabel)

                add(titlePanel, "TitleOnly")
                add(expandedPanel, "Expanded")

                titleButton.addActionListener {
                    println("Clicked $title")
                }
            }


        }


        private val service = toolWindow.project.service<MyProjectService>()
        private fun extractValuesFromData(dataJson: JsonObject): List<Any?> {
            return dataJson.keys.asSequence().map { key: String -> key }.toList()
        }

        fun getContent(startLine: Int = 0, endLine: Int = 0) = JBPanel<JBPanel<*>>().apply {
            val passwordSafe = PasswordSafe.instance
            val id = passwordSafe.getPassword(CredentialAttributes("WatermelonContext.id"))
            val email = passwordSafe.getPassword(CredentialAttributes("WatermelonContext.email"))
            val apiUrl = "http://localhost:3000/api/extension/getContext"
            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection

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
            try {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Charset", "UTF-8")
                val commitMessages = commitHashes?.map { commitHash -> commitHash.message } ?: listOf<String>()
                val commitListJson = commitMessages.joinToString(prefix = "[\"", separator = "\",\"", postfix = "\"]")

                val payload = """
    {
        "email": $email,
        "id": $id,
        "repo": "watermelon",
        "owner": "watermelontools",
        "commitList": ${commitListJson}
    }
""".trimIndent()

                connection.outputStream.write(payload.toByteArray())

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val jsonResponse = Json.parseToJsonElement(connection.inputStream.reader().readText()).jsonObject
                    val data = jsonResponse["data"]?.jsonObject
                    val serviceList = extractValuesFromData(data!!)
                    for (service in serviceList) {
                        val titleLabel = JBLabel("$service").apply {
                            font = font.deriveFont(Font.BOLD, 16f)
                        }
                        add(titleLabel)
                        add(Box.createRigidArea(Dimension(0, 10)))
                        val serviceData = data[service.toString()]

                        when (serviceData) {
                            is JsonObject -> {
                                // Handle JsonObject case
                            }

                            is JsonArray -> {
                                if (serviceData.isEmpty()) {
                                    val commitLabel = JBLabel("No $service results found").apply {
                                        font = font.deriveFont(Font.PLAIN, 14f)
                                    }
                                    add(commitLabel)
                                } else {
                                    serviceData.forEach { serviceDataElement ->
                                        val serviceDataValueJson = serviceDataElement.jsonObject
                                        val title = serviceDataValueJson["title"]?.jsonPrimitive?.content
                                        val body = serviceDataValueJson["body"]?.jsonPrimitive?.content

                                        val expandablePanel = ExpandablePanel("$title", "$body")
                                        add(expandablePanel)
                                    }
                                }
                            }

                            is JsonPrimitive -> {
                                val message = serviceData.content
                                if (message.contains(Regex("no .* token"))) {
                                    val commitLabel = JBLabel("Click here to login to $service").apply {
                                        font = font.deriveFont(Font.PLAIN, 14f)
                                    }
                                    add(commitLabel)
                                }
                            }

                            else -> {
                                // Handle any other cases if needed
                            }
                        }
                    }

                    connection.inputStream.reader().readText()
                } else {
                    // Handle non-200 HTTP responses
                    //println("Error: $responseCode")
                }

            } finally {
                connection.disconnect()
            }
        }
    }
}

