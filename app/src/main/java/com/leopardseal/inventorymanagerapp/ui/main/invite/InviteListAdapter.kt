package com.leopardseal.inventorymanagerapp.ui.main.invite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.responses.Orgs
import com.squareup.picasso.Picasso

class InviteListAdapter(context: Context, dataArrayList : ArrayList<Orgs?>?): ArrayAdapter<Orgs?>(
    context,
    R.layout.invite_list_item,
    dataArrayList!!
){

    interface OnItemButtonClickListener {
        fun onItemButtonClick(position: Int)
    }
    var listener: OnItemButtonClickListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        var invite : Orgs? = getItem(position)

        if(view == null){
            view  = LayoutInflater.from(context).inflate(R.layout.invite_list_item, parent, false)
        }
        val listImage = view!!.findViewById<ImageView>(R.id.listImage)
        val listName = view.findViewById<TextView>(R.id.listName)
        val listRole = view.findViewById<TextView>(R.id.listRole)
        val acceptButton = view.findViewById<Button>(R.id.listAccept)
        Picasso.get()
            .load(invite!!.imageUrl)
            .placeholder(R.drawable.default_img)
            .error(R.drawable.default_img)
            .into(listImage);

        listName.text = invite.name
        listRole.text = invite.role
        acceptButton.setOnClickListener(){
            listener?.onItemButtonClick(position)

        }
        return view
    }
}