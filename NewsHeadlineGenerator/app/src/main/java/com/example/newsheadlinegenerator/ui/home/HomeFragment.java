package com.example.newsheadlinegenerator.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.newsheadlinegenerator.ApiCaller;
import com.example.newsheadlinegenerator.GetHeadlinesAPI;
import com.example.newsheadlinegenerator.R;
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
    private CharSequence choice = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner spinner = (Spinner) binding.improveChoices;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.choice_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choice = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        EditText article = (EditText) binding.newsArticle;
        Button submitGpt = (Button) binding.submitGpt;
        Button submitT5 = (Button) binding.submitT5;
        TextView result = (TextView) binding.headlineResult;
        ProgressBar progress = (ProgressBar) binding.progress;
        RelativeLayout improve = (RelativeLayout) binding.improveSection;
        Button improveButton = (Button) binding.improveButton;

        improveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Your choice has been recorded for improvement " + choice, Toast.LENGTH_SHORT).show();
            }
        });

        submitGpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String articleText = article.getText().toString();
                if(articleText.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter the article for what you want to generate Headline.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        if(improve.getVisibility() == View.VISIBLE) {
                            improve.setVisibility(View.GONE);
                        }
                        result.setText("");
                        progress.setVisibility(View.VISIBLE);
                        String apiUrl = "https://0167-35-229-157-150.ngrok-free.app";
                        fetchHeadlinesGpt(apiUrl, articleText, result, progress, improve);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        submitT5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String articleText = article.getText().toString();
                if(articleText.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter the article for what you want to generate Headline.", Toast.LENGTH_SHORT).show();
                }
                else {
                    try {
                        if(improve.getVisibility() == View.VISIBLE) {
                            improve.setVisibility(View.GONE);
                        }
                        result.setText("");
                        progress.setVisibility(View.VISIBLE);
                        String apiUrl = "https://0167-35-229-157-150.ngrok-free.app";
                        fetchHeadlinesT5(apiUrl, articleText, result, progress, improve);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchHeadlinesGpt(String baseUrl, String searchQuery, TextView headlineTextView, ProgressBar progressBar, RelativeLayout relativeLayout) {

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
        Call<String[]> call = api.getGPTHeadlines(searchQuery);
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Headlines generated by GPT : \n\n");
                    int i = 1;
                    for (String headline : response.body()) {
                        stringBuilder.append(i).append(". ").append(headline).append("\n\n");
                        i++;
                    }
                    headlineTextView.setText(stringBuilder.toString());
                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    int code = response.code();
                    String message = "Failed to fetch headlines GPT (code: " + code + ")";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                String message = "Network error GPT : " + t.getMessage();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void fetchHeadlinesT5(String baseUrl, String searchQuery, TextView headlineTextView, ProgressBar progressBar, RelativeLayout relativeLayout) {

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
        Call<String[]> call = api.getT5Headlines(searchQuery);
        call.enqueue(new Callback<String[]>() {
            @Override
            public void onResponse(Call<String[]> call, Response<String[]> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Headlines generated by T5 : \n\n");
                    int i = 1;
                    for (String headline : response.body()) {
                        stringBuilder.append(i).append(". ").append(headline).append("\n\n");
                        i++;
                    }
                    headlineTextView.setText(stringBuilder.toString());
                    relativeLayout.setVisibility(View.VISIBLE);
                } else {
                    int code = response.code();
                    String message = "Failed to fetch headlines T5 (code: " + code + ")";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<String[]> call, Throwable t) {
                String message = "Network error T5 : " + t.getMessage();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}