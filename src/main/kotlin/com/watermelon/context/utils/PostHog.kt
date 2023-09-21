package com.watermelon.context.utils
import com.posthog.java.PostHog
object PostHog {

    // still need to figure this out
//    private val POSTHOG_API_KEY = System.getProperties().get("POSTHOG_API_KEY") as String;
//    private val POSTHOG_HOST = System.getProperties().get("POSTHOG_HOST") as String;


    val posthog = PostHog.Builder(POSTHOG_API_KEY)
        .host(POSTHOG_HOST)
        .build()

}
