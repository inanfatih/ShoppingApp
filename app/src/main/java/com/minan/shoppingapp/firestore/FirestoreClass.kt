package com.minan.shoppingapp.firestore

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.minan.shoppingapp.ui.activities.LoginActivity
import com.minan.shoppingapp.ui.activities.RegisterActivity
import com.minan.shoppingapp.ui.activities.UserProfileActivity
import com.minan.shoppingapp.models.User
import com.minan.shoppingapp.ui.activities.SettingsActivity
import com.minan.shoppingapp.utils.Constants

class FirestoreClass {

    private val firestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, user: User)
    {
        firestore
            .collection(Constants.USERS)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener{
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user"
                )
            }
    }

    fun getCurrentUserID(): String
    {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null)
        {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity)
    {
        firestore
            .collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(Constants.MYAPP_PREFERENCES, Context.MODE_PRIVATE)

                sharedPreferences.edit().putString(
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                ).apply()

                when (activity)
                {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity ->{
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e->
                Log.e(activity.javaClass.simpleName, e.message.toString())
                when (activity)
                {
                    is LoginActivity ->{
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity ->{
                        activity.hideProgressDialog()
                    }
                }
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>)
    {
        firestore
            .collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity)
                {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e->
                Log.e(activity.javaClass.simpleName, "Error while updating the user details", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageUri: Uri?)
    {
        if (imageUri != null)
        {
            val ref: StorageReference = FirebaseStorage
                .getInstance()
                .reference
                .child(Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(activity, imageUri))

            ref
                .putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image Url", uri.toString())

                            when (activity) {
                                is UserProfileActivity -> {
                                    activity.imageUploadSuccess(uri.toString())
                                }
                            }
                        }
                }
                .addOnFailureListener{
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(activity.javaClass.simpleName,
                        it.message,
                        it)
                }
        }
    }
}