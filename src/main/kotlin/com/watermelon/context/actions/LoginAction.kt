package com.watermelon.context.actions

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.UIUtil
import com.intellij.ide.BrowserUtil

class LoginAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        // Open mycompany.com/intellij webpage
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
            // Use the userToken for whatever purpose you need
            // For now, we just show it in a message box (not recommended for real tokens!)
            Messages.showMessageDialog(
                e.project,
                "You entered: $userToken",
                "Your Token",
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
