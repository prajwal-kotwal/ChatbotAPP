package com.example.medicalchatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class MainActivity extends AppCompatActivity {

    private EditText etQuery;
    private Button btnSend;
    private TextView tvResponse;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etQuery = findViewById(R.id.etQuery);
        btnSend = findViewById(R.id.btnSend);
        tvResponse = findViewById(R.id.tvResponse);

        // Setup Retrofit
        Gson gson = new GsonBuilder().setLenient().create();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                Log.d("Retrofit", message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.9:5000")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();

        apiService = retrofit.create(ApiService.class);

        btnSend.setOnClickListener(v -> {
            String queryText = etQuery.getText().toString();
            if (!queryText.isEmpty()) {
                sendQueryToServer(queryText);
            } else {
                Toast.makeText(this, "Please enter a query", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendQueryToServer(String query) {
        QueryRequest queryRequest = new QueryRequest(query);
        apiService.sendQuery(queryRequest).enqueue(new Callback<QueryResponse>() {
            @Override
            public void onResponse(@NonNull Call<QueryResponse> call, @NonNull Response<QueryResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    tvResponse.setText(response.body().getResponse());
                } else {
                    Toast.makeText(MainActivity.this, "Failed to get response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<QueryResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

