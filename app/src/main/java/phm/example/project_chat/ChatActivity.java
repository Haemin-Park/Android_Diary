package phm.example.project_chat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import static java.security.AccessController.getContext;

public class ChatActivity extends AppCompatActivity {

    Button writeBtn;
    private RecyclerView recyclerv;
    private MsgAdapter msgAdapter;
    private List<Msg> allMsg;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseUser Fuser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerv = (RecyclerView)findViewById(R.id.recycler);
        recyclerv.setHasFixedSize(true);
        recyclerv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        allMsg = new ArrayList<>();
        readUsers();

        writeBtn=(Button)findViewById(R.id.write);

        writeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, WriteActivity.class); // 이동하려는 액티비티
                startActivity(intent);
            }
        });

            }
    private void readUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Msg");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Rooms");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allMsg.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Msg msg = snapshot.getValue(Msg.class);

                {
                        allMsg.add(msg);
                    }
                }

                msgAdapter = new MsgAdapter(ChatActivity.this, allMsg);
                recyclerv.setAdapter(msgAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
        }