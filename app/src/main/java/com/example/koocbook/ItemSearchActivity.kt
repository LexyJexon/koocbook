package com.example.koocbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemSearchActivity: AppCompatActivity() {
    val gson = Gson()
    private val apiHelper = APIHelper("https://6561e92ddcd355c0832451fd.mockapi.io/api/v1/")
    private val itemApi = ItemAPI(apiHelper)
    private var search : EditText? = null
    private var search_btn : Button? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_item)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val itemsList: RecyclerView = findViewById(R.id.items_search)
        var items = listOf<Item>()
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = NewItemsAdapter(items, this)
        search = findViewById(R.id.search_input)
        search_btn = findViewById(R.id.item_btn)
        search_btn?.setOnClickListener{
            val userInput = search?.text

            GlobalScope.launch(Dispatchers.IO) {
                items = itemApi.getAllItems()
                // Log.d("ITEM", resp)
                for (item in items){
                    Log.d("ITEM", item.title + item.image + "\n")
                }
                val searchResult = items.filter {
                    (userInput != null) && it.title.contains(userInput, ignoreCase = true)
                }
                withContext(Dispatchers.Main) {
                    itemsList.adapter = NewItemsAdapter(searchResult, this@ItemSearchActivity)
                }

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            R.id.add_recipe -> {
                val intent = Intent(this, ItemAddActivity::class.java)
                startActivity(intent)
            }
            R.id.my_recipes -> {
                val intent = Intent(this, MyItemsActivity::class.java)
                startActivity(intent)
            }
            R.id.search -> {
                val intent = Intent(this, ItemSearchActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

}

