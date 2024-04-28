package com.example.newsheadlinegenerator;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GetHeadlinesAPI {

    @POST("api/data/")
    Call<String[]> getHeadlines(@Body String article);

}
