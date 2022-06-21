package pro.sbmn.android.cloudstodo.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pro.sbmn.android.cloudstodo.Activity.ListDetailActivity;
import pro.sbmn.android.cloudstodo.R;
import pro.sbmn.android.cloudstodo.concrete.ListFidData;
import pro.sbmn.android.cloudstodo.concrete.ListSimpleData;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ListFidData.DataDTO> list;
    private String uid;
    public final int TYPE_EMPTY = 0;
    public final int TYPE_NORMAL = 1;

    public ListAdapter(Context context, List<ListFidData.DataDTO> list, String uid) {
        this.context = context;
        this.list = list;
        this.uid = uid;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.size() <= 0) {
            return TYPE_EMPTY;
        }
        return TYPE_NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_isnull, parent, false);
            return new RecyclerView.ViewHolder(itemView) {
            };
        }
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            // 写入值
            ListFidData.DataDTO datas = list.get(position);
            ((MyViewHolder) holder).list_id.setText(position + 1 + "");
            ((MyViewHolder) holder).list_title.setText(datas.getListTitle());
            ((MyViewHolder) holder).list_content.setText(datas.getListContent());
            ((MyViewHolder) holder).list_time.setText("提醒时间：" + datas.getRemindTime());
            ((MyViewHolder) holder).list_level.setText("待办等级：" + datas.getListLevel() + "");
            // 绑定点击事件
            onClick(holder, position);
        }
    }

    private void onClick(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeItem(position, uid);
                Log.d("res", String.valueOf(position));
            }
        });
        ((MyViewHolder) holder).delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("res", String.valueOf(position));
                deleteItem(position);
            }
        });
    }

    // 点击修改按钮事件调用
    private void changeItem(int itemnums, String uid) {
        Intent it = new Intent();
        it.putExtra("uid", uid);
        it.putExtra("type", "chg");
        it.putExtra("lid", list.get(itemnums).getLid());
        it.putExtra("time", String.valueOf(list.get(itemnums).getRemindTime()));
        it.putExtra("title", list.get(itemnums).getListTitle());
        it.putExtra("content", list.get(itemnums).getListContent());
        it.putExtra("level", list.get(itemnums).getListLevel());
        it.putExtra("valid", list.get(itemnums).getValid());
        it.setClass(context, ListDetailActivity.class);
        context.startActivity(it);
    }

    // 点击删除按钮事件调用
    private void deleteItem(int itemnums) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("您确定要删除该数据吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("lid", list.get(itemnums).getLid())
                        .build();
                Request request = new Request.Builder()
                        .addHeader("content-type", "application/json")
                        .url("http://8.142.188.54/ic_todo_list/api/listdel.php")
                        .post(requestBody)
                        .build();
                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d("res", "连接失败：" + e.toString());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String res = response.body().string();
                        Log.d("res", "连接成功：" + res);
                        Gson gson = new Gson();
                        ListSimpleData listDelData = gson.fromJson(res, ListSimpleData.class);
                    }
                });
                list.remove(itemnums);
                notifyItemRemoved(itemnums);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        if (list.size() <= 0) {
            return 1;
        }
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView list_id, list_title, list_content, list_time, list_level;
        ImageButton change_btn, delete_btn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            list_id = itemView.findViewById(R.id.list_id_edt);
            list_title = itemView.findViewById(R.id.list_title_edt);
            list_content = itemView.findViewById(R.id.list_content_edt);
            list_time = itemView.findViewById(R.id.list_time_edt);
            list_level = itemView.findViewById(R.id.list_level_edt);
            change_btn = itemView.findViewById(R.id.list_change_btn);
            delete_btn = itemView.findViewById(R.id.list_delete_btn);
        }
    }
}
