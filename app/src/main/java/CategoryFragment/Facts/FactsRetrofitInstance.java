package CategoryFragment.Facts;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FactsRetrofitInstance {

    private static Retrofit retrofit;

    private static final String BaseUrl = "https://api.api-ninjas.com/v1/";

    public static Retrofit getFactsRetrofit(){
        if(retrofit == null){

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BaseUrl)
                    .build();
        }

        return retrofit;
    }

}
