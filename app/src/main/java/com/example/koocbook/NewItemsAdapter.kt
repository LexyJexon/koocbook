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

class NewItemsAdapter (private val items: List<Item>, private val context: Context) : RecyclerView.Adapter<NewItemsAdapter.NewViewHolder>() {

    // ViewHolder содержит ссылки на представления элемента списка
    class NewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        return items.size
    }

    override fun onBindViewHolder(holder: NewViewHolder, position: Int) {
        val currentItem = items[position]

        // Установка значений в представления элемента списка из объекта Item
        holder.title.text = currentItem.title
        holder.text.text = currentItem.desc

        // Использование Glide для загрузки изображения из Firebase Storage
        Glide.with(context)
            .load(currentItem.image) // currentItem.image содержит ссылку на изображение
            .into(holder.image)

        holder.btn.setOnClickListener {
            // Обработка нажатия на кнопку, если нужно
            val intent = Intent(context, ItemActivity::class.java)

            intent.putExtra("itemTitle", items[position].title)
            intent.putExtra("itemText", items[position].recipe)

            context.startActivity(intent)
        }
    }
}