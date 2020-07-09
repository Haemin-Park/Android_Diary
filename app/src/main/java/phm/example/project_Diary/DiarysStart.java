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

    String Diarysname,UserList, mid, fid, friendName;

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

        fid = intent.getStringExtra("user"); // 채팅 상대
        friendName = intent.getStringExtra("username"); //채팅 상대

        name.setText(friendName+"님과 함께하는 일기장을 만드시겠습니까?");

        String[] arr={fid, Fuser.getUid()};
        Arrays.sort(arr);

        UserList = arr[0]+"@"+arr[1]; // 소팅 후 유저리스트 생성(같은 방이 두개 생기지 않게 하기 위함)

        Diarysname = UserList; // 다이어리 이름도 유저 리스트로

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createDiarys(fid);
                createDiarys(Fuser.getUid());

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
        mid = Fuser.getUid();

        if(id == mid){
        databaseReference.child("DiaryRoom").child(id).child(Diarysname).child("mid").setValue(mid);
        databaseReference.child("DiaryRoom").child(id).child(Diarysname).child("fid").setValue(fid);

       } else{
            databaseReference.child("DiaryRoom").child(id).child(Diarysname).child("mid").setValue(fid);
            databaseReference.child("DiaryRoom").child(id).child(Diarysname).child("fid").setValue(mid);
        }
    }

}
