package com.example.travel.ui.main.view

import android.app.AlertDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import com.example.travel.R
import com.example.travel.ui.ToolWindowFragmentsAdapter
import com.example.travel.ui.base.view.BaseActivity
import com.example.travel.ui.tool_window_bars.tools.view.ToolsFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.config.Configuration
import javax.inject.Inject

class MainActivity : BaseActivity(), MainMvpView, HasAndroidInjector {

    @Inject
    internal lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        setContentView(R.layout.activity_main)

        // *****************************************************************************************

        val mnfa = ToolWindowFragmentsAdapter(supportFragmentManager)

        mnfa.addFragment(ToolsFragment.newInstance())
        mainViewPager.adapter = mnfa
        
        // *****************************************************************************************
    }

    public override fun onResume() {
        super.onResume()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
    }

    public override fun onPause() {
        super.onPause()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        Configuration.getInstance().save(this, prefs)
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
                .setTitle(R.string.main_activity_exit)
                .setMessage(R.string.main_activity_exit_confirmation)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes) { arg0, arg1 -> super@MainActivity.onBackPressed() }

        val alert = builder.create()
        alert.show()
    }

    override fun androidInjector(): AndroidInjector<Any> = fragmentDispatchingAndroidInjector
}
