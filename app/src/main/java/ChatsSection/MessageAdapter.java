package ChatsSection;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.srisu.R;
import com.example.srisu.databinding.SentMessageBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class MessageAdapter extends FirebaseRecyclerAdapter<ChatModel, MessageAdapter.MessageViewHolder> {

    String SenderRoom;
    Context context;
    String ReceiverUid;
    FirebaseAuth firebaseAuth;
    String ReceiverRoom;
    MediaPlayer mediaPlayer = new MediaPlayer();


    public MessageAdapter(@NonNull FirebaseRecyclerOptions<ChatModel> options, Context context, String senderRoom, String receiverUid,String receiverRoom) {
        super(options);
        this.context = context;
        this.SenderRoom = senderRoom;
        this.ReceiverUid = receiverUid;
        this.ReceiverRoom = receiverRoom;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ChatModel model) {

        firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getUid();

        if (Objects.equals(uid, model.getSenderId())) { // Setting up the name for sender and receiver
            holder.sentMessageBinding.senderName.setText("Me"); // if the uid matches it will set Me
        } else {
            holder.sentMessageBinding.senderName.setText(model.getNickname()); // else it will set the partner's name
        }

        FirebaseDatabase.getInstance().getReference()
                .child("Users").child(model.getSenderId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String ImageUrl = snapshot.child("profileImage").getValue(String.class);

                        Glide.with(context)
                                .load(ImageUrl)
                                .placeholder(R.drawable.placeholder)
                                .into(holder.sentMessageBinding.senderProfileChat); // This will set the profile of both the users.
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.sentMessageBinding.sentTime.setText(model.getTimeStamp());

//        The below codes will check the message status, if it is photo it will open the photo, if it's text it will open the text and so on..
        if(model.getMessage().equals("Photo") || model.getMessage().equals("Meme")){

            if(model.getImageUrl() != null) {

                holder.sentMessageBinding.SentImageView.setVisibility(View.VISIBLE);

                Glide.with(context).load(model.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.sentMessageBinding.SentImageView);
            }

        }else if(model.getMessage().equals("voice")){

            if(model.getVoiceMessage() != null) {

                holder.sentMessageBinding.play.setVisibility(View.VISIBLE);
                holder.sentMessageBinding.playVoice.setVisibility(View.VISIBLE);
            }

        }else{

            holder.sentMessageBinding.message.setVisibility(View.VISIBLE);
            holder.sentMessageBinding.message.setText(model.getMessage());
        }


        switch (model.getReaction()) { // getting the reaction from the user.
            case "like":
                 holder.sentMessageBinding.senderReactions.setImageResource(R.drawable.likes);
                break;

            case "love":
                 holder.sentMessageBinding.senderReactions.setImageResource(R.drawable.like);
                break;

            case "sad":
                 holder.sentMessageBinding.senderReactions.setImageResource(R.drawable.sad);
                break;

            case "haha":
                 holder.sentMessageBinding.senderReactions.setImageResource(R.drawable.laughing);
                 break;

            case "angry":
                 holder.sentMessageBinding.senderReactions.setImageResource(R.drawable.angry);
                break;

            case "wow":
                 holder.sentMessageBinding.senderReactions.setImageResource(R.drawable.haha);
                break;
        }


         holder.sentMessageBinding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }

                mediaPlayer = MediaPlayer.create(v.getContext(), Uri.parse(model.getVoiceMessage()));
                mediaPlayer.start(); // playing the media player.

                 holder.sentMessageBinding.play.setVisibility(View.GONE);
                 holder.sentMessageBinding.pause.setVisibility(View.VISIBLE);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                         holder.sentMessageBinding.pause.setVisibility(View.GONE);
                         holder.sentMessageBinding.play.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

         holder.sentMessageBinding.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 holder.sentMessageBinding.pause.setVisibility(View.GONE);
                 holder.sentMessageBinding.play.setVisibility(View.VISIBLE);
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause(); // it will pause the mediaPlayer
                    }
                }
            }
        });

         holder.sentMessageBinding.SentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowPhotoActivity.class);
                intent.putExtra("photo", model.getImageUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

         holder.sentMessageBinding.SentImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.reactions_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageView like = dialog.findViewById(R.id.like);
                ImageView love = dialog.findViewById(R.id.love);
                ImageView wow = dialog.findViewById(R.id.wow);
                ImageView haha = dialog.findViewById(R.id.haha);
                ImageView sad = dialog.findViewById(R.id.sad);
                ImageView angry = dialog.findViewById(R.id.angry);

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "like");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);


                        dialog.dismiss();

                    }


                });

                love.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "love");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                sad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "sad");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                wow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "wow");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                haha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "haha");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                angry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "angry");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });
                return false;
            }
        });

         holder.sentMessageBinding.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.getWindow().setContentView(R.layout.delete_options);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                View view1 = dialog.findViewById(R.id.view1);
                View view2 = dialog.findViewById(R.id.view2);
                TextView Everyone = dialog.findViewById(R.id.del_everyone);
                TextView cancel = dialog.findViewById(R.id.cancel);
                TextView onlyMe = dialog.findViewById(R.id.del_only_me);

                view1.setBackgroundResource(R.color.view_line);
                view2.setBackgroundResource(R.color.view_line);

                Everyone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.setMessage("This message was removed.");
                        model.setReaction("null");
                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats")
                                .child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).setValue(model);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats")
                                .child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).setValue(model);
                        dialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                onlyMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats")
                                .child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).setValue(null);

                        dialog.dismiss();
                    }
                });
            }
        });


         holder.sentMessageBinding.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { // setting up the reaction status on both receiver and sender's node.
                final Dialog dialog = new Dialog(context);
                dialog.getWindow().setContentView(R.layout.reactions_layout);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageView like = dialog.findViewById(R.id.like);
                ImageView love = dialog.findViewById(R.id.love);
                ImageView wow = dialog.findViewById(R.id.wow);
                ImageView haha = dialog.findViewById(R.id.haha);
                ImageView sad = dialog.findViewById(R.id.sad);
                ImageView angry = dialog.findViewById(R.id.angry);

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "like");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                love.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "love");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                sad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "sad");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                wow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "wow");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                haha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "haha");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                angry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> reactionMap = new HashMap<>();
                        reactionMap.put("reaction", "angry");

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(SenderRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        FirebaseDatabase.getInstance().getReference()
                                .child("Chats").child(ReceiverRoom)
                                .child("messages")
                                .child(Objects.requireNonNull(getRef(position).getKey())).updateChildren(reactionMap);

                        dialog.dismiss();
                    }
                });

                return false;
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SentMessageBinding sentMessageBinding = SentMessageBinding.inflate(layoutInflater, parent, false);
        return new MessageViewHolder(sentMessageBinding);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        SentMessageBinding sentMessageBinding;

        public MessageViewHolder(SentMessageBinding sentMessageBinding) {
            super(sentMessageBinding.getRoot());
            this.sentMessageBinding = sentMessageBinding;

        }
    }
}
