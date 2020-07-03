package phm.example.project_Diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class WriteActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    FirebaseUser Fuser;
    String w_title="";
    String w_mainText="";
    String formatDate;
    Boolean photo;

    private static final int PICK_FROM_ALBUM = 1;
    Uri userPhotoUri;

    ImageView gallery;
    TextView title,mainText,time;
    String UserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        firebaseAuth = FirebaseAuth.getInstance();

        gallery = (ImageView) findViewById(R.id.gallery);
        title = (TextView)findViewById(R.id.title);
        mainText = (TextView)findViewById(R.id.mainText);
        time=(TextView)findViewById(R.id.time);

        gallery.setOnClickListener(userPhotoIVClickListener);
        gallery.setBackground(new ShapeDrawable(new OvalShape()));

        Fuser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        UserList=intent.getStringExtra("UserList");

/*
        Dreference = FirebaseDatabase.getInstance().getReference("Msg").child(Fuser.getUid());
        Dreference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(final DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        Diary diary = dataSnapshot.getValue(Diary.class);

                        title.setText(diary.getTitle());
                        mainText.setText(diary.getMainText());
                        time.setText(diary.getTimestamp());

                        if (diary.getImageURL().equals("default")) {
                            gallery.setImageResource(R.drawable.ic_launcher_foreground);
                            photo = false;
                        } else {
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageReference = storage.getReference("Msg/"+ diary.getImageURL());//채팅방 아이디도 추가해서 경로 지정해야함!
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getApplicationContext()).load(uri.toString()).into(gallery);
                                    photo = true;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                    }

                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
        });
*/
        if(Build.VERSION.SDK_INT >= 21) {
            gallery.setClipToOutline(true);
        }

        Button saveBtn =(Button)findViewById(R.id.textSave);
        saveBtn.setOnClickListener(saveBtnClickListener);
    }
    Button.OnClickListener userPhotoIVClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
    };



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            gallery.setImageURI(data.getData());
            userPhotoUri = data.getData();
        }
    }

    Button.OnClickListener saveBtnClickListener = new View.OnClickListener() {
        public void onClick(final View view) {

            if (!validateForm()) return;

            long now = System.currentTimeMillis(); // 현재시간 msec로 구함
            Date date = new Date(now); // 현재시간 date 변수에 저장
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"); // 시간을 나타내는 포맷 지정
            formatDate = sdfNow.format(date); // 변수에 값 저장

            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            reference = FirebaseDatabase.getInstance().getReference("Msg").child(UserList);

            HashMap<String, Object> map = new HashMap<>();

            w_title = title.getText().toString();
            w_mainText = mainText.getText().toString();

            if (userPhotoUri!=null || photo==true) {
                map.put("imageURL", uid);
                //reference.updateChildren(map);
            }else{
                map.put("imageURL", "default");
                //reference.updateChildren(map);
            }
            map.put("title", w_title);
            map.put("mainText", w_mainText);
            map.put("id", Fuser.getUid());
            map.put("username",Fuser.getDisplayName());
            map.put("timestamp",formatDate);

            // reference.push().~: 랜덤자식이름으로 들어감
            reference.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(WriteActivity.this, "일기 추가", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (userPhotoUri==null) {

            } else {
                // small image
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(userPhotoUri)
                        .apply(new RequestOptions().override(200, 200))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();
                                FirebaseStorage.getInstance().getReference().child("Msg/" + UserList + "/" +formatDate).putBytes(data);

                            }
                        });
            }

        }
    };

    private boolean validateForm () {
        boolean valid = true;

        return valid;
    }
}
