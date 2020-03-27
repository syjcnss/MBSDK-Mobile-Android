package com.daimler.mbmobilesdk.ui.components.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.daimler.mbmobilesdk.ui.components.ViewModelOwner

/**
 * Basic class that provides usage for [ViewModel].
 *
 * ```
 *  class MyActivity : MBBaseViewModelActivity<MyViewModel>() {
 *
 *      override fun getLayoutRes(): Int = R.layout.activity_my
 *
 *      override fun getModelId(): Int = BR.model
 *
 *      override fun createViewModel(): MyViewModel =
 *              ViewModelProviders.of(this).get(MyViewModel::class.java)
 *  ]
 * ```
 */
abstract class MBBaseViewModelActivity<T : ViewModel> : AppCompatActivity(), ViewModelOwner<T> {

    /**
     * This field contains the activity's [ViewModel] after this activity has been created.
     * The access to this field is read-only.
     */
    lateinit var viewModel: T
        private set

    protected lateinit var binding: ViewDataBinding
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        binding = DataBindingUtil.setContentView(this, getLayoutRes())
        binding.apply {
            setVariable(getModelId(), viewModel)
            setLifecycleOwner(provideLifecycleOwner())
            onBindingCreated(this)
        }
    }

    override fun provideLifecycleOwner(): LifecycleOwner = this

    override fun onBindingCreated(binding: ViewDataBinding) = Unit
}