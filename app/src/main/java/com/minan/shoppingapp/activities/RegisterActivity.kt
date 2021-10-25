package com.minan.shoppingapp.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.minan.shoppingapp.R
import kotlinx.android.synthetic.main.activity_register.*

// BaseActivity, AppCompatActivity'i inherit ettiginden dolayi burada BaseActivity'i inherit etmek, ayni zamanda AppCompatActivity'i inherit etmek demek oluyor
class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else
        {
            @Suppress("DEPRECATION")
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        setupActionBar()

        tv_login.setOnClickListener{
//            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//            startActivity(intent)
            onBackPressed()
        }

        btn_register.setOnClickListener{
            registerUser()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_register_activity)
        //Once AppBar, sonra  ActionBar, daha sonra da Toolbar cikmis ve aslinda hepsi ayni ise yariyor. Toolbar daha esnek ve daha kullanisli
        val actionBar = supportActionBar
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }

        toolbar_register_activity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun validateRegisterDetails(): Boolean
    {
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim{ it <= ' '}) || et_first_name.length() < 3
            -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim()) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_email.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim()) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim()) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            et_password.text.toString().trim() != et_confirm_password.text.toString().trim() -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }

            !cb_terms_and_condition.isChecked ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun registerUser()
    {
        if (validateRegisterDetails())
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = et_email.text.toString().trim()
            val password: String = et_password.text.toString().trim()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        val firebaseUser: FirebaseUser = it.result!!.user!!
                        showErrorSnackBar("Account created. User id: ${firebaseUser.email}", false)

                        FirebaseAuth.getInstance().signOut()
                        finish()
                    }
                    else
                    {
                        showErrorSnackBar("Account creation failed: ${it.exception?.message}", true)
                    }
                    hideProgressDialog()
                }

        }
    }
}