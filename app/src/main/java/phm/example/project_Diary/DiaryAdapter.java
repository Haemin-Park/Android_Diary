package phm.example.project_Diary;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private Context context;
    private List<Diary> Diary;
    //private boolean status;

    public DiaryAdapter(Context context, List<Diary> Diary){
        this.context = context;
        this.Diary = Diary;
        //this.status = status;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout msgImg;
        public TextView username;
        public TextView title;
        public TextView timestamp;
        public TextView mainText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msgImg=itemView.findViewById(R.id.backImgset);
            username = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.title);
            timestamp = itemView.findViewById(R.id.date);
            mainText = itemView.findViewById(R.id.maintext);
        }
    }


    @NonNull
    @Override
    public DiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.msg_item, parent, false);

        return new DiaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.ViewHolder holder, int position) {

        final Diary diary = Diary.get(position);
        final DiaryAdapter.ViewHolder h=holder;
        holder.username.setText(diary.getDisplayname());
        holder.title.setText(diary.getTitle());
        holder.mainText.setText(diary.getMainText());
        holder.timestamp.setText(diary.getTimestamp());
        holder.msgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    //TODO 수정?

                }
            }//리스트 누르면 팝업창 생성
        });
        if(diary.getImageURL().equals("default")){
            holder.msgImg.setBackgroundResource(R.drawable.ic_launcher_foreground);
        }else{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference("Msg/"+ diary.getImageURL());
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
    }

    @Override
    public int getItemCount() {
        return Diary.size();
    }
}
