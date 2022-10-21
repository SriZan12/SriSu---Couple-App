package CategoryFragment.Jokes.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainModel {

    @SerializedName("error")
    private String error;

    @SerializedName("amount")
    private String amount;

    @SerializedName("jokes")
    private List<JokesModel> jokesModelList;

    public String getError() {
        return error;
    }

    public String getAmount() {
        return amount;
    }

    public List<JokesModel> getJokesModelList() {
        return jokesModelList;
    }
}
