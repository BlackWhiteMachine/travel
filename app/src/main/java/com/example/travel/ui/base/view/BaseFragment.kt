package com.example.travel.ui.base.view

import android.content.Context
import android.os.Bundle
import android.view.View

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import dagger.android.support.AndroidSupportInjection

abstract class BaseFragment : Fragment(), MvpView {

    var baseActivity: BaseActivity? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp(view)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            this.baseActivity = context
            context.onFragmentAttached()
        }
    }

    override fun onError(message: String?) {
        baseActivity?.onError(message)
    }

    override fun onError(@StringRes resId: Int) {
        baseActivity?.onError(resId)
    }

    override fun showMessage(message: String?) {
        baseActivity?.showMessage(message)
    }

    override fun showMessage(@StringRes resId: Int) {
        baseActivity?.showMessage(resId)
    }

    override fun hideKeyboard() {
            baseActivity?.hideKeyboard()
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    protected abstract fun setUp(view: View)

    private fun performDependencyInjection() = AndroidSupportInjection.inject(this)

    interface Callback {

        fun onFragmentAttached()

        fun onFragmentDetached(tag: String)
    }
}
