package com.example.koocbook

import java.io.IOException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class UserAPI(private val apiHelper: APIHelper) {

    private val gson = Gson()

    @Throws(IOException::class)
    suspend fun createUser(email: String, pass: String): String {
        // Реализация создания пользователя через APIHelper
        val url = "users"
        val allUsers = getAllUsers() // Получение списка всех пользователей
        val lastUserId = allUsers.maxByOrNull{ it.id }?.id ?: 0
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

        val newUserId = lastUserId + 1 // Увеличение максимального ID на единицу для нового пользователя

        // Используйте newUserId при создании нового пользователя
        val newUser = User(newUserId, email, pass)
        val jsonBody = "{\"id\": \"${newUser.id}\", \"email\": \"${newUser.email}\", \"password\": \"${newUser.password}\"}"
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
    suspend fun getUser(userId: String): String {
        // Реализация получения информации о пользователе через APIHelper
        val url = "users/$userId"
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
    suspend fun getUserByEmail(email: String): String {
        val url = "users?email=$email"
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
    suspend fun updateUser(userId: String, user: User): String {
        // Реализация обновления информации о пользователе через APIHelper
        val url = "users/$userId"
        val jsonBody = "{\"id\": \"${user.id}\", \"email\": \"${user.email}\", \"password\": \"${user.password}\"}"
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
    suspend fun deleteUser(userId: String): String {
        // Реализация delete user через APIHelper
        val url = "users/$userId"
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
    suspend fun getAllUsers(): List<User> {
        val url = "users"
        val response = apiHelper.getRequest(url)
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            // Преобразование JSON в список объектов User
            return parseUsers(responseBody)
        } else {
            throw IOException("Unexpected code $response")
        }
    }

    private fun parseUsers(responseBody: String): List<User> {
        // Парсинг JSON для получения списка пользователей
        val listType = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(responseBody, listType) // Значение по умолчанию или реальный список пользователей
    }

}
