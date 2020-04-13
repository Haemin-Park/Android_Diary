package phm.example.project_chat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.List;


public class ChatsFragment extends Fragment {

    Button writeBtn;
/*
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chats_fragment, container, false);

        writeBtn=view.findViewById(R.id.write);
        writeBtn.setOnClickListener(btnClick);

        return view;
    }

    Button.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(final View view) {
            Log.d("클릭","성공");
            Intent intent=new Intent(getActivity(),WriteActivity.class); // 이동하려는 액티비티
            startActivity(intent);
        }
    };*/
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Rooms> allRooms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chats_fragment, container, false);

        writeBtn=view.findViewById(R.id.write);
        writeBtn.setOnClickListener(btnClick);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        allRooms = new ArrayList<>();
        readRooms();

        return view;
    }
    Button.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(final View view) {
            Log.d("클릭","성공");
            Intent intent=new Intent(getActivity(),WriteActivity.class); // 이동하려는 액티비티
            startActivity(intent);
        }
    };

    private void readRooms(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Rooms").child(firebaseUser.getUid());;
        Log.d("아이디",firebaseUser.getUid()+" ");
        Log.d("아이디",reference+" ");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allRooms.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d("아이디",dataSnapshot.getChildren()+" ");
                    Rooms room = snapshot.getValue(Rooms.class);
                    Log.d("아이디",room+" ");

                    allRooms.add(room);

                }

                chatAdapter = new ChatAdapter(getContext(), allRooms);
                recyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
