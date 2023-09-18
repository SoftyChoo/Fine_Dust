package com.example.fine_dust.retrofit

import com.example.fine_dust.data.Dust
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface NetWorkInterface {
    @GET(" ") //시도별 실시간 측정정보 조회 주소
    suspend fun getDust(@QueryMap param: HashMap<String, String>): Dust // String 에 요청값 들어감 HashMap(Key,value) 형태로
}