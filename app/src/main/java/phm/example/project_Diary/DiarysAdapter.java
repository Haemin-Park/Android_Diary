package phm.example.project_Diary;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiarysAdapter extends RecyclerView.Adapter<DiarysAdapter.ViewHolder> {
    private Context context;
    private List<Rooms> rooms;

    public DiarysAdapter(Context context, List<Rooms> rooms){
        this.context = context;
        this.rooms = rooms;
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

        final Rooms room = rooms.get(position);
        final DiarysAdapter.ViewHolder h=holder;
        holder.chatName.setText(room.getMyusernm()+", "+room.getYourusernm());
        holder.userbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    //TODO if문으로 이미 만들어져있을 경우 팝업 안띄우게 하려면 데이터베이스 구조를 바꿔야함(아니면 너무 번거롭고 길어짐)
                    Intent intent=new Intent(view.getContext(), DiarysActivity.class);
                    intent.putExtra("UserList", room.getRoomUserList());
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
