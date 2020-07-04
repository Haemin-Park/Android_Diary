package phm.example.project_Diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.Arrays;

public class DiarysStart extends Activity {

    String Diarysname,UserList, friendID, myName, friendName;

    TextView name;

    DatabaseReference Dreference;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseUser Fuser = FirebaseAuth.getInstance().getCurrentUser();

    Button startbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diarys_start);
        name=(TextView)findViewById(R.id.name);

        startbtn = (Button) findViewById(R.id.startbtn);
        Intent intent = getIntent();

        friendID = intent.getStringExtra("user"); // 채팅 상대
        friendName = intent.getStringExtra("username"); //채팅 상대

        name.setText(friendName+"님과 채팅을 시작하겠습니까?");

        String[] arr={friendID, Fuser.getUid()};
        Arrays.sort(arr);

        UserList = arr[0]+"@"+arr[1]; // 소팅 후 유저리스트 생성(같은 방이 두개 생기지 않게 하기 위함)

        Diarysname = UserList; // 다이어리 이름도 유저 리스트로

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!databaseReference.child("DiaryRoom").child(friendID).child(Diarysname).child("diarysUserList").getKey().equals(Diarysname)) {
                    createDiarys(friendID);
                    createDiarys(Fuser.getUid());

                    DiarysUsers(Fuser.getUid(),friendID);
                }

                Intent intent = new Intent(DiarysStart.this, DiarysActivity.class);
                intent.putExtra("UserList", UserList);
                startActivity(intent);
                finish();
            }
        }); //ok버튼을 누르면 데이터베이스에 채팅방 생성
    }

    public void createDiarys(String id){

        databaseReference.child("DiaryRoom").child(id).child(Diarysname).child("diarysUserList").setValue(UserList);

        Dreference = FirebaseDatabase.getInstance().getReference("Users").child(Fuser.getUid());
        myName = Fuser.getDisplayName();

        if(id == Fuser.getUid()){
        databaseReference.child("DiaryRoom").child(id).child(Diarysname).child("myName").setValue(myName);
        databaseReference.child("DiaryRoom").child(id).child(Diarysname).child("friendName").setValue(friendName);

       } else{
            databaseReference.child("DiaryRoom").child(id).child(Diarysname).child("myName").setValue(friendName);
            databaseReference.child("DiaryRoom").child(id).child(Diarysname).child("friendName").setValue(myName);
        }
    }

    public void DiarysUsers(String mid, String fid){
        databaseReference.child("DiarysUserList").child(Diarysname).child(mid).setValue("true");
        databaseReference.child("DiarysUserList").child(Diarysname).child(fid).setValue("true");
    }

}
