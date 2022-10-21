package ChatsSection.ChatModel;

public class ChatModel {

    private String MessageId,message,SenderId,imageUrl,VoiceMessage;
    private long TimeStamp;
    private String reaction = "null";

    public ChatModel() {
    }

    public ChatModel(String message, String senderId, long timeStamp) {
        this.message = message;
        SenderId = senderId;
        TimeStamp = timeStamp;
    }

    public  ChatModel(String messageId, String message, String senderId, long timeStamp, String reaction) {
        MessageId = messageId;
        this.message = message;
        SenderId = senderId;
        TimeStamp = timeStamp;
        this.reaction = reaction;
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

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
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
}
