package CategoryFragment.Jokes.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JokesModel {

    @SerializedName("category")
    @Expose
    private String Category;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("setup")
    @Expose
    private String setUp;

    @SerializedName("delivery")
    @Expose
    private String delivery;

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("joke")
    @Expose
    private String Jokes;

    @SerializedName("safe")
    @Expose
    private boolean safe;

    @SerializedName("lang")
    @Expose
    private String language;

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSetUp() {
        return setUp;
    }

    public void setSetUp(String setUp) {
        this.setUp = setUp;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public String getLanguage() {
        return language;
    }

    public String getJokes() {
        return Jokes;
    }

    public void setJokes(String jokes) {
        Jokes = jokes;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
