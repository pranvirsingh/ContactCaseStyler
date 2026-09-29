package com.pranvir.contactcasestyler

import android.view.View
import android.widget.AdapterView

/** One-liner spinner listener so the activity stays readable. */
class SimpleSelectedListener(private val onSelect: () -> Unit) : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = onSelect()
    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
}
