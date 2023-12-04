package com.example.koocbook

import java.util.Objects

class Item(val id: Int, val image: String, val title: String, val desc: String, val recipe: String, val ingredients: List<Any>, val cookTimeInMinutes: Int, val authorId: Int){
}