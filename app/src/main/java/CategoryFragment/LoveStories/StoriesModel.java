package CategoryFragment.LoveStories;

import java.util.ArrayList;
import java.util.List;

import CategoryFragment.Entertainment.Music.MusicCommentModel;

public class StoriesModel {
    private String PhotoUrl;
    private String BookName;
    private String Author;
    private String LowerCaseStoriesTitle;
    String PDFUrl;
    List<StoriesCommentModel> CommentList = new ArrayList<>();
    String likeCount;

    public StoriesModel(String photoUrl, String bookName, String author, String lowerCaseStoriesTitle, String PDFUrl, List<StoriesCommentModel> commentList) {
        PhotoUrl = photoUrl;
        BookName = bookName;
        Author = author;
        LowerCaseStoriesTitle = lowerCaseStoriesTitle;
        this.PDFUrl = PDFUrl;
        CommentList = commentList;
    }

    public StoriesModel() {
    }

    public StoriesModel(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getLowerCaseStoriesTitle() {
        return LowerCaseStoriesTitle;
    }

    public void setLowerCaseStoriesTitle(String lowerCaseStoriesTitle) {
        LowerCaseStoriesTitle = lowerCaseStoriesTitle;
    }

    public String getPDFUrl() {
        return PDFUrl;
    }

    public void setPDFUrl(String PDFUrl) {
        this.PDFUrl = PDFUrl;
    }

    public List<StoriesCommentModel> getCommentList() {
        return CommentList;
    }

    public void setCommentList(List<StoriesCommentModel> commentList) {
        CommentList = commentList;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }
}





