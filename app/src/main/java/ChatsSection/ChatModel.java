package ChatsSection;

public class ChatModel {

    private String MessageId,message,SenderId,imageUrl,VoiceMessage,nickname;
    private String TimeStamp;
    private String reaction;

    public ChatModel() {
    }

    public ChatModel(String message, String senderId, String timeStamp,String reaction,String nickname) {
        this.message = message;
        SenderId = senderId;
        TimeStamp = timeStamp;
        this.reaction = reaction;
        this.nickname = nickname;
    }


    public String getMessageId() {
        return MessageId;
    }

    public void setMessageId(String messageId) {
        MessageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVoiceMessage() {
        return VoiceMessage;
    }

    public void setVoiceMessage(String voiceMessage) {
        VoiceMessage = voiceMessage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
