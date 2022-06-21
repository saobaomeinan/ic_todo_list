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
import pro.sbmn.android.cloudstodo.concrete.SigninData;
import pro.sbmn.android.cloudstodo.R;

public class SigninActivity extends AppCompatActivity {
    //表单控件
    EditText username_edt;
    EditText password_edt;
    Button signin_btn;
    TextView forgetpwd_edt;
    TextView notuname_edt;
    TextView useragree_edt;
    private String uname;
    private String upwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        initView();
        listenerSign();
    }

    //初始化组件
    private void initView() {
        username_edt = (EditText) findViewById(R.id.username_edt);
        password_edt = (EditText) findViewById(R.id.password_edt);
        signin_btn = (Button) findViewById(R.id.signin_btn);
        forgetpwd_edt = (TextView) findViewById(R.id.forgetpwd_edt);
        notuname_edt = (TextView) findViewById(R.id.notuname_edt);
        useragree_edt = (TextView) findViewById(R.id.useragreement_edt);
    }

    private void initText() {
        uname = username_edt.getText().toString().trim();
        upwd = password_edt.getText().toString().trim();
        Log.d("res", "账号：" + uname + " 密码：" + upwd);
    }

    //注册事件
    private void listenerSign() {
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initText();
                String str = checkEdits();
                if (str.equals("正确")) {
                    //发起请求代码
                    connect(uname, upwd);
                } else {
                    Toast.makeText(SigninActivity.this, str, Toast.LENGTH_SHORT).show();
                }
            }
        });
        forgetpwd_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SigninActivity.this, "请联系管理员！", Toast.LENGTH_LONG).show();
            }
        });
        notuname_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("a3", "点击了");
                Intent it = new Intent();
                it.setClass(SigninActivity.this, SignupActivity.class);
                startActivity(it);
            }
        });
        useragree_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://8.142.188.54/ic_todo_list/useragreement.html");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    //表单验证
    public String checkEdits() {
        String result_str;
        String uname = username_edt.getText().toString().trim();
        String upwd = password_edt.getText().toString().trim();
        if (uname.equals("")) {
            result_str = "用户名不能为空！";
        } else if (upwd.equals("")) {
            result_str = "密码不能为空！";
        } else if (uname.length() > 15) {
            result_str = "用户名过长，不能超过15位";
        } else if (upwd.length() > 20) {
            result_str = "密码过长，不能超过20位";
        } else {
            result_str = "正确";
        }
        return result_str;
    }

    //使用OkHttp连接服务器通信
    private void connect(String uname, String upwd) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("username", uname)
                .add("password", upwd)
                .build();
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url("http://8.142.188.54/ic_todo_list/api/signin.php")
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
                SigninData signinData = gson.fromJson(res, SigninData.class);
                upDateUi(signinData); // 调用更新UI方法
            }
        });
    }

    // 更新UI方法
    private void upDateUi(SigninData signinData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (signinData.getCode().equals("200")) {
                    //登录成功，跳转列表页面
                    Intent it = new Intent();
                    it.setClass(SigninActivity.this, MainActivity.class);
                    it.putExtra("uid", signinData.getUid());
                    startActivity(it);
                } else if (signinData.getMessage().equals("用户或密码错误")) {
                    Toast.makeText(SigninActivity.this, "用户或密码错误，请检查", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SigninActivity.this, "服务器错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
