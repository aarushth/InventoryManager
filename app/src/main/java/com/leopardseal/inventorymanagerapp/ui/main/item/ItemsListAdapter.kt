package com.leopardseal.inventorymanagerapp.ui.main.item

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.responses.Items
import com.squareup.picasso.Picasso

class ItemsListAdapter(context: Context, dataArrayList : ArrayList<Items?>?): ArrayAdapter<Items?>(context, R.layout.item_list_item, dataArrayList!!){



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        var item : Items? = getItem(position)

        if(view == null){
            view  = LayoutInflater.from(context).inflate(R.layout.item_list_item, parent, false)
        }
        val listImage = view!!.findViewById<ImageView>(R.id.listImage)
        val listName = view.findViewById<TextView>(R.id.listName)
        val listBarcode = view.findViewById<TextView>(R.id.listBarcode)
        val listQuantity = view.findViewById<TextView>(R.id.listQuantity)
        Picasso.get()
            .load(item!!.imageUrl)
            .placeholder(R.drawable.default_img)
            .error(R.drawable.default_img)
            .into(listImage);

        listName.text = item.name
        listBarcode.text = item.barcode
        listQuantity.text = item.quantity.toString()

        return view
    }
}