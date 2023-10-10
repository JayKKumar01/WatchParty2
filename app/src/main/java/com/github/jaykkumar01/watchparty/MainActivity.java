package com.github.jaykkumar01.watchparty;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.jaykkumar01.watchparty.enums.RoomType;
import com.github.jaykkumar01.watchparty.interfaces.FirebaseListener;
import com.github.jaykkumar01.watchparty.models.ListenerData;
import com.github.jaykkumar01.watchparty.models.Room;
import com.github.jaykkumar01.watchparty.models.UserModel;
import com.github.jaykkumar01.watchparty.utils.AspectRatio;
import com.github.jaykkumar01.watchparty.utils.Base;
import com.github.jaykkumar01.watchparty.utils.FirebaseUtils;
import com.github.jaykkumar01.watchparty.utils.Permission;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("DefaultLocale")
public class MainActivity extends AppCompatActivity {

    private TextInputEditText etJoinName,etCode;
    private AppCompatButton btnJoin;
    private boolean isLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etJoinName = findViewById(R.id.etJoinName);
        etCode = findViewById(R.id.etCode);
        btnJoin = findViewById(R.id.btnJoin);
        Permission.askPermission(this);
        UserModel userModel = new UserModel("name","id",false,false,System.currentTimeMillis());
        //FirebaseUtils.writeUserData("012345",userModel);

        AspectRatio.set(this);

    }
    @SuppressLint("SetTextI18n")
    public void join(View view) {
        if (!Permission.isGranted(this)) {
            Permission.askPermission(this);
            return;
        }

        if (isLoading) {
            return;
        }

        if (etJoinName.getText() == null || etJoinName.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }

        isLoading = true;
        btnJoin.setText("Verifying...");

        String name = etJoinName.getText().toString().trim();
        String code = (etCode.getText() != null && !etCode.getText().toString().isEmpty()) ? etCode.getText().toString().trim() : null;

        String userId = Base.generateRandomString();
        UserModel userModel = new UserModel(name, userId, false, false, System.currentTimeMillis());
        Room room = new Room(userModel, code);

        if (code == null) {
            code = Base.generateRandomRoomCode(this);
            room.setCode(code);
            room.setRoomType(RoomType.CREATED);
            FirebaseUtils.updateUserData(code, userModel,
                    new FirebaseListener() {
                        @Override
                        public void onComplete(boolean successful,ListenerData data) {
                            if (!isLoading) {// remove callback
                                return;
                            }
                            if (successful) {
                                launchPlayerActivity(room);
                            }
                            else{
                                Toast.makeText(MainActivity.this, data.getErrorMessage(), Toast.LENGTH_SHORT).show();
                            }
                            cancel(btnJoin);
                        }
                    });

            return;
        }


        FirebaseUtils.checkCodeExists(code, new FirebaseListener() {
            @Override
            public void onComplete(boolean successful, ListenerData data) {
                if (!isLoading) {// remove callback
                    return;
                }
                if (successful) {
                    FirebaseUtils.updateUserData(room.getCode(), room.getUser(), null);
                    Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    room.setRoomType(RoomType.JOINED);
                    launchPlayerActivity(room);
                } else {
                    Toast.makeText(MainActivity.this, data.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
                cancel(btnJoin);
            }
        });
    }




    private void launchPlayerActivity(Room room) {
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        intent.putExtra(getString(R.string.room), room);
        startActivity(intent);
    }





    @SuppressLint("SetTextI18n")
    public void cancel(View view) {
        isLoading = false;
        btnJoin.setText("Join");
    }

    public void info(View view) {
//        Intent intent = new Intent(MainActivity.this, DrawingActivity.class);
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }
}
