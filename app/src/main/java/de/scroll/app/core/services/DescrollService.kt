package de.scroll.app.core.services

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import app.wa.automate.core.utils.AppPreferences
import de.scroll.app.core.constants.Platform
import de.scroll.app.core.constants.PlatformRestriction
import de.scroll.app.core.exceptions.Failure
import de.scroll.app.core.extensions.toast
import kotlin.collections.isNotEmpty

class DescrollService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val type = event.eventType

        if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            type == AccessibilityEvent.TYPE_VIEW_SELECTED ||
            type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) {
            blockContent(event.packageName?.toString())
        }

        if (type == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            Log.d("NoScrollService", "Scrolling detected")
            blockFurtherScrolling(event)
        }
    }

    private fun blockContent(packageName: String?) {
        try {
            val restrictions : Map<Platform, PlatformRestriction> = AppPreferences.getAllRestrictions()

            if (
                packageName == "com.google.android.youtube" &&
                restrictions[Platform.YOUTUBE_SHORTS] == PlatformRestriction.BLOCK
            ) {
                blockShorts()
            } else if (
                packageName == "com.instagram.android" &&
                restrictions[Platform.INSTAGRAM_REELS] == PlatformRestriction.BLOCK
            ) {
                blockReels()
            }
        } catch (e: Exception) {
            if (e is Failure && e.showToast) {
                toast(message = e.message.toString())
            }
            Log.e("NoScrollService", "Failure: ${e.message}")
        }
    }

    private fun blockFurtherScrolling(event: AccessibilityEvent) {
        try {
            val node = event.source
            if (node == null) {
                throw Failure("Node is null")
            } else {
                val restrictions : Map<Platform, PlatformRestriction> = AppPreferences.getAllRestrictions()
                val scrollable = node.isScrollable
                if (!scrollable) return

                if (
                    packageName == "com.google.android.youtube" &&
                    restrictions[Platform.YOUTUBE_SHORTS] == PlatformRestriction.PARTIAL_BLOCK
                ) {
                    moveToYtHome(node)
                } else if (
                    packageName == "com.instagram.android" &&
                    restrictions[Platform.INSTAGRAM_REELS] == PlatformRestriction.PARTIAL_BLOCK
                ) {
                    moveToInstaHome(node)
                }
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
                moveToYtHome(rootNode)
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
                moveToInstaHome(rootNode)
            }
        }
    }

    private fun moveToInstaHome(rootNode: AccessibilityNodeInfo) {
        val homeButton = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/feed_tab")
        if (homeButton != null && homeButton.isNotEmpty()) {
            tapChildOrParent(homeButton[0])
        } else {
            throw Failure("Home button not found", true)
        }
    }

    private fun moveToYtHome(rootNode: AccessibilityNodeInfo) {
        val homeButton = rootNode.findAccessibilityNodeInfosByText("Home")
        if (homeButton.isNotEmpty()) {
            tapChildOrParent(homeButton[0])
        } else {
            throw Failure("Home button not found", true)
        }
    }

    override fun onInterrupt() {
        // Handle service interruption (e.g. when disabled)
    }
}
// TODO: Check best practices in all this