package com.daimler.mbmobilesdk.ui.components.dialogfragments

import androidx.lifecycle.LifecycleOwner
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimler.mbmobilesdk.databinding.FragmentMbsdkDialogBinding
import com.daimler.mbmobilesdk.ui.components.ViewModelOwner
import com.daimler.mbmobilesdk.ui.components.viewmodels.MBGenericDialogViewModel

/**
 * Base class for dialog fragments. This class provides abstract methods similar to other base
 * components in this sdk to create a ViewDataBinding and set a ViewModel to it.
 */
internal abstract class MBBaseDialogFragment<T : MBGenericDialogViewModel> :
    DialogFragment(), ViewModelOwner<T> {

    protected lateinit var viewModel: T
        private set

    protected lateinit var binding: ViewDataBinding
        private set

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = createViewModel()
        binding = DataBindingUtil.inflate<FragmentMbsdkDialogBinding>(
            inflater,
            getLayoutRes(),
            container,
            false
        )
        binding.apply {
            setVariable(getModelId(), viewModel)
            setLifecycleOwner(provideLifecycleOwner())
            onBindingCreated(binding)
        }
        return binding.root
    }

    override fun provideLifecycleOwner(): LifecycleOwner = this

    override fun onBindingCreated(binding: ViewDataBinding) = Unit
}