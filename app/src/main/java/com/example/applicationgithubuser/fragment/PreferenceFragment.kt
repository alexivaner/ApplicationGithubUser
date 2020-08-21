package com.example.applicationgithubuser.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.applicationgithubuser.R

class MyPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}