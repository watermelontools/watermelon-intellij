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
        private val service = toolWindow.project.service<MyProjectService>()

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

        private fun setupServiceUI(serviceDataArray: List<ServiceData>, serviceName: String) =
            JBPanel<JBPanel<*>>().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)

                border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

                val titleLabel = JBLabel(serviceName).apply {
                    font = font.deriveFont(Font.BOLD, 16f)
                }
                add(titleLabel)
                for (serviceData in serviceDataArray) {
                    val title = serviceData.title
                    val body = serviceData.body

                    val expandablePanel = ExpandablePanel(title, body)
                    add(expandablePanel)
                }
            }


        private fun makeApiCall(commitMessages: List<String>, email: String?, id: String?): JsonObject? {
            val apiUrl = "http://localhost:3000/api/extension/getContext"
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


        fun getContent(startLine: Int = 0, endLine: Int = 0) = JBPanel<JBPanel<*>>().apply {
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
            println("commitMessages $commitMessages")
            val apiResponse = makeApiCall(commitMessages, email, id)
            val data = apiResponse?.get("data")?.jsonObject

            val commitList = commitHashes?.map { commitHash ->
                ServiceData(
                    title = commitHash.message,
                    body = "${commitHash.hash} - ${commitHash.author} - ${commitHash.date}"
                )
            }!!
            servicePanels.plus(setupServiceUI(commitList, "Commits"))

            val serviceNames = data?.keys?.asSequence()?.map { key: String -> key }?.toList()
            if (serviceNames != null) {
                for (serviceName in serviceNames) {
                    println("serviceName $serviceName")
                    when (val serviceData = data[serviceName]) {
                        is JsonObject -> {
                            println("serviceData $serviceData")
                            // Handle JsonObject case
                        }

                        is JsonArray -> {
                            if (serviceData.isEmpty()) {
                                val commitPanel = setupServiceUI(emptyList(), serviceName ?: "")
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
                                servicePanels = servicePanels + (setupServiceUI(returnArray, serviceName ?: ""))

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
            println("servicePanels ${servicePanels.size}")
            // add the servicePanels to the UI
            servicePanels.forEach { servicePanel ->
                add(servicePanel)
            }

        }
    }
}

