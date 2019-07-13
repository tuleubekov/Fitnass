package com.akay.fitnass.view.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.akay.fitnass.R

class TestAndroidCalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_android_calendar)
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, TestAndroidCalendarActivity::class.java)
        }
    }
}
