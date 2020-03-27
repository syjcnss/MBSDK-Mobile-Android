package com.daimler.mbmobilesdk.ui.components.fragments

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimler.mbmobilesdk.ui.components.ViewModelOwner

/**
 * Basic fragment that provides usage for [ViewModel].
 */
internal abstract class MBBaseViewModelFragment<T : ViewModel> : Fragment(),
    ViewModelOwner<T> {

    /**
     * This field contains the activity's [ViewModel] after this activity has been created.
     * The access to this field is read-only.
     */
    lateinit var viewModel: T
        private set

    protected lateinit var binding: ViewDataBinding
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = createViewModel()
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        binding.apply {
            setVariable(getModelId(), viewModel)
            setLifecycleOwner(provideLifecycleOwner())
            onBindingCreated(this)
        }
        return binding.root
    }

    override fun provideLifecycleOwner(): LifecycleOwner = this

    override fun onBindingCreated(binding: ViewDataBinding) = Unit
}