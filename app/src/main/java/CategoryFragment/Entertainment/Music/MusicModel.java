package CategoryFragment.Entertainment.Music;

import java.util.ArrayList;
import java.util.List;

import CategoryFragment.Entertainment.Movie.MovieCommentModel;

public class MusicModel {

    String MusicTitle;
    String Uid,Singer;
    List<MusicCommentModel> CommentList = new ArrayList<>();
    String likeCount;

    public MusicModel(String musicTitle, String uid, String singer, List<MusicCommentModel> commentList, String likeCount) {
        MusicTitle = musicTitle;
        Uid = uid;
        Singer = singer;
        CommentList = commentList;
        this.likeCount = likeCount;
    }

    public MusicModel() {
    }

    public MusicModel(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getMusicTitle() {
        return MusicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        MusicTitle = musicTitle;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getSinger() {
        return Singer;
    }

    public void setSinger(String singer) {
        Singer = singer;
    }

    public List<MusicCommentModel> getCommentList() {
        return CommentList;
    }

    public void setCommentList(List<MusicCommentModel> commentList) {
        CommentList = commentList;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }
}
