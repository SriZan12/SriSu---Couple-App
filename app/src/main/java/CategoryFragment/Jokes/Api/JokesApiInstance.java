package CategoryFragment.Jokes.Api;

import CategoryFragment.Jokes.Model.MainModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface JokesApiInstance {

    @GET("Any?")
    Call<MainModel> getAnyJokes(@Query("amount") int amount);

    @GET("Programming?")
    Call<MainModel> getProgrammingJokes(@Query("amount") int amount);

    @GET("Misc?")
    Call<MainModel> getMiscJokes(@Query("amount") int amount);

    @GET("Dark?")
    Call<MainModel> getDarkJokes(@Query("amount") int amount);

    @GET("Christmas?")
    Call<MainModel> getChristmasJokes(@Query("amount") int amount);

    @GET("Pun?")
    Call<MainModel> getPunJokes(@Query("amount") int amount);

    @GET("Spooky?")
    Call<MainModel> getSpookyJokes(@Query("amount") int amount);

    @GET
    Call<MainModel> getJokes(@Url String url);

}
