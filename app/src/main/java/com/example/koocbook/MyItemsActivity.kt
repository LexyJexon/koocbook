package com.example.koocbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyItemsActivity: AppCompatActivity() {

    val gson = Gson()
    private val apiHelper = APIHelper("https://6561e92ddcd355c0832451fd.mockapi.io/api/v1/")
    private val itemApi = ItemAPI(apiHelper)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myitems)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val itemsList: RecyclerView = findViewById(R.id.items_to_mylist)
        var items = listOf<Item>()
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = NewItemsAdapter(items, this)
        GlobalScope.launch(Dispatchers.IO) {
            val resp = itemApi.getItemByAuthorId(AuthActivity.CurrentUser.user!!.id)
            Log.d("ITEM", resp)
            val itemsFromJson = itemApi.parseItemsFromJson(resp)
            items = itemsFromJson
            for (item in items){
                Log.d("ITEM1", item.title + item.image + "\n")
            }
            withContext(Dispatchers.Main) {
                itemsList.adapter = NewItemsAdapter(items, this@MyItemsActivity)
                itemsList.adapter?.notifyDataSetChanged()
            }
        }



        //itemsList.layoutManager = LinearLayoutManager(this)
        //itemsList.adapter = NewItemsAdapter(items, this)
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