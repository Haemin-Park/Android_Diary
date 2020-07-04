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
    private List<DiaryRoom> diarys;

    public DiarysAdapter(Context context, List<DiaryRoom> diarys){
        this.context = context;
        this.diarys = diarys;
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

        final DiaryRoom diary = diarys.get(position);
        final DiarysAdapter.ViewHolder h=holder;
        holder.chatName.setText(diary.getMyName()+", "+diary.getFriendName()); // 일기장 참여자 목록
        holder.userbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    Intent intent=new Intent(view.getContext(), DiarysActivity.class);
                    intent.putExtra("UserList", diary.getDiarysUserList());
                    view.getContext().startActivity(intent);
                }
            }//리스트 누르면 팝업창 생성
        });
    }

    @Override
    public int getItemCount() {
        return diarys.size();
    }
}
