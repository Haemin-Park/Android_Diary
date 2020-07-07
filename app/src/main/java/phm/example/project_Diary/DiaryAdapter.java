package phm.example.project_Diary;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private Context context;
    private List<Diary> Diary;
    String UserList;

    public DiaryAdapter(Context context, List<Diary> Diary, String UserList){
        this.context = context;
        this.Diary = Diary;
        this.UserList = UserList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profile;
        public TextView username;
        public LinearLayout diaryImg;
        public TextView title;
        public TextView timestamp;
        public TextView mainText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profileImg);
            username = itemView.findViewById(R.id.username);
            diaryImg = itemView.findViewById(R.id.backImgset);
            title = itemView.findViewById(R.id.title);
            timestamp = itemView.findViewById(R.id.date);
            mainText = itemView.findViewById(R.id.maintext);
        }
    }


    @NonNull
    @Override
    public DiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diary_item, parent, false);

        return new DiaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.ViewHolder holder, int position) {

        final Diary diary = Diary.get(position);

        final DiaryAdapter.ViewHolder h = holder;

        holder.username.setText(diary.getUsername());
        holder.title.setText(diary.getTitle());
        holder.mainText.setText(diary.getMainText());
        holder.timestamp.setText(diary.getTimestamp());
        holder.diaryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {

                    Intent intent = new Intent(context, WriteActivity.class);

                    intent.putExtra("diary", diary); // diary 객체 전달
                    intent.putExtra("UserList",UserList);

                    context.startActivity(intent);

                }
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference("imageURL/" + diary.getId());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(context).load(uri.toString()).apply(RequestOptions.circleCropTransform()).into(h.profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Glide.with(context).load(R.drawable.ic_launcher_foreground).apply(RequestOptions.circleCropTransform()).into(h.profile);

            }
        });


        if(diary.getImageURL().equals("default")){
                holder.diaryImg.setBackgroundResource(R.drawable.ic_launcher_foreground);
        }else{

            storageReference = storage.getReference("Diarys/"+ UserList +"/"+ diary.getpostId());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Glide.with(context).load(uri.toString()).into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                h.diaryImg.setBackground(resource);

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
