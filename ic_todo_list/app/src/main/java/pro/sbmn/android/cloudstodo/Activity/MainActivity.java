package pro.sbmn.android.cloudstodo.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pro.sbmn.android.cloudstodo.Adapter.ListAdapter;
import pro.sbmn.android.cloudstodo.concrete.ListFidData;
import pro.sbmn.android.cloudstodo.R;

public class MainActivity extends AppCompatActivity {
    private RecyclerView list_main;
    private ListAdapter listAdapter;
    private List<ListFidData.DataDTO> dataDTOs = new ArrayList<>();
    Button newlist_btn, logout_btn;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent it = getIntent();
        uid = it.getStringExtra("uid");
        getDataByServer();
    }

    //从服务器获取列表数据
    private void getDataByServer() {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("uid", uid)
                .build();
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url("http://8.142.188.54/ic_todo_list/api/listfid.php")
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("res", "连接失败：" + e.toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String str = response.body().string();
                Gson gson = new Gson();
                Log.d("res", str);
                if (!str.contains("\"data\":null")) {
                    ListFidData listFidData = gson.fromJson(str, ListFidData.class);
                    dataDTOs.clear();
                    dataDTOs.addAll(listFidData.getData());
                }
                updataUi();
            }
        });
    }

    // 新开UI线程，因为使用异步的方式获取数据，防止数据没找到UI就跑完，所以在此绑定控件
    private void updataUi() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initView();
                listenerSign();
            }
        });
    }

    // 初始化控件
    private void initView() {
        // RecyclerView控件设置
        list_main = findViewById(R.id.list_main);
        list_main.setLayoutManager(new LinearLayoutManager(this));
        listAdapter = new ListAdapter(MainActivity.this, dataDTOs, uid);
        list_main.setAdapter(listAdapter);
        newlist_btn = findViewById(R.id.newlist_btn);
        logout_btn = findViewById(R.id.logout_btn);
    }

    //注册监听事件
    private void listenerSign() {
        newlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, ListDetailActivity.class);
                it.putExtra("uid", uid);
                it.putExtra("type", "add");
                startActivity(it);
            }
        });
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uid = null;
                Intent it = new Intent();
                it.setClass(MainActivity.this, SigninActivity.class);
                startActivity(it);
            }
        });
    }
}