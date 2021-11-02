package com.minan.shoppingapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.minan.shoppingapp.R
import com.minan.shoppingapp.databinding.ActivityUserProfileBinding
import com.minan.shoppingapp.firestore.FirestoreClass
import com.minan.shoppingapp.models.User
import com.minan.shoppingapp.utils.Constants
import com.minan.shoppingapp.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityUserProfileBinding
    lateinit var userDetails: User
    private var selectedImageFileUri: Uri? = null
    private var userProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS))
        {
            userDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        binding.etFirstName.isEnabled = false
        binding.etLastName.isEnabled = false
        binding.etEmail.isEnabled = false

        binding.etFirstName.setText(userDetails.firstName)
        binding.etLastName.setText(userDetails.lastName)
        binding.etEmail.setText(userDetails.email)
        binding.etMobileNumber.setText(userDetails.mobile.toString())

        if ( userDetails.image != null)
        {
            GlideLoader(this).loadUserPicture(userDetails.image, binding.ivUserPhoto)
        }

        if (userDetails.gender == Constants.MALE)
        {
            binding.rbMale.isChecked = true
        }
        else
        {
            binding.rbFemale.isChecked = true
        }

        binding.ivUserPhoto.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)

        if (userDetails.profileCompleted != 0)
        {
            setupActionBar()
            binding.tvTitle.text = resources.getText(R.string.title_edit_profile)
        }
    }

    private fun setupActionBar()
    {
        setSupportActionBar(binding.toolbarUserProfileActivity)
        val actionBar = supportActionBar

        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        binding.toolbarUserProfileActivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null)
        {
            when (v.id){
                R.id.iv_user_photo ->{
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    {
                        showSnackBar("You already have storage permission")
                        Constants.showImageChooser(this)
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_submit->{
                    showProgressDialog()

                    if (selectedImageFileUri != null)
                    {
                        FirestoreClass().uploadImageToCloudStorage(this, selectedImageFileUri)
                    }
                    else
                    {
                        updateUserProfileDetails()
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails()
    {
        if (validateUserProfileDetails())
        {
            val userHashMap = HashMap<String, Any>()
            val mobileNumber = binding.etMobileNumber.text.toString().trim()
            val gender = if (binding.rbMale.isChecked)
            {
                Constants.MALE
            }
            else
            {
                Constants.FEMALE
            }

            if (userProfileImageURL.isNotEmpty())
            {
                userHashMap[Constants.IMAGE] = userProfileImageURL
            }

            if (mobileNumber.isNotEmpty())
            {
                userHashMap[Constants.MOBILE] = mobileNumber.toLong()
            }
            userHashMap[Constants.GENDER] = gender

            userHashMap[Constants.COMPLETE_PROFILE] = 1
            FirestoreClass().updateUserProfileData(this, userHashMap)
        }
    }

    fun userProfileUpdateSuccess()
    {
        hideProgressDialog()
        Toast.makeText(this, resources.getString(R.string.msg_profile_update_success), Toast.LENGTH_LONG).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //showErrorSnackBar("The storage permission is granted")
                Constants.showImageChooser(this)
            }
            else
            {
                Toast.makeText(this, resources.getString(R.string.read_storage_permission_denied), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE)
        {
            if (data != null)
            {
                try {
                    selectedImageFileUri = data.data!!
                    //binding.ivUserPhoto.setImageURI(selectedImageFileUri)
                    GlideLoader(this).loadUserPicture(selectedImageFileUri!!, binding.ivUserPhoto)
                }
                catch (e: IOException)
                {
                    e.printStackTrace()
                    Toast.makeText(this, resources.getString(R.string.image_selection_failed), Toast.LENGTH_LONG).show()
                }
            }
        }
        else if (requestCode == Activity.RESULT_CANCELED)
        {
            Log.e("Request cancelled", "Image selection cancelled")
        }
    }
    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim()) -> {
                showSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), false)
                true
            }
            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(uri: String)
    {
        //hideProgressDialog()
        userProfileImageURL = uri
        updateUserProfileDetails()
    }
}