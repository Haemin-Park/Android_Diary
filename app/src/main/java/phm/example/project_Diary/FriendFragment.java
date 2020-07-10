package phm.example.project_Diary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText search;
    private ImageButton searchBtn;
    private UserAdapter userAdapter;
    private List<Users> allFriends;
    String str;
    DatabaseReference rf, frf;
    FirebaseUser Fuser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.friend_fragment, container, false);

        Fuser=FirebaseAuth.getInstance().getCurrentUser();

        search = view.findViewById(R.id.search);
        searchBtn =  view.findViewById(R.id.searchBtn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str = search.getText().toString();
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                rf = FirebaseDatabase.getInstance().getReference("Users");

                rf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!Fuser.getUid().equals(str) && dataSnapshot.hasChild(str)){
                        // 추가하려는 친구가 나의 uid가 아니고, 유저 리스트에 존재하는 uid 일 때
                            HashMap<String, Object> map = new HashMap<>();
                            frf = FirebaseDatabase.getInstance().getReference("FriendsList").child(firebaseUser.getUid()).child(str);
                            map.put("fid", str); // 내 친구목록에 추가
                            frf.setValue(map);

                            HashMap<String, Object> map2 = new HashMap<>();
                            frf = FirebaseDatabase.getInstance().getReference("FriendsList").child(str).child(firebaseUser.getUid());
                            map2.put("fid", firebaseUser.getUid()); // 친구의 친구목록에 나 추가
                            frf.setValue(map2);

                            Toast.makeText(getContext(),"친구추가 완료!",Toast.LENGTH_LONG).show();
                            search.setText(null);
                        }
                        else{
                            Toast.makeText(getContext(),"이미 추가된 친구 UID 또는 존재 하지 않는 UID 입니다.",Toast.LENGTH_LONG).show();
                            search.setText(null);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        allFriends = new ArrayList<>();
        readFriends();

        return view;
    }

    private void readFriends(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference urf = FirebaseDatabase.getInstance().getReference("Users");
        final DatabaseReference frf = FirebaseDatabase.getInstance().getReference("FriendsList").child(firebaseUser.getUid());

        frf.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allFriends.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    final FriendsList friend = snapshot.getValue(FriendsList.class);

                    urf.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Users user = snapshot.getValue(Users.class);

                                if(friend.getFid().equals(user.getId()))
                                    allFriends.add(user); // 친구만 추가

                            }

                            userAdapter = new UserAdapter(getContext(), allFriends);
                            recyclerView.setAdapter(userAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
