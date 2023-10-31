package com.watermelon.context.listeners

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.PermanentInstallationID
import com.intellij.openapi.wm.IdeFrame
import com.watermelon.context.utils.PostHog

internal class MyApplicationActivationListener : ApplicationActivationListener {

    override fun applicationActivated(ideFrame: IdeFrame) {
        val uuid = PermanentInstallationID.get();
        PostHog.posthog.capture(uuid,
            "intelliJ:appActivated");
    }
}
