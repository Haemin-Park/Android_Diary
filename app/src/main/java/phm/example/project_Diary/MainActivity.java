package phm.example.project_Diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button login, signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent;

        if(SaveSharedPreference.getUser(MainActivity.this).length() == 0) {
            login=(Button)findViewById(R.id.main_login);
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class); // 이동하려는 액티비티
                    startActivity(intent);
                    finish();
                }
            });

            signup=(Button)findViewById(R.id.main_signup);
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(),SignupActivity.class); // 회원가입
                    startActivity(intent);
                    finish();
                }
            });

        } else {
            // Call Next Activity
            intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}
