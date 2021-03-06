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
import android.util.Log;
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
import java.util.Map;

public class WriteActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseUser Fuser;
    String Wtitle="";
    String WmainText="";
    String WformatDate;
    Boolean photo = false, modify = false;

    private static final int PICK_FROM_ALBUM = 1;
    Uri userPhotoUri;

    ImageView gallery;
    TextView title, mainText, time;
    Button saveBtn, removeBtn;
    Diary diary;
    String UserList, StrTitle , StrMainText , StrTime , StrGallery , WriterId;

    Intent intent;
    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        firebaseAuth = FirebaseAuth.getInstance();
        Fuser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        gallery = (ImageView) findViewById(R.id.gallery);
        title = (TextView)findViewById(R.id.title);
        mainText = (TextView)findViewById(R.id.mainText);
        time = (TextView)findViewById(R.id.time);

        saveBtn =(Button)findViewById(R.id.textSave);
        removeBtn =(Button)findViewById(R.id.textRemove);

        gallery.setBackground(new ShapeDrawable(new OvalShape()));
        gallery.setClipToOutline(true);

        firstSet(); // 초기 세팅

        saveBtn.setOnClickListener(saveBtnClickListener);
        removeBtn.setOnClickListener(removeBtnClickListener);


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
            SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.dd HH:mm"); // 시간을 나타내는 포맷 지정
            WformatDate = sdfNow.format(date); // 변수에 값 저장

            Wtitle = title.getText().toString();
            WmainText = mainText.getText().toString();

            if(modify)
                modify();
            else
                insert();


            finish();
        }// onclick 끝


    };

    Button.OnClickListener removeBtnClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) { // 스토리지의 사진 삭제, 데이터베이스 데이터 삭제 필요

            FirebaseDatabase.getInstance().getReference("Diarys").child(UserList).child(postId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            if(!StrGallery.equals("default")) {

                                storage.getReference("Diarys/" + UserList + "/" + postId).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(WriteActivity.this, "삭제 완료", Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                            else{
                                Toast.makeText(WriteActivity.this, "삭제 완료", Toast.LENGTH_LONG).show();
                            }

                            finish();

                        }
                    });
        }
    };

    public void firstSet(){

        intent = getIntent();

        UserList = intent.getStringExtra("UserList");

        diary = (Diary)intent.getSerializableExtra("diary"); // 일기 객체 받아옴
        removeBtn.setVisibility(View.INVISIBLE); // 글을 작성하는 상태 또는 친구 일기라면 삭제 버튼 보이지 않음

        if(diary != null) {

            postId = diary.getpostId();
            StrTitle = diary.getTitle();
            StrMainText = diary.getMainText();
            StrTime = diary.getTimestamp();
            StrGallery = diary.getImageURL();
            WriterId =  diary.getId();

            if(WriterId.equals(Fuser.getUid()))
                modify = true; // 수정이 가능한 상태

            if(modify) { // 본인이 작성한 일기라면 버튼 보임
                saveBtn.setVisibility(View.VISIBLE);
                removeBtn.setVisibility(View.VISIBLE);
            }
            else { // 친구의 일기라면 버튼 보이지 않음(즉 ReadOnly)
                saveBtn.setVisibility(View.INVISIBLE);
                title.setFocusable(false);
                title.setClickable(false);
                mainText.setFocusable(false);
                mainText.setClickable(false);
            }

            title.setText(StrTitle);
            mainText.setText(StrMainText);
            time.setText(StrTime);

            if (StrGallery.equals("default")) {
                gallery.setImageResource(R.drawable.noimg);
                photo = false;
            } else {
                storageReference = storage.getReference("Diarys/" + UserList + "/" + postId);
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

        if(diary == null || modify)
            gallery.setOnClickListener(userPhotoIVClickListener);

    }

    public void insert(){

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Diarys").child(UserList).push();

        postId = reference.getKey();

        HashMap<String, Object> map = new HashMap<>();

        if (userPhotoUri != null || photo) {
            map.put("imageURL", postId);
        }else{
            map.put("imageURL", "default");
        }
        map.put("title", Wtitle);
        map.put("mainText", WmainText);
        map.put("id", Fuser.getUid());
        map.put("username",Fuser.getDisplayName());
        map.put("timestamp",WformatDate);
        map.put("postId",postId);

        reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(WriteActivity.this, "일기 추가", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (userPhotoUri == null) {

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
                            FirebaseStorage.getInstance().getReference().child("Diarys/" + UserList + "/" +postId).putBytes(data);

                        }
                    });
        }

    }

    public void modify(){

        final String uid = Fuser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("Diarys").child(UserList).child(postId);

        Map<String, Object> map = new HashMap<>();

        if (userPhotoUri != null || photo) {
            map.put("imageURL", postId);
        }else{
            map.put("imageURL", "default");
        }

        map.put("title", Wtitle);
        map.put("mainText", WmainText);
        map.put("timestamp",WformatDate);

        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(WriteActivity.this, "일기 수정", Toast.LENGTH_SHORT).show();
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
                            FirebaseStorage.getInstance().getReference().child("Diarys/" + UserList + "/" + postId).putBytes(data);

                        }
                    });
        }

    }

    private boolean validateForm () {
        boolean valid = true;

        return valid;
    }
}
