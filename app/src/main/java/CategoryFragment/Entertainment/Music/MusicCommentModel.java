package CategoryFragment.Entertainment.Music;

public class MusicCommentModel {

    private String Comment;
    private String Commenter_Uid;

    public MusicCommentModel(String comment, String commenter_Uid) {
        Comment = comment;
        Commenter_Uid = commenter_Uid;
    }

    public MusicCommentModel() {
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getCommenter_Uid() {
        return Commenter_Uid;
    }

    public void setCommenter_Uid(String commenter_Uid) {
        Commenter_Uid = commenter_Uid;
    }

}
