package phm.example.project_chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Users> Users;
    //private boolean status;

    public UserAdapter(Context context, List<Users> Users){
        this.context = context;
        this.Users = Users;
        //this.status = status;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout userbt;
        public TextView username;
        public ImageView profile;
        //private ImageView status_on;
        //private ImageView status_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userbt=itemView.findViewById(R.id.user);
            username = itemView.findViewById(R.id.userListName);
            profile = itemView.findViewById(R.id.userListImage);
            //status_on = itemView.findViewById(R.id.status_on);
            //status_off = itemView.findViewById(R.id.status_off);

        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Users user = Users.get(position);
        final ViewHolder h=holder;
        holder.username.setText(user.getDisplayname());
        holder.userbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
       {//TODO if문으로 이미 만들어져있을 경우 팝업 안띄우게--하려면 데이터베이스 구조를 바꿔야함(아니면 너무 번거롭고 길어짐)
                    Intent intent=new Intent(view.getContext(),ChatStart.class);
                    intent.putExtra("user", user.getId());
                    intent.putExtra("username", user.getDisplayname());
                    view.getContext().startActivity(intent);}
            }//리스트 누르면 팝업창 생성
        });
        if(user.getImageURL().equals("default")){
            holder.profile.setImageResource(R.drawable.ic_launcher_foreground);
        }else{
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference("imageURL/"+user.getImageURL());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Glide.with(context).load(uri.toString()).apply(RequestOptions.circleCropTransform()).into(h.profile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

/*
        // 활동 상태
        if(status){
            if(user.getStatus().equals("online")){
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
        return Users.size();
    }
}
