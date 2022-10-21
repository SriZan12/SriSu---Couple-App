package CategoryFragment.Memes.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Memes {

    @SerializedName("postLink")
    private String postLink;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
