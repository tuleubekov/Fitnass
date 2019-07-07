package com.akay.fitnass.view.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.akay.fitnass.R

class TestCalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_calendar)
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, TestCalendarActivity::class.java)
        }
    }
}
