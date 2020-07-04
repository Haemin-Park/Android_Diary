package phm.example.project_Diary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;


public class DiarysFragment extends Fragment {

    private RecyclerView recyclerView;
    private DiarysAdapter diarysAdapter;
    private List<DiaryRoom> allDiaryRoom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.diarys_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        allDiaryRoom = new ArrayList<>();
        readDiaryRoom();

        return view;
    }

    private void readDiaryRoom(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DiaryRoom").child(firebaseUser.getUid());;
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allDiaryRoom.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    DiaryRoom room = snapshot.getValue(DiaryRoom.class);
                    allDiaryRoom.add(room);

                }

                diarysAdapter = new DiarysAdapter(getContext(), allDiaryRoom);
                recyclerView.setAdapter(diarysAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
