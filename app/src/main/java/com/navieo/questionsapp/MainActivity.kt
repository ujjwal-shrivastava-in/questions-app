package com.navieo.questionsapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.navieo.questionsapp.ApiClasses.ApiResponse
import com.navieo.questionsapp.ApiClasses.ApiService
import com.navieo.questionsapp.R.drawable.thin_border
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "https://sellncashuat.xtracover.com/"

    private lateinit var questionTextView: TextView
    private lateinit var optionsGrid: GridLayout
    private lateinit var nextButton: Button

    private var currentIndex = 0
    private val responses = mutableListOf<String>()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    val token = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6IlJham5pc2giLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9lbWFpbGFkZHJlc3MiOiJSYWpuaXNoQHh0cmFjb3Zlci5jb20iLCJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9tb2JpbGVwaG9uZSI6IjkzMTEzMzk5MjYiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJYdHJhQ292ZXJSaWRlciIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL2hhc2giOiI5ODExMTcyMjg0ODg5OC43Mzg1MjMyIiwibmJmIjoxNzIyODY3ODAyLCJleHAiOjE3MjgwNTE4MDIsImlzcyI6IlNOQyIsImF1ZCI6IlNOQ1VzZXIifQ.3_Q1Dr4_8X4kOjGjnJG0-qnipC1o7uqQ-Cc98QcJDFk"

    private var questions = mutableListOf<Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        questionTextView = findViewById(R.id.questionTextView)
        optionsGrid = findViewById(R.id.optionsGrid)
        nextButton = findViewById(R.id.nextButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchQuestions()

        nextButton.setOnClickListener {
            saveResponse()
            val question = questions[currentIndex]
            if(nextButton.text == "Next"){
                if(question.isOptional == true){
                    currentIndex++
                    loadQuestion(currentIndex)
                }
                else{
                    if(question.selectedOptions.size > 0){
                        currentIndex++
                        loadQuestion(currentIndex)
                    }else{
                        Toast.makeText(this,"Please select an option",Toast.LENGTH_LONG).show();
                    }
                }
                if(question.selectedOptions.size > 0 && question.isOptional){
                    currentIndex++
                    if (currentIndex < questions.size) {
                        val question = questions[currentIndex]
                        val optional = question.isOptional
                        loadQuestion(currentIndex)
                    }
                }
            }
            else {
                submitQuiz()
                Toast.makeText(this,"Submitted",Toast.LENGTH_LONG).show();
            }

        }
    }

    private fun fetchQuestions() {
        apiService.getQuestions(token).enqueue(object : Callback<ApiResponse>{
            override fun onResponse(p0: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    val fetchedQuestions = apiResponse?.getData()?.questions ?: emptyList()
                    questions = fetchedQuestions.map {
                        Question(
                            s = it.questionName ?: "",
                            options = it.questionOptions,
                            selectType = it.optionSelectTypeName.toString(),
                            isOptional = it.isOptional,
                            selectedOptions = mutableListOf()
                        )
                    }.toMutableList()
                    if (questions.isNotEmpty()) {
                        loadQuestion(0)
                    } else {
                        Log.e("API Response", "No questions found")
                    }

                } else {
                    Log.e("API Response", "Failed: ${response.message()}")
                }
            }

            override fun onFailure(p0: Call<ApiResponse>, t: Throwable) {
                Log.e("API Response", "Error: ${t.message}")
            }

        })
    }

    private fun loadQuestion(index: Int) {
        var question = questions[index]
        questionTextView.text = question.s

        val optionsGrid = findViewById<GridLayout>(R.id.optionsGrid)
        optionsGrid.removeAllViews()

        optionsGrid.columnCount = 2 // Set the number of columns to 2
        optionsGrid.rowCount = 5 // Calculate rows


        val isSingleSelection = question.selectType == "Single"

        for ((i, option) in (question.options ?: emptyList()).withIndex()) {

            val optionObj = QuestionOption(
                option.questionOptionId,
                option.questionId,
                option.questionName,
                option.questionOptionNo,
                option.questionOptionName,
                option.optionImageName,
                option.sortOrder
            )

            val optionView = layoutInflater.inflate(R.layout.option_item, optionsGrid, false)

            val radioButton = optionView.findViewById<RadioButton>(R.id.radioButtonOption)
            val checkBox = optionView.findViewById<CheckBox>(R.id.checkBoxOption)
            val imageView = optionView.findViewById<ImageView>(R.id.imageViewOption)
            val ll = optionView.findViewById<LinearLayout>(R.id.optionLinearLayout)

            radioButton.text = option.questionOptionName
            checkBox.text = option.questionOptionName

            // Load image if URL is provided
            val optionImageUrl = option.optionImageName
            if (optionImageUrl != "") {
                Picasso.get().load(optionImageUrl).into(imageView)
                imageView.visibility = View.VISIBLE
            } else {
                imageView.visibility = View.GONE
            }

            if (isSingleSelection) {
                radioButton.visibility = View.VISIBLE
                checkBox.visibility = View.GONE

                // Handle single selection radio button
                radioButton.setOnClickListener {
                    // Uncheck all radio buttons in the group
                    for (j in 0 until optionsGrid.childCount) {
                        val child = optionsGrid.getChildAt(j)
                        val rb = child.findViewById<RadioButton>(R.id.radioButtonOption)
                        rb.isChecked = false
                    }
                    // Check the selected radio button
                    radioButton.isChecked = true
                    question.updateSelectedOptions(optionObj)
                }
            } else {
                ll.background = resources.getDrawable(R.drawable.thin_border)
                radioButton.visibility = View.GONE
                checkBox.visibility = View.VISIBLE

                // Handle multiple selection check box
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        // Add the selected option to the list
                        question.selectedOptions.add(optionObj)
                    } else {
                        // Remove the unselected option from the list
                        question.selectedOptions.remove(optionObj)
                    }
                }
            }

            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 2f)  // Equal width columns
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 2f)     // Equal height rows
                setMargins(8, 8, 8, 8)
            }
            optionView.layoutParams = params

            optionsGrid.addView(optionView)
        }

        if (index == questions.size - 1) {
            nextButton.text = "Submit"
        } else {
            nextButton.text = "Next"
        }
    }

    private fun saveResponse() {
        val currentQuestion = questions[currentIndex]
        val responseMap = mutableMapOf<String, Any>()
        responseMap["questionId"] = currentQuestion.s
        responseMap["selectedOptions"] = mutableListOf<String>()

        for (i in 0 until optionsGrid.childCount) {
            val optionView = optionsGrid.getChildAt(i)
            val radioButton = optionView.findViewById<RadioButton>(R.id.radioButtonOption)
            val checkBox = optionView.findViewById<CheckBox>(R.id.checkBoxOption)

            if (radioButton.visibility == View.VISIBLE && radioButton.isChecked) {
                (responseMap["selectedOptions"] as MutableList<String>).add(radioButton.text.toString())
            }

            if (checkBox.visibility == View.VISIBLE && checkBox.isChecked) {
                (responseMap["selectedOptions"] as MutableList<String>).add(checkBox.text.toString())
            }
        }

        responses.add(Gson().toJson(responseMap))
    }

    private fun submitQuiz() {
        // Convert responses to JSON and save to local database
        val json = Gson().toJson(responses)
        Log.d("result",json)
        // Save JSON to database (e.g., Room or SharedPreferences)
        val sharedPreferences = getSharedPreferences("QuizApp", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("quiz_responses", json).apply()
    }
}