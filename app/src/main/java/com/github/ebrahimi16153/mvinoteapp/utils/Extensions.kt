package com.github.ebrahimi16153.mvinoteapp.utils

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.github.ebrahimi16153.mvinoteapp.R

// spinner config
fun Spinner.setUpSpinnerByAdapter(list: MutableList<String>, item: (String) -> Unit) {
    val adapter = ArrayAdapter(context, R.layout.spinner_item, list)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    this.adapter = adapter
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            item(list[position])
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}

    }

}

