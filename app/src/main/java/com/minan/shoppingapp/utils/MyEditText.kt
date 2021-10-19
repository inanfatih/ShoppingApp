package com.minan.shoppingapp.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MyEditText(context:Context, attributeSet: AttributeSet): AppCompatEditText(context, attributeSet) {

    init{
        applyFont()
    }

    private fun applyFont() {
        val typeface: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf" )
        setTypeface(typeface)
    }
}