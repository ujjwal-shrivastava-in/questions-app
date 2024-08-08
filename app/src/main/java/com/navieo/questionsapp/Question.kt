package com.navieo.questionsapp

import com.navieo.questionsapp.ApiClasses.ApiResponse

class Question(
    val s: String,
    val options: List<ApiResponse.Data.Question.QuestionOption>?,
    val selectType: String = "Multiple",
    val isOptional : Boolean,
    var selectedOptions : MutableList<QuestionOption> = mutableListOf()
){
    fun updateSelectedOptions(newOptions: QuestionOption){
        selectedOptions.clear();
        selectedOptions.addAll(listOf(newOptions))
    }
}

data class QuestionOption(
    val questionOptionId: Int,
    val questionId: Int,
    val questionName: String?,
    val questionOptionNo: String?,
    val questionOptionName: String?,
    val optionImageName: String?,
    val sortOrder: Int
)
