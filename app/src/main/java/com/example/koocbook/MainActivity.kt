package com.example.koocbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException

class MainActivity : AppCompatActivity() {

    val gson = Gson()
    private val apiHelper = APIHelper("https://6561e92ddcd355c0832451fd.mockapi.io/api/v1/")
    private val userApi = UserAPI(apiHelper)
    private var hello: TextView? = null
    private var emailInput: EditText? = null
    private var passInput: EditText? = null
    private var goToAuth: TextView? = null
    private var regBtn: Button? = null

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private suspend fun showToast(message: String) {
        // Получение контекста активности внутри корутины
        val context = withContext(Dispatchers.Main) { this@MainActivity }

        // Показ сообщения в Toast
        context.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun goToAuth(){
        // Получение контекста активности внутри корутины
        val context = withContext(Dispatchers.Main) { this@MainActivity }

        context.let {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello = findViewById(R.id.hello)
        emailInput = findViewById(R.id.email_input)
        passInput = findViewById(R.id.pass_input)
        regBtn = findViewById(R.id.reg_btn)
        goToAuth = findViewById(R.id.go_to_auth)

        hello?.text = "Регистрация"
        emailInput?.hint = "Введите email"
        passInput?.hint = "Введите пароль"

        regBtn?.setOnClickListener{
            val email: String = emailInput?.text.toString().trim()
            val pass: String = passInput?.text.toString().trim()
            if (email == "" || pass == "")
                Toast.makeText(this, "Не все поля заполнены!", Toast.LENGTH_LONG).show()
            else if (!isValidEmail(email))
                Toast.makeText(this, "Неверный формат адреса электронной почты!", Toast.LENGTH_LONG).show()
            else {
                Log.d("INFO", email)
                Log.d("INFO", pass)
                GlobalScope.launch(Dispatchers.IO){
                    try{
                        val resp = userApi.getUserByEmail(email)
                        Log.d("Responce",resp)
                        if (resp.isNotEmpty() && resp != "[]"){
                            if (resp.contains("Ошибка") || resp.contains("Error")){
                                withContext(Dispatchers.Main) {
                                    showToast("Ошибка сервера! Повторите попытку позже")
                                    emailInput?.text?.clear()
                                    passInput?.text?.clear()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    showToast("Пользователь существует! Переход к авторизации")
                                    emailInput?.text?.clear()
                                    passInput?.text?.clear()
                                }
                                goToAuth()
                            }
                        }
                        userApi.createUser(email, pass)
                        withContext(Dispatchers.Main){
                            showToast("Пользователь добавлен")
                            emailInput?.text?.clear()
                            passInput?.text?.clear()
                        }
                        goToAuth()
                    } catch (e: IOException){
                        e.printStackTrace()
                    }
                }
                //val user = User(email, pass)
               // val db = DbHelper(this, null)
                //db.addUser(user)
            }
        }

        goToAuth?.setOnClickListener{
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

    }


}

