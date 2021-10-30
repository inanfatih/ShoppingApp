package com.minan.shoppingapp.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.minan.shoppingapp.R
import com.minan.shoppingapp.databinding.ActivityForgotPasswordBinding
import com.minan.shoppingapp.databinding.ActivityLoginBinding
import com.minan.shoppingapp.databinding.ActivityRegisterBinding

private lateinit var binding: ActivityForgotPasswordBinding
private lateinit var bindingLogin: ActivityLoginBinding
private lateinit var bindingRegister: ActivityRegisterBinding

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        bindingLogin = ActivityLoginBinding.inflate(layoutInflater)
        bindingRegister = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setupActionBar()

        binding.btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null && v.id == R.id.btn_submit)
        {
            if (validateFields())
            {
                val email = binding.etEmailForgotPassword.text.toString().trim()
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
                        it.exception?.message?.let { it1 -> showSnackBar(it1, true) }
                    }
                    hideProgressDialog()
                }
            }
        }
    }

    private fun setupActionBar() {
        val toolbar_forgot_password_activity: Toolbar = findViewById(R.id.toolbar_forgot_password_activity)
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
            TextUtils.isEmpty(binding.etEmailForgotPassword.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            else -> {
                true
            }
        }
    }
}