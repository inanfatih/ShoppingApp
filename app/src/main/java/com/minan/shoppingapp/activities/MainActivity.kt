package com.minan.shoppingapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.minan.shoppingapp.R
import com.minan.shoppingapp.utils.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.tv_main).text =
            getSharedPreferences(Constants.MYAPP_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME, "")
    }
}