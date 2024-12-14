package com.example.dynamiclist

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class DynamicListViewModel : ViewModel() {

    // static data ...
    val listOfImages = arrayListOf(
        R.drawable.nature_image,
        R.drawable.nature_image,
        R.drawable.nature_image,
        R.drawable.nature_image,
        R.drawable.nature_image
    )

    val listOfItems = arrayListOf(
        ListDataModel(R.drawable.natures_image, "Item 1", "Description 1"),
        ListDataModel(R.drawable.natures_image, "Item 2", "Description 2"),
        ListDataModel(R.drawable.natures_image, "Item 3", "Description 3"),
        ListDataModel(R.drawable.natures_image, "Item 4", "Description 4"),
        ListDataModel(R.drawable.natures_image, "Item 5", "Description 5"),
        ListDataModel(R.drawable.natures_image, "Item 6", "Description 6"),
        ListDataModel(R.drawable.natures_image, "Item 7", "Description 7"),
        ListDataModel(R.drawable.natures_image, "Item 8", "Description 8"),
        ListDataModel(R.drawable.natures_image, "Item 9", "Description 9"),
        ListDataModel(R.drawable.natures_image, "Item 10", "Description 10"),
    )

    var isBottomSheetVisible = mutableStateOf(false)

    fun filterList(query: String): List<ListDataModel> {
        return listOfItems.filter { it.title.contains(query, ignoreCase = true) }
    }

    fun calculateCharacterFrequency(list: List<String>): List<Pair<Char, Int>> {
        val charCount = mutableMapOf<Char, Int>()
        list.forEach { item ->
            item.forEach { char ->
                charCount[char] = charCount.getOrDefault(char, 0) + 1
            }
        }
        return charCount.toList().sortedByDescending { it.second }
    }
}