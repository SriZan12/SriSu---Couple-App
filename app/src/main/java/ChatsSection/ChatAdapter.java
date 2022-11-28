package ChatsSection;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.srisu.R;
import com.example.srisu.databinding.ReceiveMessageBinding;
import com.example.srisu.databinding.SentMessageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final String TAG = "Message";
    Context thisContext;
    ArrayList<ChatModel> chats;
    int ITEM_SENT = 1;
    int ITEM_RECEIVE = 2;
    String SenderRoom;
    String ReceiverRoom;
    MediaPlayer mediaPlayer = new MediaPlayer();

    public ChatAdapter(Context context, ArrayList<ChatModel> chats, String senderRoom, String receiverRoom) {
        this.thisContext = context;
        this.chats = chats;
        this.SenderRoom = senderRoom;
        this.ReceiverRoom = receiverRoom;

    }

    public ChatAdapter() {
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_message, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_message, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel chatsModel = chats.get(position);
        if (Objects.equals(FirebaseAuth.getInstance().getUid(), chatsModel.getSenderId())) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVE;
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder.getClass() == SentViewHolder.class) {

//            ((SentViewHolder) holder).setIsRecyclable(false);

            ChatModel chatsModel = chats.get(((SentViewHolder) holder).getBindingAdapterPosition());
            String message = chatsModel.getMessage();
            String reactions = chatsModel.getReaction();

                if (message.equals("Photo") || message.equals("Meme")) {
                    ((SentViewHolder) holder).sentMessageBinding.message.setVisibility(View.GONE);
                    ((SentViewHolder) holder).sentMessageBinding.SentImageView.setVisibility(View.VISIBLE);

                    Glide.with(thisContext).load(chatsModel.getImageUrl())
                            .placeholder(R.drawable.placeholder).into(((SentViewHolder) holder).sentMessageBinding.SentImageView);

                } else if (message.equals("voice")) {
                    ((SentViewHolder) holder).sentMessageBinding.message.setVisibility(View.GONE);
                    ((SentViewHolder) holder).sentMessageBinding.play.setVisibility(View.VISIBLE);
                    ((SentViewHolder) holder).sentMessageBinding.playVoice.setVisibility(View.VISIBLE);

                } else {
                    ((SentViewHolder) holder).sentMessageBinding.message.setText(message);
                }


            Log.d(TAG, "onBindViewHolder: " + message);
            Log.d(TAG, "onBindViewHolder: " + reactions);
            switch (reactions) {
                case "like":
                    ((SentViewHolder) holder).sentMessageBinding.senderReactions.setImageResource(R.drawable.like);
                    break;

                case "love":
                    ((SentViewHolder) holder).sentMessageBinding.senderReactions.setImageResource(R.drawable.love);
                    break;

                case "sad":
                    ((SentViewHolder) holder).sentMessageBinding.senderReactions.setImageResource(R.drawable.sad);
                    break;

                case "haha":
                    ((SentViewHolder) holder).sentMessageBinding.senderReactions.setImageResource(R.drawable.laughing);

                case "angry":
                    ((SentViewHolder) holder).sentMessageBinding.senderReactions.setImageResource(R.drawable.angry);
                    break;

                case "wow":
                    ((SentViewHolder) holder).sentMessageBinding.senderReactions.setImageResource(R.drawable.haha);
                    break;
            }


            ((SentViewHolder) holder).sentMessageBinding.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }

                    mediaPlayer = MediaPlayer.create(v.getContext(), Uri.parse(chatsModel.getVoiceMessage()));
                    mediaPlayer.start();

                    ((SentViewHolder) holder).sentMessageBinding.play.setVisibility(View.GONE);
                    ((SentViewHolder) holder).sentMessageBinding.pause.setVisibility(View.VISIBLE);

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            ((SentViewHolder) holder).sentMessageBinding.pause.setVisibility(View.GONE);
                            ((SentViewHolder) holder).sentMessageBinding.play.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });

            ((SentViewHolder) holder).sentMessageBinding.pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SentViewHolder) holder).sentMessageBinding.pause.setVisibility(View.GONE);
                    ((SentViewHolder) holder).sentMessageBinding.play.setVisibility(View.VISIBLE);
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                    }
                }
            });

            ((SentViewHolder) holder).sentMessageBinding.SentImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(thisContext, ShowPhotoActivity.class);
                    intent.putExtra("photo", chatsModel.getImageUrl());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    thisContext.startActivity(intent);
                }
            });

            ((SentViewHolder) holder).sentMessageBinding.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(thisContext);
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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);
                            notifyDataSetChanged();


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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);
                            notifyDataSetChanged();

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);
                            notifyDataSetChanged();

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);
                            notifyDataSetChanged();

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);
                            notifyDataSetChanged();

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);
                            notifyDataSetChanged();

                            dialog.dismiss();
                        }
                    });
                    return false;
                }
            });

            ((SentViewHolder) holder).sentMessageBinding.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(thisContext);
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
                            chatsModel.setMessage("This message was removed.");
                            chatsModel.setReaction("null");
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats")
                                    .child(SenderRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).setValue(chatsModel);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats")
                                    .child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).setValue(chatsModel);
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
                                    .child(chatsModel.getMessageId()).setValue(null);

                            dialog.dismiss();
                        }
                    });
                }
            });


            ((SentViewHolder) holder).sentMessageBinding.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(thisContext);
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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            dialog.dismiss();
                        }
                    });

                    return false;
                }
            });
        } else {
            ChatModel chatsModel = chats.get(((ReceiveViewHolder) holder).getAbsoluteAdapterPosition());
            String message = chatsModel.getMessage();
            String reactions = chatsModel.getReaction();

            if (message.equals("Photo") || message.equals("Meme")) {
                ((ReceiveViewHolder) holder).receiveMessageBinding.imageView.setVisibility(View.VISIBLE);
                ((ReceiveViewHolder) holder).receiveMessageBinding.message.setVisibility(View.GONE);

                Glide.with(thisContext).load(chatsModel.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .into(((ReceiveViewHolder) holder).receiveMessageBinding.imageView);


            } else if (message.equals("voice")) {
                ((ReceiveViewHolder) holder).receiveMessageBinding.play.setVisibility(View.VISIBLE);
                ((ReceiveViewHolder) holder).receiveMessageBinding.playVoice.setVisibility(View.VISIBLE);
                ((ReceiveViewHolder) holder).receiveMessageBinding.message.setVisibility(View.GONE);

            } else {
                ((ReceiveViewHolder) holder).receiveMessageBinding.message.setText(message);
            }


            switch (reactions) {
                case "like":
                    ((ReceiveViewHolder) holder).receiveMessageBinding.receiverReactions.setImageResource(R.drawable.likes);
                    break;

                case "love":
                    ((ReceiveViewHolder) holder).receiveMessageBinding.receiverReactions.setImageResource(R.drawable.love);
                    break;

                case "sad":
                    ((ReceiveViewHolder) holder).receiveMessageBinding.receiverReactions.setImageResource(R.drawable.sad);
                    break;

                case "haha":
                    ((ReceiveViewHolder) holder).receiveMessageBinding.receiverReactions.setImageResource(R.drawable.laughing);
                    break;

                case "angry":
                    ((ReceiveViewHolder) holder).receiveMessageBinding.receiverReactions.setImageResource(R.drawable.angry);
                    break;

                case "wow":
                    ((ReceiveViewHolder) holder).receiveMessageBinding.receiverReactions.setImageResource(R.drawable.haha);
                    break;
            }


            ((ReceiveViewHolder) holder).receiveMessageBinding.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }

                    mediaPlayer = MediaPlayer.create(v.getContext(), Uri.parse(chatsModel.getVoiceMessage()));
                    mediaPlayer.start();

                    ((ReceiveViewHolder) holder).receiveMessageBinding.play.setVisibility(View.GONE);
                    ((ReceiveViewHolder) holder).receiveMessageBinding.pause.setVisibility(View.VISIBLE);

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            ((ReceiveViewHolder) holder).receiveMessageBinding.pause.setVisibility(View.GONE);
                            ((ReceiveViewHolder) holder).receiveMessageBinding.play.setVisibility(View.VISIBLE);
                        }
                    });

                }
            });

            ((ReceiveViewHolder) holder).receiveMessageBinding.pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ReceiveViewHolder) holder).receiveMessageBinding.pause.setVisibility(View.GONE);
                    ((ReceiveViewHolder) holder).receiveMessageBinding.play.setVisibility(View.VISIBLE);
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                }
            });

            ((ReceiveViewHolder) holder).receiveMessageBinding.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(thisContext, ShowPhotoActivity.class);
                    intent.putExtra("photo", chatsModel.getImageUrl());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    thisContext.startActivity(intent);
                }
            });


            ((ReceiveViewHolder) holder).receiveMessageBinding.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(thisContext);
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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            dialog.dismiss();
                        }
                    });

                    return false;
                }
            });

            ((ReceiveViewHolder) holder).receiveMessageBinding.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(thisContext);
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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

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
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats").child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).updateChildren(reactionMap);

                            dialog.dismiss();
                        }
                    });

                    return false;
                }
            });

            ((ReceiveViewHolder) holder).receiveMessageBinding.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(thisContext);
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
                            chatsModel.setMessage("This message was removed.");
                            chatsModel.setReaction("null");
                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats")
                                    .child(SenderRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).setValue(chatsModel);

                            FirebaseDatabase.getInstance().getReference()
                                    .child("Chats")
                                    .child(ReceiverRoom)
                                    .child("messages")
                                    .child(chatsModel.getMessageId()).setValue(chatsModel);
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
                                    .child(chatsModel.getMessageId()).setValue(null);

                            dialog.dismiss();
                        }
                    });
                }
            });
        }


    }


    @Override
    public int getItemCount() {
        return chats.size();
    }


    public static class SentViewHolder extends RecyclerView.ViewHolder {

        SentMessageBinding sentMessageBinding;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessageBinding = SentMessageBinding.bind(itemView);

        }

    }

    public static class ReceiveViewHolder extends RecyclerView.ViewHolder {

        ReceiveMessageBinding receiveMessageBinding;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            receiveMessageBinding = ReceiveMessageBinding.bind(itemView);
        }
    }
}