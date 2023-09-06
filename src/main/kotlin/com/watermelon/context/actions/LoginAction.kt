package com.watermelon.context.actions

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.ide.BrowserUtil
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.ide.passwordSafe.PasswordSafe.*
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.remoteServer.util.CloudConfigurationUtil.createCredentialAttributes
import kotlinx.serialization.json.*
import org.bouncycastle.cms.RecipientId.password
import java.net.HttpURLConnection
import java.net.URL


class LoginAction : AnAction() {
    private fun sendTokenToAPI(token: String): String {
        val apiUrl = "https://app.watermelontools.com/api/extension/intellijLogin"
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
                val jsonResponse = Json.parseToJsonElement(connection.inputStream.reader().readText()).jsonObject
                val data = jsonResponse["data"]?.jsonObject
                val id = data?.get("id")?.toString()
                val email = data?.get("email")?.toString()
                // Store the tokens
                val passwordSafe = PasswordSafe.instance
                passwordSafe.setPassword(CredentialAttributes("WatermelonContext.id"), id)
                passwordSafe.setPassword(CredentialAttributes("WatermelonContext.email"), email)
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
        val email = Messages.showPasswordDialog(
            e.project,
            "Please enter your email:",
            "Email Input",
            null
        )

        if (email.isNullOrEmpty()) {
            Messages.showMessageDialog(
                e.project,
                "Email should not be empty.",
                "Input Error",
                Messages.getErrorIcon()
            )
        } else {
            sendTokenToAPI(email)
            Messages.showMessageDialog(
                e.project,
                "You have logged in to Watermelon Context as $email.",
                "Success",
                Messages.getInformationIcon()
            )
        }
    }

    override fun update(e: AnActionEvent) {
        val condition = checkYourCondition(e)
        e.presentation.isEnabledAndVisible = condition
    }

    private fun checkYourCondition(e: AnActionEvent): Boolean {
        val passwordSafe = PasswordSafe.instance
        val id = passwordSafe.getPassword(CredentialAttributes("WatermelonContext.id"))
        val email = passwordSafe.getPassword(CredentialAttributes("WatermelonContext.email"))
        return id.isNullOrEmpty() || email.isNullOrEmpty()
    }
}
