package com.example.koocbook

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

import com.google.gson.*
import java.lang.reflect.Type

class ItemIngredientsDeserializer : JsonDeserializer<List<Any>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<Any> {
        val list = mutableListOf<Any>()
        if (json != null && json.isJsonArray) {
            val jsonArray = json.asJsonArray
            for (element in jsonArray) {
                when {
                    element.isJsonPrimitive -> {
                        if (element.asJsonPrimitive.isNumber) {
                            list.add(element.asInt)
                        } else if (element.asJsonPrimitive.isString) {
                            list.add(element.asString)
                        }
                    }
                }
            }
        }
        return list
    }
}

class ItemAPI(private val apiHelper: APIHelper) {
    private val gson = Gson()

    @Throws(IOException::class)
    suspend fun createItem(image: String, title: String, desc: String, recipe: String, ingredients : List<Any>, cookTimeInMinutes: Int, authorId: Int): String {
        // Реализация создания пользователя через APIHelper
        val url = "items"
        val allItems = getAllItems() // Получение списка всех пользователей
        val lastItemId = allItems.maxByOrNull{ it.id }?.id ?: 0
        /*
        { it.id } - это лямбда-выражение, которое извлекает id из каждого объекта User в списке.
        Здесь it ссылается на каждый элемент списка последовательно.
?.id - оператор безопасного вызова. Он проверяет, что результат лямбда-выражения не равен null и
пытается вызвать id. Если текущий элемент списка равен null, то операция вызова id не выполняется и
 результат будет null.
?: 0 - оператор Элвиса (Elvis operator). Он возвращает левую часть, если она не равна null, в
противном случае возвращает правую часть. В данном случае, если результат операции безопасного
вызова (вызов id) равен null, то будет возвращено значение 0.
Таким образом, выражение { it.id }?.id ?: 0 будет вычислять максимальное значение id среди объектов
 User в списке, предотвращая возможные ошибки, связанные с null значениями или пустыми списками.
        */

        val newItemId = lastItemId + 1 // Увеличение максимального ID на единицу для нового пользователя

        // Используйте newUserId при создании нового пользователя
        val newItem = Item(newItemId, image, title, desc, recipe, ingredients, cookTimeInMinutes, authorId)
        val jsonBody = "{\"id\": \"${newItem.id}\", \"image\": \"${newItem.image}\", \"title\": \"${newItem.title}\", \"description\": \"${newItem.desc}\", \"recipe\": \"${newItem.recipe}\", \"ingredients\": \"${newItem.ingredients}\", \"cookTimeInMinutes\": \"${newItem.cookTimeInMinutes}\", \"authorId\": \"${newItem.authorId}\"}"
        return try {
            apiHelper.postRequest(url, jsonBody).body?.string() ?: ""
            // Обработка полученных данных
        } catch (e: IOException) {
            // Обработка ошибок сети или некорректных запросов
            val msg = "Ошибка: ${e.message}"
            println(msg)
            msg
        }

    }

    @Throws(IOException::class)
    suspend fun getItem(itemId: String): String {
        // Реализация получения информации о пользователе через APIHelper
        val url = "items/$itemId"
        return try {
            apiHelper.getRequest(url).body?.string() ?: ""
            // Обработка полученных данных
        } catch (e: IOException) {
            // Обработка ошибок сети или некорректных запросов
            val msg = "Ошибка: ${e.message}"
            println(msg)
            msg
        }
    }

    @Throws(IOException::class)
    suspend fun getItemByTitle(title: String): String {
        val url = "items?title=$title"
        return try {
            apiHelper.getRequest(url).body?.string() ?: ""
            // Обработка полученных данных
        } catch (e: IOException) {
            // Обработка ошибок сети или некорректных запросов
            val msg = "Ошибка: ${e.message}"
            println(msg)
            msg
        }
    }

    @Throws(IOException::class)
    suspend fun getItemByAuthorId(authorId: Int): String {
        val url = "items?authorId=$authorId"
        return try {
            apiHelper.getRequest(url).body?.string() ?: ""
            // Обработка полученных данных
        } catch (e: IOException) {
            // Обработка ошибок сети или некорректных запросов
            val msg = "Ошибка: ${e.message}"
            println(msg)
            msg
        }
    }

    fun parseItemsFromJson(resp: String): List<Item> {
        val gson = GsonBuilder()
            .registerTypeAdapter(
                object : TypeToken<List<Any>>() {}.type,
                ItemIngredientsDeserializer()
            )
            .create()
        // Предполагая, что jsonString - это ваш JSON-ответ
        val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
        val items: List<Map<String, Any>> = gson.fromJson(resp, listType)

        val updatedItems = items.map { item ->
            val ingredients = item["ingredients"]
            if (ingredients is String) {
                val ingredientsArray = ingredients.split(",") // Преобразовать строку в массив строк
                item.plus("ingredients" to ingredientsArray)
            } else {
                item
            }
        }
        val itemList = updatedItems.map { item ->
            Item(
                item["id"].toString().toInt(),
                item["image"].toString(),
                item["title"].toString(),
                item["description"].toString(),
                item["recipe"].toString(),
                (item["ingredients"] as List<*>).map { it.toString() }, // Преобразовать List<Any> в List<String>
                (item["cookTimeInMinutes"].toString().toDoubleOrNull() ?: 0.0).toInt(),
                (item["authorId"].toString().toDoubleOrNull() ?: 0.0).toInt(),
            )
        }

        return itemList
    }

    @Throws(IOException::class)
    suspend fun updateItem(itemId: String, item : Item): String {
        // Реализация обновления информации о пользователе через APIHelper
        val url = "items/$itemId"
        val jsonBody = "{\"id\": \"${item.id}\", \"image\": \"${item.image}\", \"title\": \"${item.title}\", \"description\": \"${item.desc}\", \"recipe\": \"${item.recipe}\", \"ingredients\": \"${item.ingredients}\", \"cookTimeInMinutes\": \"${item.cookTimeInMinutes}\", \"authorId\": \"${item.authorId}\"}"
        return try {
            apiHelper.putRequest(url,jsonBody).body?.string() ?: ""
            // Обработка полученных данных
        } catch (e: IOException) {
            // Обработка ошибок сети или некорректных запросов
            val msg = "Ошибка: ${e.message}"
            println(msg)
            msg
        }
    }

    @Throws(IOException::class)
    suspend fun deleteItem(itemId: String): String {
        // Реализация delete user через APIHelper
        val url = "items/$itemId"
        return try {
            apiHelper.deleteRequest(url).body?.string() ?: ""
            // Обработка полученных данных
        } catch (e: IOException) {
            // Обработка ошибок сети или некорректных запросов
            val msg = "Ошибка: ${e.message}"
            println(msg)
            msg
        }
    }

    @Throws(IOException::class)
    suspend fun getAllItems(): List<Item> {
        val url = "items"
        val response = apiHelper.getRequest(url)
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            // Преобразование JSON в список объектов Item
            return parseItemsFromJson(responseBody)
        } else {
            throw IOException("Unexpected code $response")
        }
    }

    private fun parseItems(responseBody: String): List<Item> {
        // Парсинг JSON для получения списка пользователей
        val listType = object : TypeToken<List<Item>>() {}.type
        return gson.fromJson(responseBody, listType) // Значение по умолчанию или реальный список пользователей
    }
}