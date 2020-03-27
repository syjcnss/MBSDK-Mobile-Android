package com.daimler.mbmobilesdk.ui.utils.extensions

import android.app.Activity
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Replaces the current fragment in this activity.
 *
 * @param id the id of the fragment container
 * @param fragment the fragment that shall be placed into this activity
 * @param tag an optional tag for the fragment to retrieve it later via [FragmentManager.findFragmentByTag]
 * @param addToBackStack true, if the transaction shall be added to the back stack
 * @param name an optional name for the back stack state; only used if [addToBackStack] is true
 */
internal fun AppCompatActivity.replaceFragment(
    @IdRes id: Int,
    fragment: Fragment,
    tag: String? = null,
    addToBackStack: Boolean = false,
    name: String? = null
) {
    supportFragmentManager.transaction {
        replace(id, fragment, tag)
        if (addToBackStack) {
            addToBackStack(name)
        }
    }
}

/**
 * Calls the given [action] within a [FragmentTransaction]. The transaction will be commited
 * using [FragmentTransaction.commit].
 */
internal inline fun FragmentManager.transaction(action: FragmentTransaction.() -> Unit) {
    val transaction: FragmentTransaction = beginTransaction()
    transaction.action()
    transaction.commit()
}

internal fun Activity.hideKeyboard() {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    val view = currentFocus ?: View(this)
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}