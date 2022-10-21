package CategoryFragment.Facts;

import com.google.gson.annotations.SerializedName;

public class FactsModel {

    @SerializedName("fact")
    String fact;

    public String getFact() {
        return fact;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }
}
