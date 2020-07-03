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

public class DiarysStart extends Activity {
    String userID="";
    String Roomname="";
    String Roomnamechk="";
    String UserList="";
    String UserListchk="";
    String username1,username2="";

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

        userID = intent.getStringExtra("user");
        username1 = intent.getStringExtra("username");//채팅할 상대

        name.setText(username1+"님과 채팅을 시작하겠습니까?");

        UserList = userID+"@"+Fuser.getUid();
        UserListchk=Fuser.getUid()+"@"+userID;
        Roomname = "@make@"+UserList; // 룸 네임이 필요할까 고민해보기
        Roomnamechk = "@make@"+UserListchk;

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!databaseReference.child("Rooms").child(userID).child(Roomnamechk).child("roomUserList").getKey().equals(UserListchk)) {
                    createRoom(userID);
                    createRoom(Fuser.getUid());

                    RoomUsers(userID,Fuser.getUid());
            }
                Intent intent=new Intent(DiarysStart.this, DiarysActivity.class);
                intent.putExtra("UserList", UserList);
                startActivity(intent);
                finish();
            }
        }); //ok버튼을 누르면 데이터베이스에 채팅방 생성
    }

    public void createRoom(String id){

        databaseReference.child("Rooms").child(id).child(Roomname).child("roomUserList").setValue(UserList);

        Dreference = FirebaseDatabase.getInstance().getReference("Users").child(Fuser.getUid());
        username2=Fuser.getDisplayName();

        if(id==Fuser.getUid()){
        databaseReference.child("Rooms").child(id).child(Roomname).child("myusernm").setValue(username2);
        databaseReference.child("Rooms").child(id).child(Roomname).child("yourusernm").setValue(username1);

       }else{
            databaseReference.child("Rooms").child(id).child(Roomname).child("myusernm").setValue(username1);
            databaseReference.child("Rooms").child(id).child(Roomname).child("yourusernm").setValue(username2);}
    }
    public void RoomUsers(String id,String id2){
        databaseReference.child("RoomUsers").child(Roomname).child(id).setValue("true");
        databaseReference.child("RoomUsers").child(Roomname).child(id2).setValue("true");
    }




}
