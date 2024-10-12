package com.example.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "people.db", null, 2) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """CREATE TABLE persons (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT,
                    achievement TEXT,
                    info TEXT)"""
            )

            insertPerson(
                db, "Сергей Брин",
                "Один из основателей Google",
                "Работал над Android и Google, разработчик Android Studio"
            )
            insertPerson(
                db, "Билл Гейтс",
                "Основатель Microsoft",
                "Android Studio разрабатывалась для Windows"
            )
            insertPerson(
                db, "Стив Джобс",
                "Сооснователь Apple",
                "Apple стимулирует конкуренцию с Android"
            )
            insertPerson(
                db, "Ада Лавлейс",
                "Первая женщина-программист",
                "Первая в мире программа"
            )
            insertPerson(
                db, "Линус Торвальдс",
                "Создатель ОС Linux",
                "Вдохновитель множества свободных ОС"
            )
            insertPerson(
                db, "Энди Рубин",
                "Создатель Android",
                "Android назван в честь его имени"
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS persons")
            onCreate(db)
        }

        private fun insertPerson(db: SQLiteDatabase, name: String, achievement: String, info: String) {
            val values = ContentValues().apply {
                put("name", name)
                put("achievement", achievement)
                put("info", info)
            }
            db.insert("persons", null, values)
        }
    }

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)

            val loadDataButton = Button(context).apply {
                text = "Загрузить данные"
            }

            val listView = ListView(context)

            addView(loadDataButton)
            addView(listView)

            databaseHelper = DatabaseHelper(this@MainActivity)

            loadDataButton.setOnClickListener {
                val people = loadPeopleFromDatabase()
                val adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_list_item_1,
                    people.map { "${it.first}: ${it.second}\n${it.third}" }
                )
                listView.adapter = adapter
            }
        }

        setContentView(layout)
    }

    private fun loadPeopleFromDatabase(): List<Triple<String, String, String>> {
        val people = mutableListOf<Triple<String, String, String>>()
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT name, achievement, info FROM persons", null)

        while (cursor.moveToNext()) {
            val name = cursor.getString(0)
            val achievement = cursor.getString(1)
            val info = cursor.getString(2)
            people.add(Triple(name, achievement, info))
        }
        cursor.close()
        return people
    }
}
