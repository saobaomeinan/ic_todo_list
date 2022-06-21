package pro.sbmn.android.cloudstodo.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pro.sbmn.android.cloudstodo.concrete.SignupData;
import pro.sbmn.android.cloudstodo.R;

public class SignupActivity extends AppCompatActivity {
    EditText username_edt_signup;
    EditText password_edt_signup;
    EditText password_again_edt_signup;
    EditText nickname_edt_signup;
    Button signup_btn_signup;
    TextView haveuname_edt_signup;
    TextView useragreement_edt_signup;
    private String uname;
    private String upwd;
    private String upwd_aga;
    private String nick;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
        listenerSign();
    }

    //初始化组件
    private void initView() {
        username_edt_signup = (EditText) findViewById(R.id.username_edt_signup);
        password_edt_signup = (EditText) findViewById(R.id.password_edt_signup);
        password_again_edt_signup = (EditText) findViewById(R.id.password_again_edt_signup);
        nickname_edt_signup = (EditText) findViewById(R.id.nickname_edt_signup);
        signup_btn_signup = (Button) findViewById(R.id.signup_btn_signup);
        haveuname_edt_signup = (TextView) findViewById(R.id.haveuname_edt_signup);
        useragreement_edt_signup = (TextView) findViewById(R.id.useragreement_edt_signup);
    }

    private void initText() {
        uname = username_edt_signup.getText().toString().trim();
        upwd = password_edt_signup.getText().toString().trim();
        upwd_aga = password_again_edt_signup.getText().toString().trim();
        nick = nickname_edt_signup.getText().toString().trim();
    }

    //注册事件
    private void listenerSign() {
        signup_btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initText();
                String str = checkEdits();
                if (str.equals("正确")) {
                    //发起请求代码
                    connect(uname, upwd, nick);
                } else {
                    Toast.makeText(SignupActivity.this, str, Toast.LENGTH_SHORT).show();
                }
            }
        });
        haveuname_edt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(SignupActivity.this, SigninActivity.class);
                startActivity(it);
            }
        });
        useragreement_edt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://8.142.188.54/ic_todo_list/useragreement.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    //表单验证
    private String checkEdits() {
        String result_str;
        if (uname.equals("")) {
            result_str = "用户名不能为空！";
        } else if (upwd.equals("")) {
            result_str = "密码不能为空！";
        } else if (upwd_aga.equals("")) {
            result_str = "确认密码不能为空！";
        } else if (nick.equals("")) {
            result_str = "昵称不能为空！";
        } else if (uname.length() > 15) {
            result_str = "用户名过长，不能超过15位";
        } else if (upwd.length() > 20) {
            result_str = "密码过长，不能超过20位";
        } else if (nick.length() > 10) {
            result_str = "昵称过长，不能超过10位";
        } else if (!upwd.equals(upwd_aga)) {
            result_str = "两次密码不一致，请检查";
        } else {
            result_str = "正确";
        }
        return result_str;
    }

    //使用OkHttp连接服务器通信
    private void connect(String uname, String upwd, String unick) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("username", uname)
                .add("password", upwd)
                .add("user_nickname", unick)
                .build();
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url("http://8.142.188.54/ic_todo_list/api/signup.php")
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("res", "连接失败：" + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                Log.d("res", "连接成功：" + res);
                Gson gson = new Gson();
                SignupData signupData = gson.fromJson(res, SignupData.class);
                upDateUi(signupData); // 调用更新UI方法
            }
        });
    }

    // 更新UI方法
    private void upDateUi(SignupData signupData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (signupData.getCode().equals("200")) {
                    Toast.makeText(SignupActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    // 跳转首页
                    Intent it = new Intent();
                    it.setClass(SignupActivity.this, SigninActivity.class);
                    startActivity(it);
                } else if (signupData.getMessage().equals("用户存在")) {
                    Toast.makeText(SignupActivity.this, "用户名已经存在，请重新填写", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SignupActivity.this, "服务器错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
