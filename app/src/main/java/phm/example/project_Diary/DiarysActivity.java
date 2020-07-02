package phm.example.project_Diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class DiarysActivity extends AppCompatActivity {

    Button writeBtn;
    private RecyclerView recyclerv;
    private DiaryAdapter diaryAdapter;
    private List<Diary> allDiary;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseUser Fuser = FirebaseAuth.getInstance().getCurrentUser();
    String UserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diarys);

        Intent intent = getIntent();
        UserList=intent.getStringExtra("UserList");

        recyclerv = (RecyclerView)findViewById(R.id.recycler);
        recyclerv.setHasFixedSize(true);
        recyclerv.setLayoutManager(new LinearLayoutManager(DiarysActivity.this));

        allDiary = new ArrayList<>();

        readMsgs();

        writeBtn=(Button)findViewById(R.id.write);

        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiarysActivity.this, WriteActivity.class); // 이동하려는 액티비티
                intent.putExtra("UserList",UserList);
                startActivity(intent);
            }
        });

    }
    private void readMsgs(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Msg").child(UserList);
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allDiary.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Diary diary = snapshot.getValue(Diary.class);
                    allDiary.add(diary);

                }

                diaryAdapter = new DiaryAdapter(DiarysActivity.this, allDiary);
                recyclerv.setAdapter(diaryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}