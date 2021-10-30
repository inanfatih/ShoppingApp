package com.minan.shoppingapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.minan.shoppingapp.R
import com.minan.shoppingapp.databinding.ActivityLoginBinding
import com.minan.shoppingapp.firestore.FirestoreClass
import com.minan.shoppingapp.models.User
import com.minan.shoppingapp.utils.Constants

class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding.tvForgotPassword.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.tvRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null)
        {
            when (v.id)
            {
                R.id.tv_forgot_password ->{
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.btn_login ->{
                    logInRegisteredUser()
                }
                R.id.tv_register -> {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when
        {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser()
    {
        if (validateLoginDetails())
        {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{
                    if (it.isSuccessful)
                    {
                        FirestoreClass().getUserDetails(this)
                    }
                    else
                    {
                        hideProgressDialog()
                        showSnackBar(it.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()
        Log.i("First name", user.firstName)
        Log.i("Last name", user.lastName)
        Log.i("email", user.email)

        if (user.profileCompleted == 0)
        {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        }
        else
        {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }

}