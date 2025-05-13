package com.leopardseal.inventorymanagerapp.ui.main.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.leopardseal.inventorymanagerapp.R
import com.leopardseal.inventorymanagerapp.data.responses.Items
import com.squareup.picasso.Picasso

class ItemsListAdapter(private val items: List<Items?>, private val listener: OnItemClickListener):
    RecyclerView.Adapter<ItemsListAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.listImage)
        val name: TextView = view.findViewById(R.id.listName)
        val quantity: TextView = view.findViewById(R.id.listQuantity)
        val quantityLabel: TextView = view.findViewById(R.id.listQuantityLabel)
        val barcode : TextView = view.findViewById(R.id.listBarcode)
        val itemCard = view.findViewById<CardView>(R.id.itemCard)
    }
    interface OnItemClickListener {
        fun onItemClick(items: Items)
    }

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//
//        var view = convertView
//        var item : Items? = getItem(position)
//
//        if(view == null){
//            view  = LayoutInflater.from(context).inflate(R.layout.item_list_item, parent, false)
//        }
//        val listImage = view!!.findViewById<ImageView>(R.id.listImage)
//        val listName = view.findViewById<TextView>(R.id.listName)
//        val listBarcode = view.findViewById<TextView>(R.id.listBarcode)
//        val listQuantity = view.findViewById<TextView>(R.id.listQuantity)
//        Picasso.get()
//            .load(item!!.imageUrl)
//            .placeholder(R.drawable.default_img)
//            .error(R.drawable.default_img)
//            .into(listImage);
//
//        listName.text = item.name
//        listBarcode.text = item.barcode
//        listQuantity.text = item.quantity.toString()
//
//        return view
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemsListAdapter.ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemsListAdapter.ItemViewHolder, position: Int) {
        val item = items[position]

        Picasso.get()
            .load(item!!.imageUrl)
            .placeholder(R.drawable.default_img)
            .into(holder.image)

        holder.name.text = item.name
        holder.quantity.text = "${item.quantity}"
        holder.barcode.text = item.barcode
        var text = "in stock"
        when{
            item.quantity <= 0L ->{
                text = "out of stock"
            }
            item.quantity > 0L && (item.quantity <= item.alert) ->{
                text = "low stock"
            }
            item.quantity > item.alert ->{
                text = "in stock"
            }
        }
        holder.quantityLabel.text = text


        holder.itemCard.setOnClickListener {
            item?.let { listener.onItemClick(item) }
            // Handle click here
//            Toast.makeText(context, "Clicked: ${item?.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = items.size
}