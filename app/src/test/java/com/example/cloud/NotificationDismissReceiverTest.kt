package com.example.cloud

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.example.cloud.ui.notification.pushNotification.NotificationDismissReceiver
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NotificationDismissReceiverTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var notificationDismissReceiver: NotificationDismissReceiver

    @Before
    fun setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this)

        // Initialize the receiver
        notificationDismissReceiver = NotificationDismissReceiver()

        // Mock the SharedPreferences and Editor behavior
        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.getInt(anyString(), anyInt())).thenReturn(1) // Assume badge count is 1
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor)
    }

    @Test
    fun testOnReceive_decrementBadgeCount() {
        // Create a mock intent using Robolectric
        val intent = Intent("com.example.cloud.NOTIFICATION_COUNT_UPDATED")

        // Call onReceive() method
        notificationDismissReceiver.onReceive(mockContext, intent)

        // Verify that the badge count was decremented
        verify(mockEditor).putInt("notification_badge_count", 0)
        verify(mockEditor).apply()

        // Verify that a broadcast was sent for the badge count update
        val intentCaptor = ArgumentCaptor.forClass(Intent::class.java)
        verify(mockContext).sendBroadcast(intentCaptor.capture())

        val capturedIntent = intentCaptor.value
        assert(capturedIntent.action == "com.example.cloud.NOTIFICATION_COUNT_UPDATED")
    }

    @Test
    fun testOnReceive_noDecrementIfBadgeCountIsZero() {
        // Set the badge count to 0
        `when`(mockSharedPreferences.getInt(anyString(), anyInt())).thenReturn(0)

        // Call onReceive() method
        notificationDismissReceiver.onReceive(mockContext, Intent())

        // Verify that putInt() is never called since badge count is 0
        verify(mockEditor, never()).putInt(anyString(), anyInt())

        // Verify that a broadcast was not sent
        verify(mockContext, never()).sendBroadcast(any(Intent::class.java))
    }
}
