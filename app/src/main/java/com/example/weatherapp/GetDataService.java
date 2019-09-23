package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

   /* @GET("Vy2abloQD")
    Call<List<Pokemon>> getAllPokemon();
*/
    @GET("3534")
    Call<WeatherMontreal> getWeather();
}
