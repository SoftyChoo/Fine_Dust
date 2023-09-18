package com.example.fine_dust

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fine_dust.data.DustItem
import com.example.fine_dust.databinding.ActivityMainBinding
import com.example.fine_dust.retrofit.NetWorkClient
import com.skydoves.powerspinner.IconSpinnerAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    var items = mutableListOf<DustItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.spinnerViewSido.setOnSpinnerItemSelectedListener<String> { _, _, _, text -> // 도시 선택
            communicateNetWork(setUpDustParameter(text))
        }

        binding.spinnerViewGoo.setOnSpinnerItemSelectedListener<String> { _, _, _, text -> // 지역 선택

            Log.d("miseya", "selectedItem: spinnerViewGoo selected >  $text")
            var selectedItem = items.filter { f -> f.stationName == text } // 시도, 지역과 일치하는 item을 가져옴
            Log.d("miseya", "selectedItem: sidoName > " + selectedItem[0].sidoName) // 시도 이름과
            Log.d("miseya", "selectedItem: pm10Value > " + selectedItem[0].pm10Value) // 미세먼지 수치

            binding.tvCityname.text = selectedItem[0].sidoName + "  " + selectedItem[0].stationName // 받아온 시도, 지역을 나타냄
            binding.tvDate.text = selectedItem[0].dataTime // 측정 시간
            binding.tvP10value.text = selectedItem[0].pm10Value + " ㎍/㎥" // 미세먼지 수치

            when (getGrade(selectedItem[0].pm10Value)) { // 미세먼지 등급에 따라 배경색, 이모티콘을 변경해줌
                1 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#9ED2EC"))
                    binding.ivFace.setImageResource(R.drawable.mise1)
                    binding.tvP10grade.text = "좋음"
                }

                2 -> { //D6A478
                    binding.mainBg.setBackgroundColor(Color.parseColor("#EDC766"))
                    binding.ivFace.setImageResource(R.drawable.mise2)
                    binding.tvP10grade.text = "보통"
                }

                3 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#DF7766"))
                    binding.ivFace.setImageResource(R.drawable.mise3)
                    binding.tvP10grade.text = "나쁨"
                }

                4 -> {
                    binding.mainBg.setBackgroundColor(Color.parseColor("#BB3320"))
                    binding.ivFace.setImageResource(R.drawable.mise4)
                    binding.tvP10grade.text = "매우나쁨"
                }
            }
        }
    }

    private fun communicateNetWork(param: HashMap<String, String>) = lifecycleScope.launch() {

        // 코루틴의 별도의 쓰레드로 돌게 만듬

        val responseData = NetWorkClient.dustNetWork.getDust(param) // responsData에 들어옴
        Log.d("Parsing Dust ::", responseData.toString())

        val adapter = IconSpinnerAdapter(binding.spinnerViewGoo)
        items = responseData.response.dustBody.dustItem!! // 위치를 찾아서 모든 아이템을 가져와 처음에 선언 한 items List에 넣어줌

        val goo = ArrayList<String>()
        items.forEach { // 지역명들을 찾아 goo에 넣어줌
            Log.d("add Item :", it.stationName)
            goo.add(it.stationName)
        }

        // 코루틴 별도의 쓰레드 이므로 Ui를 건들게 해주려면 아래와 같이 코딩해주어야 함
        runOnUiThread { // 지역 스피너에 추가시켜줌
            binding.spinnerViewGoo.setItems(goo)
        }

    }

    private fun setUpDustParameter(sido: String): HashMap<String, String> { // 요청 파라미터 생성
        val authKey = "pLRSGS4cjeZEi5SsFep48mx9anSTWUB8lf3l6V71qtIiyV8dA46BTyvodygKwD3HmtJ0Jue0nDOfqtOxFEZ3hg=="

        return hashMapOf(
            "serviceKey" to authKey,
            "returnType" to "json",
            "numOfRows" to "100",
            "pageNo" to "1",
            "sidoName" to sido, // 시도 선택에서 넣은 값 들어옴
            "ver" to "1.0"
        )
    }

    fun getGrade(value: String): Int { // 미세먼지 등급을 나눠줌
        val mValue = value.toInt()
        var grade = 1
        grade = if (mValue >= 0 && mValue <= 30) {
            1
        } else if (mValue >= 31 && mValue <= 80) {
            2
        } else if (mValue >= 81 && mValue <= 100) {
            3
        } else 4
        return grade
    }
}