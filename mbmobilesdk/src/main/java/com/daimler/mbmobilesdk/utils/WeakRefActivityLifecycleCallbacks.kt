package com.daimler.mbmobilesdk.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

/**
 * [Application.ActivityLifecycleCallbacks] implementation that holds the current foreground
 * activity in a [WeakReference]. The reference is set when the activity was started and cleared
 * when the activity stopped.
 * You can receive the current activity via calling [activity]. An exception will be thrown
 * when you try to receive the activity when none is set. Check [activitySet] before.
 * You can also use one of the methods [withActivity] or [withFragmentActivity] that receives
 * a lambda that is only executed when an activity is set.
 */
open class WeakRefActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    private var currentActivity: WeakReference<Activity>? = null

    /**
     * True if there is an activity referenced.
     */
    protected val activitySet: Boolean
        get() = currentActivity?.get() != null

    protected fun finalize() {
        unregisterCallback()
    }

    /**
     * Subclasses MUST call this method to register the callbacks.
     */
    protected fun initCallbacks(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    private fun unregisterCallback() {

        currentActivity?.get()?.run {
            application.unregisterActivityLifecycleCallbacks(this@WeakRefActivityLifecycleCallbacks)
        }
    }

    /**
     * Returns the current foreground activity.
     *
     * @throws IllegalStateException if there is no activity referenced
     */
    protected fun activity(): Activity {
        return currentActivity?.get()
            ?: throw IllegalStateException("No activity attached. Did you call init()?")
    }

    /**
     * Invokes the given [action] if there is an activity set.
     */
    protected fun <T : Any> withActivity(action: Activity.() -> T): T? =
        currentActivity?.get()?.let(action)

    /**
     * Invokes the given [action] if there is a [FragmentActivity] set.
     */
    protected fun <T : Any> withFragmentActivity(action: FragmentActivity.() -> T): T? =
        (currentActivity?.get() as? FragmentActivity)?.let(action)

    protected open fun onActivitySet(activity: Activity) = Unit

    protected open fun onActivityCleared() = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityStarted(activity: Activity) {
        currentActivity?.clear()
        currentActivity = WeakReference(activity)
        onActivitySet(activity)
    }

    override fun onActivityDestroyed(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) = Unit

    override fun onActivityStopped(activity: Activity) {
        if (currentActivity?.get() == activity) {
            // other activities would have been cleared when onActivityStarted was called
            currentActivity?.clear()
            currentActivity = null
            onActivityCleared()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
}