package com.tanzeelmarwat.expandabledynamicfaqs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    private var TAG: String = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(TAG)
        if (fragment == null) {
            fragment = ItemFragment()
        }
        replaceFragment(fragment, false, R.id.frame_content, TAG)
    }

    private fun replaceFragment(fragment: Fragment,
                                addToBackStack: Boolean,
                                frameId: Int,
                                tag: String) {
        if (supportFragmentManager != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(frameId, fragment, tag)
            if (addToBackStack)
                fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
            if (!isFinishing) {
                fragmentTransaction.commit()
            }

        }
    }
}
