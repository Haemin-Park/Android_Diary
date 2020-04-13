package phm.example.project_chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class SettingFragment extends Fragment {

    ImageView profile_image;
    EditText username;

    DatabaseReference Dreference;
    DatabaseReference reference;
    FirebaseUser Fuser;
    String username1="";

    StorageReference storageReference;

    private static final int PICK_FROM_ALBUM = 1;
    Uri userPhotoUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.setting_fragment, container, false);

        profile_image = view.findViewById(R.id.imageView);
        username = view.findViewById(R.id.profile_user);

        profile_image.setOnClickListener(userPhotoIVClickListener);
        profile_image.setBackground(new ShapeDrawable(new OvalShape()));

        Fuser = FirebaseAuth.getInstance().getCurrentUser();
        Dreference = FirebaseDatabase.getInstance().getReference("Users").child(Fuser.getUid());

        Dreference.addValueEventListener(new ValueEventListener() {
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

        if(Build.VERSION.SDK_INT >= 21) {
            profile_image.setClipToOutline(true);
        }

        Button saveBtn =view.findViewById(R.id.save);
        saveBtn.setOnClickListener(saveBtnClickListener);

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

            if (!validateForm()) return;

            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            reference = FirebaseDatabase.getInstance().getReference("Users").child(Fuser.getUid());

            HashMap<String, Object> map = new HashMap<>();

            username1 = username.getText().toString();
            map.put("displayname", username1);
            reference.updateChildren(map);

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(username1).build();
            Fuser.updateProfile(profileUpdates);

            Log.d("와우와우",Fuser.getDisplayName());

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

        private boolean validateForm () {
            boolean valid = true;

            return valid;
        }

        }

