package phm.example.project_chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private Context context;
    private List<Msg> Msg;
    //private boolean status;

    public MsgAdapter(Context context, List<Msg> Msg){
        this.context = context;
        this.Msg = Msg;
        //this.status = status;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout msgImg;
        public TextView username;
        public TextView title;
        public TextView timestamp;
        public TextView mainText;
        //private ImageView status_on;
        //private ImageView status_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msgImg=itemView.findViewById(R.id.backImgset);
            username = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.title);
            timestamp = itemView.findViewById(R.id.date);
            mainText = itemView.findViewById(R.id.maintext);
            //status_on = itemView.findViewById(R.id.status_on);
            //status_off = itemView.findViewById(R.id.status_off);

        }
    }


    @NonNull
    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.msg_item, parent, false);

        return new MsgAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgAdapter.ViewHolder holder, int position) {

        final Msg msg = Msg.get(position);
        final MsgAdapter.ViewHolder h=holder;
        holder.username.setText(msg.getDisplayname());
        holder.title.setText(msg.getTitle());
        holder.mainText.setText(msg.getMainText());
        holder.timestamp.setText(msg.getTimestamp());
        holder.msgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    //TODO 수정?

                }
            }//리스트 누르면 팝업창 생성
        });
        if(msg.getImageURL().equals("default")){
            holder.msgImg.setBackgroundResource(R.drawable.ic_launcher_foreground);
        }else{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference("Msg/"+msg.getImageURL());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context).load(uri.toString()).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                h.msgImg.setBackground(resource);
                            }
                        }
                    });

            }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

/*
        // 활동 상태
        if(status){
            if(msg.getStatus().equals("online")){
                holder.status_on.setVisibility(View.VISIBLE);
                holder.status_off.setVisibility(View.GONE);
            }
            else{
                holder.status_on.setVisibility(View.GONE);
                holder.status_off.setVisibility(View.VISIBLE);
            }
        }else{
            holder.status_on.setVisibility(View.GONE);
            holder.status_off.setVisibility(View.GONE);
        }*/

    }

    @Override
    public int getItemCount() {
        return Msg.size();
    }
}
