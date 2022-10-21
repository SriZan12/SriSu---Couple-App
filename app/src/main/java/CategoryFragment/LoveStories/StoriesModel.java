package CategoryFragment.LoveStories;

public class StoriesModel {
    private String PhotoUrl;
    private String BookName;
    private String Author;
    private String  LowerCaseStoriesTitle;
    String PDFUrl;

    public StoriesModel() {
    }

    public StoriesModel(String photoUrl, String bookName, String author, String lowerCaseStoriesTitle,String pdfUrl) {
        PhotoUrl = photoUrl;
        BookName = bookName;
        Author = author;
        LowerCaseStoriesTitle = lowerCaseStoriesTitle;
        PDFUrl = pdfUrl;
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
}
