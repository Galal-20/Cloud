package com.example.cloud.ui.map


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.location.Address

class SuggestionAdapter(context: Context, private val addresses: List<Address>) : ArrayAdapter<Address>(context, 0, addresses) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val address = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)

        val textView: TextView = view.findViewById(android.R.id.text1)
        textView.text = address?.getAddressLine(0)

        return view
    }
}
