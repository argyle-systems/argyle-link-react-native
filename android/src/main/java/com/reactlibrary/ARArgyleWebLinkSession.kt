package com.reactlibrary

import org.json.JSONObject

internal class ARArgyleWebLinkSession(
    val page: ARArgyleWebLinkPage,
    val onCallback: (String, JSONObject) -> Unit
) {
    var activity: ARArgyleWebLinkActivity? = null

    companion object {
        var current: ARArgyleWebLinkSession? = null
            private set

        fun begin(session: ARArgyleWebLinkSession): Boolean {
            if (current != null) {
                return false
            }
            current = session
            return true
        }

        fun end(activity: ARArgyleWebLinkActivity) {
            if (current?.activity === activity) {
                current = null
            }
        }

        fun cancel(session: ARArgyleWebLinkSession) {
            if (current === session) {
                current = null
            }
        }
    }
}
