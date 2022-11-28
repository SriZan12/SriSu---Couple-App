package SignInSection;

public class UserModel {

    private String fullName,userName,UserId,UserNumber,ProfileImage;
    private String isEngaged,Relationship;

    public UserModel(String fullName, String userName, String userId, String userNumber, String profileImage,String IsEngaged,String relationship) {
        this.fullName = fullName;
        this.userName = userName;
        this.UserId = userId;
        this.UserNumber = userNumber;
        this.ProfileImage = profileImage;
        this.isEngaged = IsEngaged;
        this.Relationship = relationship;
    }


    public UserModel() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getIsEngaged() {
        return isEngaged;
    }

    public void setIsEngaged(String isEngaged) {
        this.isEngaged = isEngaged;
    }

    public String getRelationship() {
        return Relationship;
    }

    public void setRelationship(String relationship) {
        Relationship = relationship;
    }
}
