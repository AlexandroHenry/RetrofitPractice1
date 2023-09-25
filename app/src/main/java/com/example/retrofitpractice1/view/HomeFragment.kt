package com.example.retrofitpractice1.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.example.retrofitpractice1.api.ApiService
import com.example.retrofitpractice1.data.Ticker
import com.example.retrofitpractice1.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment(), OnClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        with(binding) {
            searchBtn.setOnClickListener(this@HomeFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.searchBtn -> {

                // 초기화
                binding.resultText.text = ""
                
                // api 호출
                apiRequest()
            }
        }


    }

    /**
     * HTTP api 호출
     */
    fun apiRequest() {
        // 1. retrofit 객체 생성
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.bithumb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 2. service 객체 생성
        val apiService: ApiService = retrofit.create(ApiService::class.java)

        // 3. Call 객체 생성

        val coinNm = binding.coinEdit.text.toString().uppercase() // 입력값, 대문자로

        val tickerCall = apiService.getCoinTicker(coinNm, "KRW")

        // 4. 네트워크 통신

        tickerCall.enqueue(object : Callback<Ticker> {
            override fun onResponse(call: Call<Ticker>, response: Response<Ticker>) {
                // 호출 데이터
                val tickerInfo = response.body()

                binding.resultText.append("status: ${tickerInfo?.status}\n")
                binding.resultText.append("closing_price: ${tickerInfo?.data?.closing_price}\n")
                binding.resultText.append("opening_price: ${tickerInfo?.data?.opening_price}\n")
                binding.resultText.append("max_price: ${tickerInfo?.data?.max_price}\n")
                binding.resultText.append("min_price: ${tickerInfo?.data?.min_price}\n")
            }

            override fun onFailure(call: Call<Ticker>, t: Throwable) {
                // 오류 시 발생
                call.cancel()
            }

        })

    }
}