package com.example.koocbook

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ItemsAdapter(var items: List<Item>, var context: Context): RecyclerView.Adapter<ItemsAdapter.NewViewHolder>() {

    class NewViewHolder(view: View): RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.item_image)
        val title: TextView = view.findViewById(R.id.item_title)
        val text: TextView = view.findViewById(R.id.item_text)
        val btn: Button = view.findViewById(R.id.item_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false)
        return NewViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: NewViewHolder, position: Int) {
        holder.itemView.setBackgroundResource(R.drawable.round_corner)
        holder.title.text = items[position].title
        holder.text.text = items[position].desc

        /*val imageId = context.resources.getIdentifier(
            items[position].image,
            "drawable",
            context.packageName
        )

        holder.image.setImageResource(imageId)
        holder.image.foreground = ResourcesCompat.getDrawable(context.resources,R.drawable.round_corner, null )*/

        Glide.with(context)
            .load(items[position].image)
            .placeholder(android.R.drawable.ic_menu_gallery) // Заглушка, показываемая во время загрузки изображения
            .error(R.drawable.no_icon) // Заглушка, показываемая в случае ошибки загрузки
            .into(holder.image)

        holder.btn.setOnClickListener{
            val intent = Intent(context, ItemActivity::class.java)

            intent.putExtra("itemTitle", items[position].title)
            intent.putExtra("itemText", items[position].recipe)

            context.startActivity(intent)
        }
    }
}