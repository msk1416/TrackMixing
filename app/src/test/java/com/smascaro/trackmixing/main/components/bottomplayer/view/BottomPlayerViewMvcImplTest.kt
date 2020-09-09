package com.smascaro.trackmixing.main.components.bottomplayer.view

import com.bumptech.glide.RequestManager
import com.smascaro.trackmixing.common.utils.ResourcesWrapper
import com.smascaro.trackmixing.playbackservice.MixPlayerServiceChecker
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BottomPlayerViewMvcImplTest {
    private lateinit var SUT: BottomPlayerViewMvcImpl

    // region constants

    // endregion constants

    // region helper fields
    @Mock private lateinit var glide: RequestManager
    @Mock private lateinit var resourcesWrapper: ResourcesWrapper
    @Mock private lateinit var serviceChecker: MixPlayerServiceChecker
    private lateinit var viewMvcListener: BottomPlayerViewMvcListenerImplementator
    // endregion helper fields

    @Before
    fun setup() {
        viewMvcListener = BottomPlayerViewMvcListenerImplementator()
        SUT = BottomPlayerViewMvcImpl(glide, resourcesWrapper, serviceChecker)
    }

    // region tests
    @Test
    fun onCreate_serviceIsRunning_listenerIsNotified() {
        // Arrange
        setServiceCheckResultOk()
        SUT.registerListener(viewMvcListener)
        // Act
        SUT.onCreate()
        // Assert
        assertTrue(viewMvcListener.hasServiceBeenChecked)
    }

    @Test
    fun onCreate_serviceIsRunning_listenerIsNotifiedWithOkResult() {
        // Arrange
        setServiceCheckResultOk()
        SUT.registerListener(viewMvcListener)
        // Act
        SUT.onCreate()
        // Assert
        assertTrue(viewMvcListener.serviceCheckResult!!)
    }

    @Test
    fun onCreate_serviceIsNotRunning_listenerIsNotified() {
        // Arrange
        setServiceCheckResultNook()
        SUT.registerListener(viewMvcListener)
        // Act
        SUT.onCreate()
        // Assert
        assertTrue(viewMvcListener.hasServiceBeenChecked)
    }

    @Test
    fun onCreate_serviceIsNotRunning_listenerIsNotifiedWithNookResult() {
        // Arrange
        setServiceCheckResultNook()
        SUT.registerListener(viewMvcListener)
        // Act
        SUT.onCreate()
        // Assert
        assertFalse(viewMvcListener.serviceCheckResult!!)
    }

    // endregion tests

    // region helper methods
    private fun setServiceCheckResultOk() {
        Mockito.`when`(serviceChecker.ping()).thenReturn(true)
    }

    private fun setServiceCheckResultNook() {
        Mockito.`when`(serviceChecker.ping()).thenReturn(false)
    }

    // endregion helper methods

    // region helper classes
    class BottomPlayerViewMvcListenerImplementator : BottomPlayerViewMvc.Listener {
        var hasServiceBeenChecked: Boolean = false
        var serviceCheckResult: Boolean? = null
        override fun onLayoutClick() {
            TODO("Not yet implemented")
        }

        override fun onActionButtonClicked() {
            TODO("Not yet implemented")
        }

        override fun onPlayerStateChanged() {
            TODO("Not yet implemented")
        }

        override fun onServiceRunningCheck(running: Boolean) {
            hasServiceBeenChecked = true
            serviceCheckResult = running
        }

    }
    // endregion helper classes
}