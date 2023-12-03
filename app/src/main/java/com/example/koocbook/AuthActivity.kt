package com.example.koocbook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {

    object CurrentUser{
        var user: User? = null
    }

    val gson = Gson()
    private val apiHelper = APIHelper("https://6561e92ddcd355c0832451fd.mockapi.io/api/v1/")
    private val userApi = UserAPI(apiHelper)
    private var hello: TextView? = null
    private var emailInput: EditText? = null
    private var passInput: EditText? = null
    private var forgotPass: TextView? = null
    private var loginBtn: Button? = null
    private var user: User? = null

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private suspend fun showToast(message: String) {
        // Получение контекста активности внутри корутины
        val context = withContext(Dispatchers.Main) { this@AuthActivity }

        // Показ сообщения в Toast
        context.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun goToItems(){
        // Получение контекста активности внутри корутины
        val context = withContext(Dispatchers.Main) { this@AuthActivity }

        context.let {
            val intent = Intent(this, ItemsActivity::class.java)
            startActivity(intent)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        hello = findViewById(R.id.hello_auth)
        emailInput = findViewById(R.id.email_input_auth)
        passInput = findViewById(R.id.pass_input_auth)
        loginBtn = findViewById(R.id.login_btn)
        forgotPass = findViewById(R.id.forgot_pass)

        hello?.text = "Войдите в аккаунт"
        emailInput?.hint = "Введите email"
        passInput?.hint = "Введите пароль"
        forgotPass?.text = "Забыли праоль?"

        val goToReg: TextView = findViewById(R.id.go_to_reg)

        goToReg.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }

        loginBtn?.setOnClickListener{
            val login = emailInput?.text.toString().trim()
            val pass = passInput?.text.toString().trim()

            if (login == "" || pass == "")
                Toast.makeText(this, "Не все поля заполнены!", Toast.LENGTH_LONG).show()
            else if (!isValidEmail(login))
                Toast.makeText(this, "Неверный формат адреса электронной почты!", Toast.LENGTH_LONG).show()
            else {
                Log.d("INFO", login)
                Log.d("INFO", pass)
                GlobalScope.launch(Dispatchers.IO){
                    val resp = userApi.getUserByEmail(login)
                    if (!resp.contains("Ошибка")) {
                        val listType = object : TypeToken<List<User>>() {}.type
                        val users: List<User> = gson.fromJson(resp, listType)
                        if (users.isNotEmpty()){
                            user = users[0]
                        }
                        if (user?.email.equals(login) && user?.password.equals(pass) && user != null) {
                            withContext(Dispatchers.Main) {
                                showToast("Вы вошли!")
                                emailInput!!.text.clear()
                                passInput!!.text.clear()
                            }
                            CurrentUser.user = user
                            goToItems()
                        } else {
                            withContext(Dispatchers.Main) {
                                showToast("Пользователь не существует!")
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            showToast("Ошибка сервера! Повторите попытку позже")
                        }
                    }
                }
                //val db = DbHelper(this, null)

                //if (db.getUser(login, pass)) {

            }

        }
    }

}