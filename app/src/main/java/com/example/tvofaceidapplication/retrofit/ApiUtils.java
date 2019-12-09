package com.example.tvofaceidapplication.retrofit;

public class ApiUtils {
    private ApiUtils() {}

    public static final String BASE_URL = "https://262cf100.ngrok.io/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
