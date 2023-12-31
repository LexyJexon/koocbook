package com.example.koocbook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemsActivity : AppCompatActivity() {

    val gson = Gson()
    private val apiHelper = APIHelper("https://6561e92ddcd355c0832451fd.mockapi.io/api/v1/")
    private val itemApi = ItemAPI(apiHelper)

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val itemsList: RecyclerView = findViewById(R.id.items_to_list)
        var items = listOf<Item>()
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = NewItemsAdapter(items, this)

       /* items.add(Item(1, "borsh", "Борщ с курицей(с капустой)", "Предлагаю рецепт борща–лайт с курицей и свежей капустой. Легкий и вкусный домашний суп порадует всю семью. В борщ можно не добавлять картофель, тогда блюдо станет почти диетическим. ", "Ингредиенты:\n" +
                "1 средняя свекла, тертая\n" +
                "1 средняя морковь, тертая\n" +
                "1 средний лук, мелко нарезанный\n" +
                "3 картошки, нарезанные кубиками\n" +
                "1/2 кочана капусты, нарезанной тонкой соломкой\n" +
                "2 помидора, нарезанные\n" +
                "1 зеленый перец, нарезанный\n" +
                "2-3 зубчика чеснока, измельченные\n" +
                "1/4 чашки томатной пасты\n" +
                "2 лавровых листа\n" +
                "5-6 крупных свежих пучков укропа и петрушки, измельченные\n" +
                "Соль и перец по вкусу\n" +
                "2 столовые ложки растительного масла\n" +
                "1/4 чашки уксуса (по желанию)\n" +
                "Инструкции:\n" +
                "1. Подготовка овощей: В большой кастрюле разогрейте масло. Обжаривайте лук, морковь и свеклу до мягкости.\n" +
                "\n" +
                "2. Добавление томатной пасты: Добавьте томатную пасту и обжаривайте еще 5-7 минут до того, как она станет бордово-красной.\n" +
                "\n" +
                "3. Приготовление бульона: Добавьте кастрюлю воды (примерно 2-2.5 литра), картошку, капусту, помидоры, чеснок, лавровый лист, перец и уксус. Доведите до кипения, затем уменьшите огонь и доведите до готовности овощей.\n" +
                "\n" +
                "4. Приправление: Добавьте соль и перец по вкусу. Уберите с огня и добавьте измельченные зелень и уксус (если используете).\n" +
                "\n" +
                "5. Подача: Подавайте горячим с ложкой сметаны.\n" +
                "\n" +
                "Приятного аппетита!",
            listOf(),120, 0))
        items.add(Item(2, "plov", "Плов с курицей", "Приготовьте настоящий плов с курицей. Рассыпчатый рис, нежные кусочки куриного мяса, специи, пряности и казан. Такой плов вы можете приготовить на открытом огне или дома, на плите.", "Ингредиенты:\n" +
                "2 чашки длиннозернового риса\n" +
                "500 г куриного мяса, нарезанного кубиками\n" +
                "2 луковицы, нарезанные полукольцами\n" +
                "2 моркови, нарезанные кружочками или ломтями\n" +
                "4 столовые ложки масла (предпочтительно растительное)\n" +
                "1 головка чеснока, измельченная\n" +
                "1 чайная ложка зиры (тмин)\n" +
                "1 чайная ложка куркумы\n" +
                "Соль и перец по вкусу\n" +
                "4 чашки кипятка или бульона\n" +
                "Инструкции:\n" +
                "1. Подготовка ингредиентов: Промойте рис в холодной воде до тех пор, пока вода не станет прозрачной. Оставьте рис в воде для промывки.\n" +
                "\n" +
                "2. Обжаривание мяса: Разогрейте масло в кастрюле или глубокой сковороде. Обжаривайте куриное мясо до золотистой корки. Вынесите мясо на бумажное полотенце, чтобы избавиться от лишнего масла.\n" +
                "\n" +
                "3. Обжаривание лука и моркови: В оставшемся масле обжаривайте лук до прозрачности. Затем добавьте морковь и обжаривайте вместе с луком до мягкости.\n" +
                "\n" +
                "4. Добавление специй и чеснока: Добавьте зиру, куркуму, чеснок, соль и перец. Помешивайте в течение 1-2 минут, чтобы специи раскрыли свой аромат.\n" +
                "\n" +
                "5. Добавление риса: Добавьте промытый рис и обжаривайте вместе с остальными ингредиентами в течение 2-3 минут.\n" +
                "\n" +
                "6. Варка: Влейте кипяток или бульон в кастрюлю. Доведите до кипения, затем уменьшите огонь до минимума, накройте кастрюлю крышкой и варите до готовности риса (обычно 15-20 минут).\n" +
                "\n" +
                "7. Подача: Перед подачей аккуратно перемешайте плов вилкой, чтобы рассыпать рис и равномерно распределить ингредиенты. Подавайте горячим.\n" +
                "\n" +
                "Приятного аппетита!",listOf(),120, 0))
        items.add(Item(3, "beshparmak", "Бешпармак с говядиной", "Бешбармак - казахское национальное блюдо (насколько мне известно, - это скорее русский вариант названия этого кушанья). Если перевести дословно - \"беш\" по-казахски - пять, а \"бармак\" - палец/пальцы. Казахские и другие кочевые племена не использовали во время еды столовые приборы, а брали мясо руками - отсюда и название :-)\n" +
                "Блюдо готовится из баранины, говядины и конины. Поскольку, конина не всем доступна, бешбармак можно готовить, из соображений доступности, из баранины и говядины или из одного вида мяса.\n" +
                "Желаю всем приятного аппетита!", "Ингредиенты:\n" +
                "500 г говядины (лучше всего использовать мраморную часть), нарезанной кубиками\n" +
                "500 г муки\n" +
                "1 яйцо\n" +
                "Соль по вкусу\n" +
                "2 луковицы, нарезанные полукольцами\n" +
                "4 картошки, нарезанные кубиками\n" +
                "2 моркови, нарезанные кружочками\n" +
                "1/2 стакана зелени (укроп, петрушка), мелко нарезанной\n" +
                "Соль и перец по вкусу\n" +
                "Инструкции:\n" +
                "Для теста:\n" +
                "1. В глубокой миске смешайте муку, яйцо и соль. Постепенно добавляйте теплую воду и замешивайте тесто до получения мягкой и эластичной консистенции.\n" +
                "\n" +
                "2. Дайте тесту отдохнуть под чистой тряпкой при комнатной температуре в течение примерно 30 минут.\n" +
                "\n" +
                "3. Раскатайте тесто и разрежьте его на небольшие квадраты или полосы.\n" +
                "\n" +
                "Приготовление бульона:\n" +
                "1. В кастрюле с водой варите говядину до готовности, добавляя соль по вкусу. Когда мясо готово, выньте его, а воду оставьте для варки теста.\n" +
                "\n" +
                "2. Варите нарезанные кусочки теста в этом бульоне до тех пор, пока они не всплывут.\n" +
                "\n" +
                "Приготовление начинки:\n" +
                "1. В другой сковороде обжаривайте лук, морковь и картошку до мягкости на растительном масле.\n" +
                "\n" +
                "2. Добавьте отваренное мясо, зелень, соль и перец по вкусу. Помешивайте и тушите в течение нескольких минут.\n" +
                "\n" +
                "Подача:\n" +
                "1. Выложите отваренные кусочки теста на большую тарелку. На верх добавьте приготовленную начинку.\n" +
                "\n" +
                "2. Бешпармак готов к подаче! Традиционно его подают на большой доске, но вы можете сервировать так, как удобно.\n" +
                "\n" +
                "Приятного аппетита!",listOf(),120, 0))*/

        GlobalScope.launch(Dispatchers.IO) {
            items = itemApi.getAllItems()
           // Log.d("ITEM", resp)
            for (item in items){
                Log.d("ITEM", item.title + item.image + "\n")
            }
            withContext(Dispatchers.Main) {
                itemsList.adapter = NewItemsAdapter(items, this@ItemsActivity)
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