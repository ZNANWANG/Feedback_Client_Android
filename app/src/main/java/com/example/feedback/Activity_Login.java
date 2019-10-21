package com.example.feedback;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import adapter.UserBeanAdapter;
import bean.UserBean;
import main.AllFunctions;
import util.UserInfoOperator;
import widget.LoginVideoView;

public class Activity_Login extends AppCompatActivity {

    AllFunctions allFunctions;
    Handler handler;
    private LoginVideoView mLoginVideoView;
    private EditText mEmailText;
    private EditText mPasswordText;
    private TextView mSignupTextView;
    //    private Switch switchRem;
    private CheckBox mEyeCheckBox;
    private CheckBox mDropdownCheckBox;
    private ImageView mClearEmail;
    private ImageView mClearPassword;
    private RelativeLayout mPasswordLayout;
    private Button mLoginButton;
    private List<View> mDropDownInvisibleViews;
    private HashMap<String, String> mAccounts;
    public static UserInfoOperator mUserInfoOpertor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        initDropDownGroup();
        resetHandler();
    }

    protected void onNewIntent(Intent intent) {
        init();
        resetHandler();
    }

    protected void resetHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 100:
                        Toast.makeText(Activity_Login.this, "The email and password are not correct. Please check and try again.", Toast.LENGTH_SHORT).show();
                        break;
                    case 101: //means mLoginButton successfully and go to next page
                        Log.d("EEEE", "login!!!!!");
                        Intent intent = new Intent(Activity_Login.this, Activity_Homepage.class);
                        startActivityForResult(intent, 1);
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
    }

    // 1、检查是否有读写sdcard的权限
    private void checkWriteAndReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 1000);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permission : grantResults) {
            if (permission == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    private void init() {
        checkWriteAndReadPermission();

        Log.d("EEEE", "start!!!!!");
//        Full screen - no status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        no actionbar
//        getActionBar().hide();
//        getSupportActionBar().hide();

        initVideoView();

        mEmailText = findViewById(R.id.editText_email_inlogin);
        mEmailText.setText("");

        mPasswordText = findViewById(R.id.editText_password_inlogin);
        mPasswordText.setText("");

        allFunctions = AllFunctions.getObject();

        mSignupTextView = (TextView) findViewById(R.id.textView_signup_inlogin);
        mSignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Login.this, Activity_Signup.class);
                startActivityForResult(intent, 2);
            }
        });

//        switchRem = (Switch)findViewById(R.id.switch_rem);
//        if(switchRem.isChecked()) {
//            extractMemInfo(mEmailText, mPasswordText);
//        }
//        switchRem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    extractMemInfo(mEmailText, mPasswordText);
//                } else {
//                    mEmailText.setText("");
//                    mPasswordText.setText("");
//                }
//            }
//        });

        mClearEmail = findViewById(R.id.iv_clear_account);
        mClearEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mEmailText.setText("");
            }
        });

        mClearPassword = findViewById(R.id.iv_clear_password);
        mClearPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPasswordText.setText("");
            }
        });

        mDropdownCheckBox = findViewById(R.id.cb_login_drop_down);
        mDropdownCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setDropDownGroupVisible(View.INVISIBLE);
                    showDropDownWindow();
                } else {
                    setDropDownGroupVisible(View.VISIBLE);
                }
            }
        });

        mEyeCheckBox = findViewById(R.id.iv_login_open_eye);
        mEyeCheckBox.setOnCheckedChangeListener((new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mPasswordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        }));

        mPasswordLayout = findViewById(R.id.password_layout);

//        android:onClick="login"
        mLoginButton = findViewById(R.id.buttonLogin_inlogin);

        mUserInfoOpertor = new UserInfoOperator(Activity_Login.this);
    }

    public void initVideoView() {
        mLoginVideoView = (LoginVideoView) findViewById(R.id.videoview);
        mLoginVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.media));
        mLoginVideoView.start();
        mLoginVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mLoginVideoView.start();
            }
        });
    }

    public void initDropDownGroup() {
        mDropDownInvisibleViews = new ArrayList<>();
        mDropDownInvisibleViews.add(mPasswordLayout);
        mDropDownInvisibleViews.add(mPasswordText);
//        mDropDownInvisibleViews.add(switchRem);
        mDropDownInvisibleViews.add(mLoginButton);
    }

    public void setDropDownGroupVisible(int visible) {
        for (View view : mDropDownInvisibleViews) {
            view.setVisibility(visible);
        }
    }

    public void login(View view) {
        Log.d("EEEE", "click login!!!!");
        AllFunctions.getObject().setHandler(handler);

//        if(switchRem.isChecked()) {
//            Log.d("CCC", "come");
//            String email = mEmailText.getText().toString();
//            String password = mPasswordText.getText().toString();
//            memInfo(email, password);
//
//            String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";
//
//            if (email.matches(emailPattern)) {
//                allFunctions.mLoginButton(email, password);
//            } else {
//                Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Log.d("CCC", "go");
//            SharedPreferences.Editor editor = getSharedPreferences("data", 0).edit();
//            editor.clear();
//            editor.commit();
//        }

        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\\.+[a-z]+";
        if (email.matches(emailPattern)) {
            allFunctions.login(email, password);
        } else {
            Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
        }
    }

//    public void memInfo(String email, String password) {
//        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("email", email);
//        editor.putString("password", password);
//        editor.commit();
//    }
//
//    public void extractMemInfo() {
//        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
//        String email = sp.getString("email", "");
//        String password = sp.getString("password", "");
//        mEmailText.setText(email);
//        mPasswordText.setText(password);
//    }

    private void showDropDownWindow() {
        final PopupWindow window = new PopupWindow(mDropdownCheckBox);
        mAccounts = mUserInfoOpertor.getUserInfo();
        List<UserBean> userBeanList = new ArrayList<>();
        for (String key : mAccounts.keySet()) {
            userBeanList.add(new UserBean(key, mAccounts.get(key)));
        }
        final UserBeanAdapter adapter = new UserBeanAdapter(getApplicationContext());
        adapter.replaceData(userBeanList);
        ListView userListView = (ListView) View.inflate(this,
                R.layout.window_account_drop_down, null);
        userListView.setAdapter(adapter);
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDropdownCheckBox.setChecked(false);
                UserBean checkedUser = adapter.getItem(position);
                mEmailText.setText(checkedUser.getAccount());
                mPasswordText.setText(checkedUser.getPassword());
                window.dismiss();
            }
        });
//        userListView.addFooterView(new TextView(this));

        window.setContentView(userListView);
        window.setAnimationStyle(0);
        window.setBackgroundDrawable(null);
        window.setWidth(mPasswordLayout.getWidth());
        window.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setOutsideTouchable(true);
        window.setFocusable(true);
        window.setTouchable(true);
        window.showAsDropDown(mEmailText);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mDropdownCheckBox.setChecked(false);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                boolean hasBackPressed = data.getBooleanExtra("hasBackPressed", true);
                initVideoView();
            }
        } else if(requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                boolean hasBackPressed = data.getBooleanExtra("hasBackPressed", true);
                initVideoView();
            }
        }
    }

//    public static UserInfoOperator getObject() {
//        if (mUserInfoOpertor == null) {
//            mUserInfoOpertor = new UserInfoOperator();
//        }
//        return mUserInfoOpertor;
//    }
}
