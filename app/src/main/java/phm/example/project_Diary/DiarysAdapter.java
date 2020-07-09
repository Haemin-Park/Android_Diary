package phm.example.project_Diary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class DiarysAdapter extends RecyclerView.Adapter<DiarysAdapter.ViewHolder> {
    private Context context;
    private List<DiaryRoom> diaryRooms;
    String UserList;
    String mname, fname;

    public DiarysAdapter(Context context, List<DiaryRoom> diaryRooms){
        this.context = context;
        this.diaryRooms = diaryRooms;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout userbt;
        public TextView chatName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userbt=itemView.findViewById(R.id.chat);
            chatName = itemView.findViewById(R.id.chatListName);

        }
    }

    @NonNull
    @Override
    public DiarysAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diarys_item, parent, false);

        return new DiarysAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiarysAdapter.ViewHolder holder, int position) {

        final DiaryRoom diaryroom = diaryRooms.get(position);
        final DiarysAdapter.ViewHolder h=holder;

        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Users user = snapshot.getValue(Users.class);

                    if(diaryroom.getMid().equals(user.getId()))
                        mname = user.getDisplayname();

                    if(diaryroom.getFid().equals(user.getId()))
                        fname = user.getDisplayname();

                }

                h.chatName.setText(mname+", "+fname); // 일기장 참여자 목록

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.userbt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder=new AlertDialog.Builder(context);

                builder.setMessage("일기장을 삭제하시겠습니까?");

                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        UserList=diaryroom.getDiarysUserList();

                        FirebaseDatabase.getInstance().getReference("Diarys").child(UserList).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                FirebaseStorage storage = FirebaseStorage.getInstance();;

                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    Diary diary = snapshot.getValue(Diary.class);
                                    storage.getReference("Diarys/" + UserList + "/" + diary.getpostId()).delete();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        FirebaseDatabase.getInstance().getReference("Diarys").child(diaryroom.getDiarysUserList()).removeValue();
                        FirebaseDatabase.getInstance().getReference("DiaryRoom").child(diaryroom.getMid()).child(diaryroom.getDiarysUserList()).removeValue();
                        FirebaseDatabase.getInstance().getReference("DiaryRoom").child(diaryroom.getFid()).child(diaryroom.getDiarysUserList()).removeValue();
                        Toast.makeText(context, "삭제 완료", Toast.LENGTH_LONG).show();
                    }
                });

                builder.setPositiveButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;
            }
        });

        holder.userbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    Intent intent=new Intent(view.getContext(), DiarysActivity.class);
                    intent.putExtra("UserList", diaryroom.getDiarysUserList());
                    view.getContext().startActivity(intent);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return diaryRooms.size();
    }
}
