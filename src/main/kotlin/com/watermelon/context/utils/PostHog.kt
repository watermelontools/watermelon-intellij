package com.watermelon.context.utils

import com.posthog.java.PostHog

object PostHog {

    // manually insert API key here
    private val POSTHOG_API_KEY = "POSTHOG_API_KEY";
    private val POSTHOG_HOST = "POSTHOG_HOST";


    val posthog = PostHog.Builder(POSTHOG_API_KEY)
        .host(POSTHOG_HOST)
        .build()

    fun capture(uuid: String, event: String) {
        posthog.capture(uuid, event)
    }
}
