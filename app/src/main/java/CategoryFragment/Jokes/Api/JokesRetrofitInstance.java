package CategoryFragment.Jokes.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JokesRetrofitInstance {

    private static Retrofit retrofit;
    private static final String BaseUrl = "https://v2.jokeapi.dev/joke/";

    public static Retrofit getRetrofit(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
