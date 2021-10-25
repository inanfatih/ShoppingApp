package com.minan.shoppingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.Utils
import com.minan.shoppingapp.R
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setupActionBar()
        btn_submit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null && v.id == R.id.btn_submit)
        {
            if (validateFields())
            {
                val email = et_email_forgot_password.text.toString().trim()
                showProgressDialog(getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        Toast.makeText(this@ForgotPasswordActivity, getString(R.string.email_sent_success), Toast.LENGTH_LONG).show()
                        //Asagidaki intent e gerek yok. cunku finish zaten bizi buraya gonderen activity e gonderecek
//                        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
//                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        it.exception?.message?.let { it1 -> showErrorSnackBar(it1, true) }
                    }
                    hideProgressDialog()
                }
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_forgot_password_activity)
        //Once AppBar, sonra  ActionBar, daha sonra da Toolbar cikmis ve aslinda hepsi ayni ise yariyor. Toolbar daha esnek ve daha kullanisli
        val actionBar = supportActionBar
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_forgot_password_activity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun validateFields(): Boolean
    {
        return when
        {
            TextUtils.isEmpty(et_email_forgot_password.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            else -> {
                true
            }
        }
    }
}