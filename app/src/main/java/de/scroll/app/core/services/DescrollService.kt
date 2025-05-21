package de.scroll.app.core.services

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import de.scroll.app.core.exceptions.Failure
import de.scroll.app.core.extensions.toast

class DescrollService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val type = event.eventType

        if (type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            type != AccessibilityEvent.TYPE_VIEW_SELECTED &&
            type != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            return
        }

        try {
            val preferences : SharedPreferences = getSharedPreferences("de.scroll.app", MODE_PRIVATE)
            val ytAllowed : Boolean = preferences.getBoolean("yt_allowed", true)
            val instaAllowed : Boolean = preferences.getBoolean("insta_allowed", true)
            val packageName = event.packageName?.toString()

            if (packageName == "com.google.android.youtube" && ytAllowed) {
                blockShorts()
            } else if (packageName == "com.instagram.android" && instaAllowed) {
                blockReels()
            }
        } catch (e: Exception) {
            if (e is Failure && e.showToast) {
                toast(message = e.message.toString())
            }
            Log.e("NoScrollService", "Failure: ${e.message}")
        }
    }

    private fun tapChildOrParent(node: AccessibilityNodeInfo) {
        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        } else {
            val parent = node.parent
            if (parent != null) {
                tapChildOrParent(parent)
            }
        }
    }

    private fun blockShorts() {
        val rootNode = rootInActiveWindow
        if (rootNode == null) {
            throw Failure("Root node is null")
        } else {
            val overlayNode = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/reel_player_overlay_container")
            val barNode = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/elements_button_bar_container")

            if (
                overlayNode != null && overlayNode.isNotEmpty() &&
                barNode != null && barNode.isNotEmpty()
            ){
                val homeButton = rootNode.findAccessibilityNodeInfosByText("Home")
                if (homeButton.isNotEmpty()) {
                    tapChildOrParent(homeButton[0])
                } else {
                    throw Failure("Home button not found", true)
                }
            }
        }
    }

    private fun blockReels() {
        val rootNode = rootInActiveWindow
        if (rootNode == null) {
            throw Failure("Root node is null")
        } else {
            val layoutNode = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_viewer_video_layout")
            val infoNode = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_media_info_component")
            val overlayNode = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_item_overlay_component")
            Log.d("NoScrollService", "Node: $layoutNode")
            if (
                layoutNode != null && layoutNode.isNotEmpty() &&
                infoNode != null && infoNode.isNotEmpty() &&
                overlayNode != null && overlayNode.isNotEmpty()
            ) {
                val homeButton = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/feed_tab")
                if (homeButton != null && homeButton.isNotEmpty()) {
                    tapChildOrParent(homeButton[0])
                } else {
                    throw Failure("Home button not found", true)
                }
            }
        }
    }

    override fun onInterrupt() {
        // Handle service interruption (e.g. when disabled)
    }
}