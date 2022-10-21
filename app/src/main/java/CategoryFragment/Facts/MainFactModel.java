package CategoryFragment.Facts;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MainFactModel {

    @SerializedName("fact")
    List<String> factsList = new ArrayList<>();

    public List<String> getFactsList() {
        return factsList;
    }

    public void setFactsList(List<String> factsList) {
        this.factsList = factsList;
    }
}
