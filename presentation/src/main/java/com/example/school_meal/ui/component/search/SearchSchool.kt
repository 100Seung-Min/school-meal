package com.example.school_meal.ui.component.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.school_meal.data.DTO.SchoolInfoData
import com.example.school_meal.data.DTO.infoRow
import com.example.school_meal.databinding.ActivitySearchSchoolBinding
import com.example.school_meal.data.retrofit.SchoolAPIClient
import com.example.school_meal.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchSchool : AppCompatActivity() {

    val binding by lazy { ActivitySearchSchoolBinding.inflate(layoutInflater) }
    val itemlist: ArrayList<infoRow> = ArrayList()
    lateinit var adapter: SearchSchoolAdapter
    var selectItem: infoRow = infoRow("", "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var pref = getSharedPreferences("MY_SCHOOL", MODE_PRIVATE)
        var edit = pref.edit()
        var mySchoolName = pref.getString("mySchoolName", null)
        var mySchoolCode = pref.getString("mySchoolCode", null)
        var mySchoolNum = pref.getString("mySchoolNum", null)
        var mySchoolClass = pref.getString("mySchoolClass", null)
        var mySchoolGrade = pref.getString("mySchoolGrade", null)
        var mySchoolLevel = pref.getString("mySchoolLevel", null)

        if(!mySchoolName.isNullOrEmpty()){
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("mySchoolName", mySchoolName)
            intent.putExtra("mySchoolCode", mySchoolCode)
            intent.putExtra("mySchoolNum", mySchoolNum)
            intent.putExtra("mySchoolClass", mySchoolClass)
            intent.putExtra("mySchoolGrade", mySchoolGrade)
            intent.putExtra("mySchoolLevel", mySchoolLevel)
            startActivity(intent)
            finish()
        }

        adapter = SearchSchoolAdapter(itemlist, this){
            selectItem = it
            binding.setSchoolBtn.visibility = View.VISIBLE
        }

        binding.setSchoolBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            mySchoolName = selectItem.SCHUL_NM
            mySchoolCode = selectItem.ATPT_OFCDC_SC_CODE
            mySchoolNum = selectItem.SD_SCHUL_CODE
            mySchoolLevel = selectItem.SCHUL_KND_SC_NM
            mySchoolClass = binding.classEdit.text.toString()
            mySchoolGrade = binding.gradeEdit.text.toString()
            edit.putString("mySchoolName", mySchoolName)
            edit.putString("mySchoolCode", mySchoolCode)
            edit.putString("mySchoolNum", mySchoolNum)
            edit.putString("mySchoolClass", mySchoolClass)
            edit.putString("mySchoolGrade", mySchoolGrade)
            edit.putString("mySchoolLevel", mySchoolLevel)
            edit.commit()
            intent.putExtra("mySchoolName", mySchoolName)
            intent.putExtra("mySchoolCode", mySchoolCode)
            intent.putExtra("mySchoolNum", mySchoolNum)
            intent.putExtra("mySchoolClass", mySchoolClass)
            intent.putExtra("mySchoolGrade", mySchoolGrade)
            intent.putExtra("mySchoolLevel", mySchoolLevel)
            startActivity(intent)
            finish()
        }

        binding.resultSchoolRecyclerview.adapter = adapter
        binding.resultSchoolRecyclerview.layoutManager = LinearLayoutManager(this)
        PagerSnapHelper().attachToRecyclerView(binding.resultSchoolRecyclerview)

        binding.searchSchoolBtn.setOnClickListener {
            val schoolName = binding.searchSchoolEdit.text.toString()
            if(binding.classEdit.text.isEmpty() && binding.gradeEdit.text.isEmpty() && schoolName.isEmpty()){
                makeToast("?????? ??????????????????")
            }
            else if(schoolName.isEmpty()){
                makeToast("????????? ??????????????????")
            }
            else if(binding.gradeEdit.text.isEmpty()){
                makeToast("????????? ??????????????????")
            }
            else if(binding.classEdit.text.isEmpty()){
                makeToast("?????? ??????????????????")
            }
            else{
                SchoolAPIClient.api.getSchoolInfo(schoolName = schoolName).enqueue(object : Callback<SchoolInfoData>{
                    override fun onResponse(
                        call: Call<SchoolInfoData>,
                        response: Response<SchoolInfoData>
                    ) {
                        itemlist.clear()
                        if(response.body()!!.schoolInfo != null){
                            for(infoData in response.body()!!.schoolInfo){
                                if(infoData.row != null){
                                    for(data in infoData.row){
                                        itemlist.add(data)
                                    }
                                }
                            }
                        }
                        else{
                            makeToast("??????????????? ????????????")
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<SchoolInfoData>, t: Throwable) {
                        println("?????? ${t}")
                    }

                })
            }
        }
    }

    fun makeToast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}