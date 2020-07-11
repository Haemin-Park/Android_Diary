package phm.example.project_Diary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static android.content.Context.CLIPBOARD_SERVICE;

public class SettingFragment extends Fragment {

    ImageView profile_image;
    EditText username;
    TextView uid;

    DatabaseReference reference;
    FirebaseUser Fuser;
    String cusername="";

    StorageReference storageReference;

    private static final int PICK_FROM_ALBUM = 1;
    Uri userPhotoUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.setting_fragment, container, false);

        Fuser = FirebaseAuth.getInstance().getCurrentUser();

        profile_image = view.findViewById(R.id.imageView);
        username = view.findViewById(R.id.profile_user);
        uid = view.findViewById(R.id.uid);

        profile_image.setOnClickListener(userPhotoIVClickListener);
        profile_image.setBackground(new ShapeDrawable(new OvalShape()));
        profile_image.setClipToOutline(true);

        uid.setText(Fuser.getUid());
        uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("uid", Fuser.getUid());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "복사완료",Toast.LENGTH_LONG).show();

            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(Fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                username.setText(user.getDisplayname()); //유저명 바꾸기
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.ic_launcher_foreground);
                }
                else{
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference("imageURL/"+user.getImageURL());
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            Glide.with(getActivity()).load(uri.toString()).into(profile_image);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Button saveBtn =view.findViewById(R.id.save);
        Button logoutBtn =view.findViewById(R.id.logout);
        saveBtn.setOnClickListener(saveBtnClickListener);
        logoutBtn.setOnClickListener(logoutBtnClickListener);


        return view;

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
        if (requestCode==PICK_FROM_ALBUM && resultCode== getActivity().RESULT_OK) {
            profile_image.setImageURI(data.getData());
            userPhotoUri = data.getData();
        }
    }

    Button.OnClickListener saveBtnClickListener = new View.OnClickListener() {
        public void onClick(final View view) {

            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();

            HashMap<String, Object> map = new HashMap<>();

            cusername = username.getText().toString();
            map.put("displayname", cusername);
            reference.updateChildren(map);

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(cusername).build();
            Fuser.updateProfile(profileUpdates);

            if (userPhotoUri!=null) {
                map.put("imageURL", uid);
                reference.updateChildren(map);
            }

            if (userPhotoUri==null) {

            } else {
                // small image
                Glide.with(getContext())
                        .asBitmap()
                        .load(userPhotoUri)
                        .apply(new RequestOptions().override(200, 200))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();
                                FirebaseStorage.getInstance().getReference().child("imageURL/" + uid).putBytes(data);

                            }
                        });
            }

        }
    };
    Button.OnClickListener logoutBtnClickListener = new View.OnClickListener() {
        public void onClick(final View view) {
            SaveSharedPreference.clearUser(getContext()); // 자동로그인 해제
            Intent intent=new Intent(getContext(),MainActivity.class);
            startActivity(intent); // 첫 화면으로 이동
            getActivity().finish();
        }
    };
}

