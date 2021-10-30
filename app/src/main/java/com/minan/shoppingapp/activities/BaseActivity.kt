package com.minan.shoppingapp.activities

import android.app.Dialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.minan.shoppingapp.R
import com.minan.shoppingapp.databinding.ActivityBaseBinding

private lateinit var binding: ActivityBaseBinding
open class BaseActivity : AppCompatActivity() {

    private lateinit var progressDialog: Dialog

    fun showSnackBar(message: String, errorMessage: Boolean = false)
    {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view

        if (errorMessage)
        {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorSnackBarError))
        }
        else
        {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.colorSnackBarSuccess))
        }
        snackBar.show()
    }

    fun showToastMessage(activity: AppCompatActivity, text: String)
    {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }

    fun showProgressDialog(text: String = "")
    {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialog_progress)
        //progressDialog.tv_progress_text.text = text
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    fun hideProgressDialog()
    {
        progressDialog.dismiss()
    }
}