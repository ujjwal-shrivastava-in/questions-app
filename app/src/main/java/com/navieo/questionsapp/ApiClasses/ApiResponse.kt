package com.navieo.questionsapp.ApiClasses

import com.google.gson.annotations.SerializedName

class ApiResponse {
    @SerializedName("data")
    private val data: Data? = null

    fun getData(): Data? {
        return data
    }

    class Data {
        @SerializedName("questions")
        val questions: List<Question>? = null

        class Question {
            @SerializedName("questionId")
            val questionId: Int = 0

            @SerializedName("categoryId")
            val categoryId: Int = 0

            @SerializedName("categoryName")
            val categoryName: String? = null

            @SerializedName("questionNo")
            val questionNo: String? = null

            @SerializedName("questionName")
            val questionName: String? = null

            @SerializedName("sortOrder")
            val sortOrder: Int = 0

            @SerializedName("optionSelectType")
            val optionSelectType: Int = 0

            @SerializedName("optionSelectTypeName")
            val optionSelectTypeName: String? = null

            @SerializedName("isOptional")
            val isOptional: Boolean = false

            @SerializedName("isMainQuestion")
            val isMainQuestion: Boolean = false

            @SerializedName("isFrontendSingleSelection")
            val isFrontendSingleSelection: Boolean = false

            @SerializedName("isIssueQuestion")
            val isIssueQuestion: Boolean = false

            @SerializedName("frontendShowProcessNo")
            val frontendShowProcessNo: Int = 0

            @SerializedName("priceCalculationType")
            val priceCalculationType: Int = 0

            @SerializedName("priceCalculationTypeName")
            val priceCalculationTypeName: String? = null

            @SerializedName("questionOptions")
            val questionOptions: List<QuestionOption>? = null

            class QuestionOption {
                @SerializedName("questionOptionId")
                val questionOptionId: Int = 0

                @SerializedName("questionId")
                val questionId: Int = 0

                @SerializedName("questionName")
                val questionName: String? = null

                @SerializedName("questionOptionNo")
                val questionOptionNo: String? = null

                @SerializedName("questionOptionName")
                val questionOptionName: String? = null

                @SerializedName("sortOrder")
                val sortOrder: Int = 0

                @SerializedName("optionImageName")
                val optionImageName: String? = null

                @SerializedName("isCorrect")
                val isCorrect: Boolean = false
            }
        }
    }
}