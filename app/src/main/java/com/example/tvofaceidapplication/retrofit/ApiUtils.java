package com.example.tvofaceidapplication.retrofit;

public class ApiUtils {
    private ApiUtils() {}

    public static final String BASE_URL = "https://705cd208.ngrok.io/check-image/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
