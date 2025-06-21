package de.scroll.app.core.services

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import app.wa.automate.core.utils.AppPreferences
import de.scroll.app.core.constants.Platform
import de.scroll.app.core.constants.PlatformRestriction
import de.scroll.app.core.exceptions.Failure
import de.scroll.app.core.extensions.toast
import java.time.Instant
import kotlin.collections.isNotEmpty
import kotlin.time.Duration

class DescrollService : AccessibilityService() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val type = event.eventType

        if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            type == AccessibilityEvent.TYPE_VIEW_SELECTED ||
            type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        ) {
            blockContent(event.packageName?.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun blockContent(packageName: String?) {
        try {
            val restrictions: Map<Platform, PlatformRestriction> = AppPreferences.getAllRestrictions()

            if (packageName == "com.google.android.youtube") {
                if (restrictions[Platform.YOUTUBE_SHORTS] == PlatformRestriction.PAUSE) {
                    if (!shouldPause()) {
                        blockShorts()
                    }
                }
            } else if (
                packageName == "com.instagram.android" &&
                restrictions[Platform.INSTAGRAM_REELS] == PlatformRestriction.BLOCK
            ) {
                if (restrictions[Platform.YOUTUBE_SHORTS] == PlatformRestriction.PAUSE) {
                    if (!shouldPause()) {
                        blockReels()
                    }
                }
            }
        } catch (e: Exception) {
            if (e is Failure && e.showToast) {
                toast(message = e.message.toString())
            }
            Log.e("NoScrollService", "Failure: ${e.message}", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun shouldPause(): Boolean {
        val now = Instant.now()

        val nextPauseTimeMillis = AppPreferences.getNextPauseTime()
        val pauseDurationMillis = AppPreferences.getPauseDuration() * 60 * 1000L
        val cooldownMillis = AppPreferences.getPauseCooldown() * 60 * 1000L

        // Case 1: No pause scheduled yet → allow pause, and set nextPauseTime
        if (nextPauseTimeMillis == null) {
            val newNextPauseTime = now.plusMillis(pauseDurationMillis + cooldownMillis)
            AppPreferences.setNextPauseTime(newNextPauseTime.toEpochMilli())
            Log.d("NoScrollService", "Set new nextPauseTime: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(newNextPauseTime.toEpochMilli()))}")
            return true
        }

        val nextPauseTime = Instant.ofEpochMilli(nextPauseTimeMillis)
        val pauseWindowEnd = nextPauseTime.plusMillis(pauseDurationMillis)

        return when {
            // Not yet in the pause window
            now.isBefore(nextPauseTime) -> false

            // Inside the pause window
            now.isBefore(pauseWindowEnd) -> true

            // After pause window ends → set new nextPauseTime and deny access now
            else -> {
                val newNextPauseTime = now.plusMillis(pauseDurationMillis + cooldownMillis)
                AppPreferences.setNextPauseTime(newNextPauseTime.toEpochMilli())
                Log.d("NoScrollService", "Set new nextPauseTime: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(newNextPauseTime.toEpochMilli()))}")
                false
            }
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