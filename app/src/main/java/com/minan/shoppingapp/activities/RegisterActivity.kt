package com.minan.shoppingapp.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.minan.shoppingapp.R
import com.minan.shoppingapp.databinding.ActivityRegisterBinding
import com.minan.shoppingapp.firestore.FirestoreClass
import com.minan.shoppingapp.models.User

private lateinit var binding: ActivityRegisterBinding

// BaseActivity, AppCompatActivity'i inherit ettiginden dolayi burada BaseActivity'i inherit etmek, ayni zamanda AppCompatActivity'i inherit etmek demek oluyor
class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_register)

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

        binding.tvLogin.setOnClickListener{
            //val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            //startActivity(intent)
            onBackPressed()
        }

        binding.btnRegister.setOnClickListener{
            registerUser()
        }
    }

    private fun setupActionBar() {
        val toolbar_register_activity = findViewById<Toolbar>(R.id.toolbar_register_activity)
        setSupportActionBar(findViewById(R.id.toolbar_register_activity))
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
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim{ it <= ' '}) || binding.etFirstName.length() < 3
            -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(binding.etLastName.text.toString().trim()) ->{
                showSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(binding.etEmail.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.etPassword.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim()) ->{
                showSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            binding.etPassword.text.toString().trim() != binding.etConfirmPassword.text.toString().trim() -> {
                showSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }

            !binding.cbTermsAndCondition.isChecked ->{
                showSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
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
            val email: String = binding.etEmail.text.toString().trim()
            val password: String = binding.etPassword.text.toString().trim()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                    {
                        val firebaseUser: FirebaseUser = it.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            binding.etFirstName.text.toString().trim(),
                            binding.etLastName.text.toString().trim(),
                            binding.etEmail.text.toString().trim()
                        )
                        // Class'in instance'i FirestoreClass() ile olusturuluyor. Asagidaki sekilde aslinda bi isim vermeden instance olusturmus oluyoruz.
                        FirestoreClass().registerUser(this, user)

                        showToastMessage(this, "Account created")

                        //FirebaseAuth.getInstance().signOut()
                        //finish()
                    }
                    else
                    {
                        showSnackBar("Account creation failed: ${it.exception?.message}", true)
                        hideProgressDialog()
                    }
                }

        }
    }

    fun userRegistrationSuccess(){
        hideProgressDialog()
        Toast.makeText(this@RegisterActivity, resources.getString(R.string.register_success), Toast.LENGTH_LONG).show()
    }

}