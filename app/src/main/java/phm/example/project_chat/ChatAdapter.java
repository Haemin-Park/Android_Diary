package phm.example.project_chat;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private List<Rooms> rooms;

    public ChatAdapter(Context context, List<Rooms> rooms){
        this.context = context;
        this.rooms = rooms;
        //this.status = status;
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
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);

        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

        final Rooms room = rooms.get(position);
        final ChatAdapter.ViewHolder h=holder;
        holder.chatName.setText(room.getMyusernm()+", "+room.getYourusernm());
        holder.userbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    //TODO if문으로 이미 만들어져있을 경우 팝업 안띄우게--하려면 데이터베이스 구조를 바꿔야함(아니면 너무 번거롭고 길어짐)
                    Intent intent=new Intent(view.getContext(),ChatActivity.class);
                    //intent.putExtra("user", room.getId()); 정보줄때!!! 쓰자!!!
                    view.getContext().startActivity(intent);
                }
            }//리스트 누르면 팝업창 생성
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }
}
