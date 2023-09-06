package com.watermelon.context.actions

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages
import com.intellij.ide.BrowserUtil
import java.net.HttpURLConnection
import java.net.URL

class LoginAction : AnAction() {
    private fun sendTokenToAPI(token: String): String {
        val apiUrl = "http://localhost:3000/api/extension/intellijLogin"
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Charset", "UTF-8")

            val payload = """
            {
                "token": "$token"
            }
        """.trimIndent()

            connection.outputStream.write(payload.toByteArray())

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return connection.inputStream.reader().readText()
            } else {
                // Handle non-200 HTTP responses
                return "Error: $responseCode"
            }
        } finally {
            connection.disconnect()
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        // Open webpage
        BrowserUtil.browse("https://app.watermelontools.com/intellij")

        // Open a dialog for user input
        val userToken = Messages.showPasswordDialog(
            e.project,
            "Please enter your token:",
            "Token Input",
            null
        )

        if (userToken.isNullOrEmpty()) {
            Messages.showMessageDialog(
                e.project,
                "Token should not be empty.",
                "Input Error",
                Messages.getErrorIcon()
            )
        } else {
            val response = sendTokenToAPI(userToken)
            Messages.showMessageDialog(
                e.project,
                "Server Response: $response",
                "API Response",
                Messages.getInformationIcon()
            )
        }
    }

    override fun update(e: AnActionEvent) {
        val condition = checkYourCondition(e)
        e.presentation.isEnabledAndVisible = condition
    }

    private fun checkYourCondition(e: AnActionEvent): Boolean {
        return true
    }
}
