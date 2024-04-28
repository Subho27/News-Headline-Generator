package com.example.newsheadlinegenerator.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.newsheadlinegenerator.ApiCaller;
import com.example.newsheadlinegenerator.GetHeadlinesAPI;
import com.example.newsheadlinegenerator.databinding.FragmentHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText article = (EditText) binding.newsArticle;
        Button submit = (Button) binding.submitButton;
        TextView result = (TextView) binding.headlineResult;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String articleText = article.getText().toString();
                if(articleText.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter the article for what you want to generate Headline.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        System.out.println("Call");
                        String apiUrl = "https://ede4-34-171-144-64.ngrok-free.app";
                        fetchHeadlines(apiUrl, articleText, result);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchHeadlines(String baseUrl, String searchQuery, TextView headlineTextView) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        GetHeadlinesAPI api = retrofit.create(GetHeadlinesAPI.class);
        Call<String[]> call = api.getHeadlines(searchQuery);
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if (response.isSuccessful()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String headline : response.body()) {
                        stringBuilder.append(headline).append("\n");
                    }
                    headlineTextView.setText(stringBuilder.toString());
                } else {
                    int code = response.code();
                    String message = "Failed to fetch headlines (code: " + code + ")";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                String message = "Network error: " + t.getMessage();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}