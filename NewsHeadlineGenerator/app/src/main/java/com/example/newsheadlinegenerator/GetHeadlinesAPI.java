package com.example.newsheadlinegenerator;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetHeadlinesAPI {

    @POST("api/gptdata/")
    Call<String[]> getGPTHeadlines(@Body String article);

    @POST("api/t5data/")
    Call<String[]> getT5Headlines(@Body String article);

}
