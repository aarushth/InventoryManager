package com.leopardseal.inventorymanagerapp.ui.home.org

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import com.squareup.picasso.Picasso

class OrgsListAdapter(context: Context, dataArrayList : ArrayList<Orgs?>?): ArrayAdapter<Orgs?>(context, R.layout.org_list_item, dataArrayList!!){



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        var org : Orgs? = getItem(position)

        if(view == null){
            view  = LayoutInflater.from(context).inflate(R.layout.org_list_item, parent, false)
        }
        val listImage = view!!.findViewById<ImageView>(R.id.listImage)
        val listName = view.findViewById<TextView>(R.id.listName)

        Picasso.get()
            .load(org!!.imageUrl)
            .placeholder(R.drawable.default_img)
            .error(R.drawable.default_img)
            .into(listImage);

        listName.text = org.name

        return view
    }
}