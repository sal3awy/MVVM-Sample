package com.sal3awy.thed.base

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.DaggerFragment


abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : DaggerFragment() {

    private var baseActivity: BaseActivity<*>? = null
    private var mRootView: View? = null
    var mViewDataBinding: T? = null
    private var mViewModel: V? = null

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    fun getViewDataBinding() = mViewDataBinding!!

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun  viewModel(): V

    val isNetworkConnected: Boolean = baseActivity != null && baseActivity!!.isNetworkConnected()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*>) {
            this.baseActivity = context
            context.onFragmentAttached()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)

        mViewModel = viewModel()

        observeErrorMessage()
        observeLoading()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mRootView = mViewDataBinding!!.root
        return mRootView
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewDataBinding!!.lifecycleOwner = this
        mViewDataBinding!!.executePendingBindings()
    }


    fun hideKeyboard() {
        if (baseActivity != null) {
            baseActivity!!.hideKeyboard()
        }
    }

    fun showLoading() {
        if (baseActivity != null) {
            baseActivity!!.showLoading()
        }
    }

    fun hideLoading() {
        if (baseActivity != null) {
            baseActivity!!.hideLoading()
        }
    }

    fun openActivityOnTokenExpire() {
        if (baseActivity != null) {
            baseActivity!!.openActivityOnTokenExpire()
        }
    }

    private fun performDependencyInjection() {
        AndroidSupportInjection.inject(this)
    }

    private fun observeErrorMessage() {
        mViewModel!!.errorMessage.observe(this, Observer<String> { message ->
            if (!TextUtils.isEmpty(message)) {
                if (baseActivity != null) {
                    baseActivity!!.showSnakeBar(message)
                }
            }
        })
    }

    private fun observeLoading() {
        mViewModel!!.isLoading.observe(this, Observer<Boolean> { isLoading ->
            if (isLoading != null && isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        })
    }

    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }
}