package com.example.koocbook
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import java.io.ByteArrayOutputStream
import java.net.URI
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ItemAddActivity: AppCompatActivity() {
    val gson = Gson()
    private val apiHelper = APIHelper("https://6561e92ddcd355c0832451fd.mockapi.io/api/v1/")
    private val itemApi = ItemAPI(apiHelper)
    private var image: ImageView? = null
    private var uploadImageButton: Button? = null
    private var title : TextInputEditText? = null
    private var description : TextInputEditText? = null
    private var recipe : TextInputEditText? = null
    private var addItemButton: Button? = null
    private val storage = Firebase.storage.reference.child("images")
    private var downloadUrl: String? = null
    private var cookTime : TextInputEditText? = null

    private suspend fun showToast(message: String) {
        // Получение контекста активности внутри корутины
        val context = withContext(Dispatchers.Main) { this@ItemAddActivity }

        // Показ сообщения в Toast
        context.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_add)


        image = findViewById(R.id.add_item_image)
        uploadImageButton = findViewById(R.id.upload_img_btn)
        title = findViewById(R.id.add_item_title)
        description = findViewById(R.id.add_item_text)
        recipe = findViewById(R.id.add_item_recipe_text)
        addItemButton = findViewById(R.id.add_item_btn)
        cookTime = findViewById(R.id.add_item_cook_time)

        cookTime?.hint = "Время приготовления"
        title?.hint = "Название блюда"
        description?.hint = "Описание блюда"
        recipe?.hint = "Рецепт"

        uploadImageButton?.setOnClickListener{
            getImage()
        }

        addItemButton?.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    itemApi.createItem(
                        downloadUrl!!,
                        title!!.text.toString(),
                        description!!.text.toString(),
                        recipe!!.text.toString(),
                        arrayOf(),
                        cookTime!!.text.toString().toInt(),
                        AuthActivity.CurrentUser.user!!.id
                    )
                    withContext(Dispatchers.Main) {
                        showToast("Рецепт добавлен")
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        e.printStackTrace()
                    }
                }
            }
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101 && data != null && data.data != null){
            if(resultCode == RESULT_OK){
                Log.d("MyLog", "Image uri : " + data.data)
                image?.setImageURI(data.data)
                uploadImage()

            }
        }
    }

    private fun getImage(){
        val explorer = Intent()
        explorer.type = "image/*"
        explorer.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(explorer, 101)
    }

    private fun uploadImage(){
        val bitmap = image?.drawable?.toBitmap()
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArray = baos.toByteArray()
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = currentDateTime.format(formatter)
        val imagesRef = storage.child(formattedDateTime + "uploaded")
        val up = imagesRef.putBytes(byteArray)
        up.addOnSuccessListener {
            // Загрузка успешно завершена
            imagesRef.downloadUrl.addOnSuccessListener { uri ->
                downloadUrl = uri.toString()
                // Получение ссылки на загруженное изображение (downloadUrl)
                println("Ссылка на загруженное изображение: $downloadUrl")
            }.addOnFailureListener{
                val message = "Ошибка при получении URL загруженного файла: ${it.message}"
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            // Обработка ошибки при загрузке
            val message = "Ошибка при загрузке изображения: ${it.message}"
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }

}