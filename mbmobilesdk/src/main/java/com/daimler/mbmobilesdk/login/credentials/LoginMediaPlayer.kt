package com.daimler.mbmobilesdk.login.credentials

import android.content.Context
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import com.daimler.mbloggerkit.MBLoggerKit
import com.daimler.mbmobilesdk.R

class LoginMediaPlayer(
    context: Context,
    textureView: TextureView
) : TextureView.SurfaceTextureListener {

    private var context: Context? = context
    private var player: MediaPlayer? = null
    private var textureView: TextureView? = textureView

    private var orgPreviewWidth = 0
    private var orgPreviewHeight = 0

    init {
        textureView.surfaceTextureListener = this
    }

    fun resume() = player?.tryResume()

    fun pause() = player?.tryPause()

    fun releasePlayer() {
        destroy()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        updateTextureMatrix(width, height)
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) = Unit

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        orgPreviewHeight = height
        orgPreviewWidth = width

        player?.tryAction {
            setSurface(Surface(surface))
        } ?: context?.let {
            player = MediaPlayer.create(it, R.raw.login_movie_new)?.apply {
                setSurface(Surface(surface))
                isLooping = true
                mute()
                setOnPreparedListener { tryResume() }
            }
        }
    }

    private fun destroy() {
        player?.tryStop()
        player?.tryAction { release() }
        player = null
        context = null
        textureView = null
    }

    private fun MediaPlayer.tryResume() {
        try {
            if (!isPlaying) start()
        } catch (e: IllegalStateException) {
            MBLoggerKit.e("Error while resuming media player.", throwable = e)
        }
    }

    private fun MediaPlayer.tryPause() {
        try {
            if (isPlaying) pause()
        } catch (e: IllegalStateException) {
            MBLoggerKit.e("Error while pausing media player.", throwable = e)
        }
    }

    private fun MediaPlayer.tryStop() {
        try {
            if (isPlaying) stop()
        } catch (e: IllegalStateException) {
            MBLoggerKit.e("Error while stopping media player.", throwable = e)
        }
    }

    private fun MediaPlayer.tryAction(action: MediaPlayer.() -> Unit) {
        try {
            this.action()
        } catch (e: IllegalStateException) {
            MBLoggerKit.e("Error while performing action on media player.", throwable = e)
        }
    }

    private fun MediaPlayer.mute() {
        setVolume(0f, 0f)
    }

    private fun updateTextureMatrix(width: Int, height: Int) {

        var scaleX = 1.0f
        var scaleY = 1.0f

        if (orgPreviewHeight > height) {
            scaleX = orgPreviewWidth.toFloat() / width
            scaleY = orgPreviewHeight.toFloat() / height
        }

        val matrix = Matrix()
        matrix.setScale(scaleX, scaleY, (width / 2).toFloat(), height.toFloat())

        textureView?.setTransform(matrix)
    }
}