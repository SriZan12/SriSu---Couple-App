package CategoryFragment.Entertainment.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieModel {

    String MovieTitle;
    String Uid,Category,movieSource;
    List<MovieCommentModel> CommentList = new ArrayList<>();
    String likeCount;

    public MovieModel(String movieTitle, String UID, String category, List<MovieCommentModel> commentModelList, String LikeCount,String movieSource) {
        MovieTitle = movieTitle;
        this.Uid = UID;
        this.Category = category;
        this.CommentList = commentModelList;
        this.likeCount = LikeCount;
        this.movieSource = movieSource;
    }

    public MovieModel(String LikeCount){
        this.likeCount = LikeCount;
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

    public List<MovieCommentModel> getCommentList() {
        return CommentList;
    }

    public void setCommentList(List<MovieCommentModel> commentList) {
        CommentList = commentList;
    }


    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getMovieSource() {
        return movieSource;
    }

    public void setMovieSource(String movieSource) {
        this.movieSource = movieSource;
    }
}
