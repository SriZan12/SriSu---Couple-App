package CategoryFragment.Facts;

import CategoryFragment.Jokes.Model.MainModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface FactsApiInstance {


    @GET("facts?")
    @Headers("Authorization:{7wBw062CJFYJEND2ZkE0vA==brI2eJZMFtXs8VqS}")
    Call<MainFactModel> allFacts(@Query("limit") int limit);

    @GET
    Call<MainFactModel> getFacts(@Url String url,@Query("X-Api-Key") String apiKey);


}
