package CategoryFragment.Movie;

public class MovieModel {

    String MovieTitle;
    String Uid,Category;

    public MovieModel(String movieTitle,String UID,String category) {
        MovieTitle = movieTitle;
        this.Uid = UID;
        this.Category = category;
    }

    public MovieModel() {
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getMovieTitle() {
        return MovieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        MovieTitle = movieTitle;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
