package phm.example.project_Diary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    FirebaseUser Fuser;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private Context context;
    private List<Users> Users;
    String UserList;
    Boolean existDiary;

    Intent intent;
    View v;

    public UserAdapter(Context context, List<Users> Users){
        this.context = context;
        this.Users = Users;
        Fuser= FirebaseAuth.getInstance().getCurrentUser();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout userbt;
        public TextView username;
        public ImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userbt=itemView.findViewById(R.id.user);
            username = itemView.findViewById(R.id.userListName);
            profile = itemView.findViewById(R.id.userListImage);

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

                String[] arr = {user.getId(), Fuser.getUid()};
                Arrays.sort(arr);
                v = view;

                UserList = arr[0]+"@"+arr[1]; // 소팅 후 유저리스트 생성(같은 방이 두개 생기지 않게 하기 위함)

                existDiary = true;

                // addListenerForSingleValueEvent: 한 번만 호출되고 즉시 삭제되는 콜백
                databaseReference.child("DiaryRoom").addListenerForSingleValueEvent(new ValueEventListener() {
                    // 리스너는 이벤트 발생 시점에 데이터베이스에서 지정된 위치에 있던 데이터를 포함하는 데이터스냅샷 수신
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(Fuser.getUid())){ // 존재하지 않는 경우 null을 반환
                            if(dataSnapshot.child(Fuser.getUid()).hasChild(UserList)){

                                intent = new Intent(v.getContext(), DiarysActivity.class);
                                intent.putExtra("UserList", UserList);
                                v.getContext().startActivity(intent);
                            }

                            else{ // 일기장이 존재하지 않는 경우 일기장을 만듦

                                intent = new Intent(v.getContext(), DiarysStart.class);
                                intent.putExtra("user", user.getId());
                                intent.putExtra("username", user.getDisplayname());
                                v.getContext().startActivity(intent);
                            }

                        }
                        else{ // 일기장이 존재하지 않는 경우 일기장을 만듦

                            intent = new Intent(v.getContext(), DiarysStart.class);
                            intent.putExtra("user", user.getId());
                            intent.putExtra("username", user.getDisplayname());
                            v.getContext().startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
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
    }

    @Override
    public int getItemCount() {
        return Users.size();
    }
}
