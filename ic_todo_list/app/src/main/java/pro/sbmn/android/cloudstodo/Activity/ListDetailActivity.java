package pro.sbmn.android.cloudstodo.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pro.sbmn.android.cloudstodo.Alarms.AlarmService;
import pro.sbmn.android.cloudstodo.concrete.ListSimpleData;
import pro.sbmn.android.cloudstodo.R;

public class ListDetailActivity extends AppCompatActivity {
    EditText title_edt, content_edt;
    TextView dataout_edt, titles;
    Button data_btn, time_btn, cancel_btn, save_btn;
    RadioGroup level_rdg;
    RadioButton le1_rdb, le2_rdb, le3_rdb, le4_rdb, le5_rdb;
    CheckBox valid_chk;
    int year = 2022;
    int month = 1;
    int day = 1;
    int houre = 8;
    int minute = 0;
    private String uid;
    private String time = "2022-01-01 08:00:00";
    private String title = "";
    private String content = "";
    private String level = "3";
    private String valid = "0";
    private String type = "新增待办";
    private String lid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        Intent it = getIntent();
        uid = it.getStringExtra("uid");
        if (it.getStringExtra("type").equals("chg")) {
            type = "修改待办";
            lid = it.getStringExtra("lid");
            time = it.getStringExtra("time");
            title = it.getStringExtra("title");
            content = it.getStringExtra("content");
            level = it.getStringExtra("level");
            valid = it.getStringExtra("valid");
        }
        initView();
        listenerSign();
    }

    private void initView() {
        titles = findViewById(R.id.titles);
        title_edt = findViewById(R.id.list_title_edt);
        content_edt = findViewById(R.id.list_content_edt);
        dataout_edt = findViewById(R.id.list_dataout_edt);
        data_btn = findViewById(R.id.list_data_btn);
        time_btn = findViewById(R.id.list_time_btn);
        cancel_btn = findViewById(R.id.list_cancel_btn);
        save_btn = findViewById(R.id.list_save_btn);
        level_rdg = findViewById(R.id.list_level_rdg);
        valid_chk = findViewById(R.id.list_valid_chk);
        setDates();
    }

    private void setDates() {
        titles.setText(type);
        title_edt.setText(title);
        content_edt.setText(content);
        dataout_edt.setText(time);
        switch (level) {
            case "1":
                level_rdg.check(R.id.leve1);
                break;
            case "2":
                level_rdg.check(R.id.leve2);
                break;
            case "3":
                level_rdg.check(R.id.leve3);
                break;
            case "4":
                level_rdg.check(R.id.leve4);
                break;
            case "5":
                level_rdg.check(R.id.leve5);
                break;
        }
        if (valid.equals("1")) {
            valid_chk.setChecked(true);
        } else {
            valid_chk.setChecked(false);
        }
    }

    private void getDatas() {
        time = dataout_edt.getText().toString().trim();
        title = title_edt.getText().toString().trim();
        content = content_edt.getText().toString().trim();
        switch (level_rdg.getCheckedRadioButtonId()) {
            case R.id.leve1:
                level = "1";
                break;
            case R.id.leve2:
                level = "2";
                break;
            case R.id.leve3:
                level = "3";
                break;
            case R.id.leve4:
                level = "4";
                break;
            case R.id.leve5:
                level = "5";
                break;
        }
        if (valid_chk.isChecked()) {
            valid = "1";
        } else {
            valid = "0";
        }
    }

    private void listenerSign() {
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.putExtra("uid", uid);
                uid = null;
                it.setClass(ListDetailActivity.this, MainActivity.class);
                startActivity(it);
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = checkEdits();
                if (str.equals("正确")) {
                    // 获取数据
                    getDatas();
                    // 设置提醒
                    setRemind();
                    if (type.equals("修改待办")) {
                        listChgConnect(uid, time, title, content, level, valid);
                    } else {
                        listAddConnect(uid, time, title, content, level, valid);
                    }
                } else {
                    Toast.makeText(ListDetailActivity.this, str, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setRemind() {
        long times = dateToStamp(time);
        if (times > System.currentTimeMillis()) {
            Intent it = new Intent();
            it.setClass(ListDetailActivity.this, AlarmService.class);
            it.putExtra("Time", times);
            startService(it);
        }
    }

    public static long dateToStamp(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long res = 0;
        if (!s.equals("")) {
            try {
                res = sdf.parse(s).getTime();
            } catch (Exception e) {
                Log.e("res", e.toString());
            }
        } else {
            res = System.currentTimeMillis();
        }
        return res;
    }

    private void listAddConnect(String uid, String time, String title, String content, String level, String valid) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("uid", uid)
                .add("time", time)
                .add("title", title)
                .add("content", content)
                .add("level", level)
                .add("valid", valid)
                .build();
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url("http://8.142.188.54/ic_todo_list/api/listadd.php")
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
                ListSimpleData listSimpleData = gson.fromJson(res, ListSimpleData.class);
                upDateUi(listSimpleData); // 调用更新UI方法
            }
        });
    }

    private void listChgConnect(String uid, String time, String title, String content, String level, String valid) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("lid", lid)
                .add("uid", uid)
                .add("time", time)
                .add("title", title)
                .add("content", content)
                .add("level", level)
                .add("valid", valid)
                .build();
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url("http://8.142.188.54/ic_todo_list/api/listchg.php")
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
                ListSimpleData listSimpleData = gson.fromJson(res, ListSimpleData.class);
                upDateUi(listSimpleData); // 调用更新UI方法
            }
        });
    }

    private void upDateUi(ListSimpleData listSimpleData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listSimpleData.getCode().equals("200")) {
                    //成功，跳转列表页面
                    if (type.equals("新增待办")) {
                        Toast.makeText(ListDetailActivity.this, "新增待办成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListDetailActivity.this, "修改待办成功", Toast.LENGTH_SHORT).show();
                    }
                    Intent it = new Intent();
                    it.putExtra("uid", uid);
                    it.setClass(ListDetailActivity.this, MainActivity.class);
                    startActivity(it);
                } else if (listSimpleData.getMessage().equals("用户错误")) {
                    Toast.makeText(ListDetailActivity.this, "用户错误，请重新登录", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListDetailActivity.this, "服务器错误，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String checkEdits() {
        String result_str;
        String title = title_edt.getText().toString().trim();
        if (title.equals("")) {
            result_str = "待办标题不能为空！";
        } else if (title.length() > 20) {
            result_str = "待办标题过长，不能超过20位";
        } else {
            result_str = "正确";
        }
        return result_str;
    }

    //时间日期控件方法
    public void getDate(View v) {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                ListDetailActivity.this.year = year;
                ListDetailActivity.this.month = monthOfYear;
                ListDetailActivity.this.day = dayOfMonth;
                setDate();
            }
        }, 2022, 0, 1).show();
    }

    public void getTime(View v) {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                ListDetailActivity.this.houre = hourOfDay;
                ListDetailActivity.this.minute = minute;
                setDate();
            }
        }, 8, 0, true).show();
    }

    private void setDate() {
        String mons = String.valueOf(month + 1);
        String days = String.valueOf(day);
        String hous = String.valueOf(houre);
        String mins = String.valueOf(minute);
        if (ListDetailActivity.this.month < 10) {
            mons = "0" + mons;
        }
        if (ListDetailActivity.this.day < 10) {
            days = "0" + days;
        }
        if (ListDetailActivity.this.minute < 10) {
            mins = "0" + mins;
        }
        if (ListDetailActivity.this.houre < 10) {
            hous = "0" + hous;
        }
        String date = year + "-" + mons + "-" + days + " " + hous + ":" + mins + ":" + "00";
        dataout_edt.setText(date);
    }
}
