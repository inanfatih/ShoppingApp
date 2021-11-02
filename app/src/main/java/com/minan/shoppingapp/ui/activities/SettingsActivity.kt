package com.minan.shoppingapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.minan.shoppingapp.R
import com.minan.shoppingapp.databinding.ActivitySettingsBinding
import com.minan.shoppingapp.firestore.FirestoreClass
import com.minan.shoppingapp.models.User
import com.minan.shoppingapp.utils.Constants
import com.minan.shoppingapp.utils.GlideLoader

class SettingsActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivitySettingsBinding
    lateinit var userDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnLogout.setOnClickListener(this)
        binding.tvEdit.setOnClickListener(this)
    }

    private fun setupActionBar()
    {
        setSupportActionBar(binding.toolbarSettingsActivity)

        val actionBar = supportActionBar
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled((true))
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarSettingsActivity.setNavigationOnClickListener{
            onBackPressed()
        }

    }

    private fun getUserDetails()
    {
        showProgressDialog()
        FirestoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User) {

        userDetails = user

        hideProgressDialog()
        GlideLoader(this).loadUserPicture(user.image, binding.ivUserPhoto)

        binding.tvName.text = "${user.firstName} ${user.lastName}"
        binding.tvGender.text = user.gender
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = user.mobile.toString()
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null)
        {
            when(v.id)
            {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.tv_edit -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, userDetails)
                    startActivity(intent)
                }
            }
        }
    }
}