package com.tsd.directory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private MyHelper myHelper;
    private MyAdapter myAdapter;
    private ListView listView;
    private ArrayList<User> users = new ArrayList<>();
    private FloatingActionButton fab;
    private SearchView searchView, searchView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_book);
        init();
        //一进来就查询数据
        selectUsers();
        //添加数据
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSowDialog();
            }
        });
        //listView点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.clearFocus();
                updateShowDialog(position);
            }
        });
        //listView长按事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("警告")
                        .setMessage("是否删除\n姓名:" + users.get(position).getName() + "\n手机号:" + users.get(position).getPhone())
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                searchView.clearFocus();
                                if (deleteUser(String.valueOf(users.get(position).getId()))) {
                                    Toast.makeText(MainActivity.this, "信息已经删除!", Toast.LENGTH_LONG).show();
                                    selectUsers();
                                } else {
                                    Toast.makeText(MainActivity.this, "删除失败!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", null).create();
                dialog.show();
                return true;
            }
        });
        //设置搜索文本监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //当点击搜索按钮时触发
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SSS", "点击了搜索" + query);
                likeSelectUsers(query);
                searchView.clearFocus();
                return false;
            }

            //当搜索内容改变时触发
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("MMM", "内容改变" + newText);
                likeSelectUsers(newText);
                return false;
            }
        });
    }

    //添加数据的对话框
    private void addSowDialog() {
        searchView.clearFocus();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        Button btnAdd = view.findViewById(R.id.btn_add);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        EditText edName = view.findViewById(R.id.ed_name);
        EditText edPhone = view.findViewById(R.id.ed_phone);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString().trim();
                String phone = edPhone.getText().toString().trim();
                if (name.equals("") || phone.equals("")) {
                    Toast.makeText(MainActivity.this, "数据不允许为空!", Toast.LENGTH_LONG).show();
                    return;
                }
                Boolean isSave = addUsers(name, phone);
                if (isSave) {
                    Toast.makeText(MainActivity.this, "信息添加成功!", Toast.LENGTH_LONG).show();
                    selectUsers();

                } else {
                    Toast.makeText(MainActivity.this, "信息添加失败!", Toast.LENGTH_LONG).show();
                }
                //关闭对话框
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        //dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3),LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    //更新数据的对话框
    private void updateShowDialog(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        Button btnAdd = view.findViewById(R.id.btn_add);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        EditText edName = view.findViewById(R.id.ed_name);
        EditText edPhone = view.findViewById(R.id.ed_phone);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText("修改用户");
        edName.setText(users.get(position).getName());
        edPhone.setText(users.get(position).getPhone());
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString().trim();
                String phone = edPhone.getText().toString().trim();
                if (name.equals("") || phone.equals("")) {
                    Toast.makeText(MainActivity.this, "数据不允许为空!", Toast.LENGTH_LONG).show();
                    return;
                }
                User user = new User(users.get(position).getId(), edName.getText().toString().trim(), edPhone.getText().toString().trim());
                Boolean isSave = updateUser(user);
                if (isSave) {
                    Toast.makeText(MainActivity.this, "信息修改成功!", Toast.LENGTH_LONG).show();
                    selectUsers();

                } else {
                    Toast.makeText(MainActivity.this, "信息修改失败!", Toast.LENGTH_LONG).show();
                }
                //关闭对话框
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        //dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3),LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    //数据库操作 查询数据
    public boolean selectUsers() {
        users.clear();
        SQLiteDatabase db;
        db = myHelper.getReadableDatabase(); // 获取可读写数据库
        Cursor cursor = db.query("information", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setPhone(cursor.getString(2));
            users.add(user);
        }
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        return users.size() > 0 ? true : false;
    }

    //数据库操作 模糊查询数据
    public boolean likeSelectUsers(String str) {
        users.clear();
        SQLiteDatabase db;
        db = myHelper.getReadableDatabase(); // 获取可读写数据库
        String current_sql_sel = "SELECT  * FROM information" + " where name like '%" + str + "%' " + "or phone like '%" + str + "%'";
        Cursor cursor = db.rawQuery(current_sql_sel, null);
        while (cursor.moveToNext()) {
            User user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setPhone(cursor.getString(2));
            users.add(user);
        }
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        return users.size() > 0 ? true : false;
    }

    //数据库操作 添加数据
    public boolean addUsers(String name, String phone) {
        SQLiteDatabase db;
        ContentValues values;
        values = new ContentValues();
        values.put("name", name);
        values.put("phone", phone);
        db = myHelper.getWritableDatabase(); //获取可写数据库
        Long number = db.insert("information", null, values);
        db.close(); //关闭数据库
        return number != -1 ? true : false;
    }

    //数据库操作 删除数据
    public boolean deleteUser(String id) {
        SQLiteDatabase db;
        db = myHelper.getWritableDatabase();
        int isDeleteUser = db.delete("information", "_id=?", new String[]{id});
        db.close();
        return isDeleteUser > 0 ? true : false;
    }

    //数据库操作 更新数据
    public boolean updateUser(User user) {
        SQLiteDatabase db;
        ContentValues values;
        db = myHelper.getWritableDatabase();
        values = new ContentValues(); // 要修改的数据
        values.put("phone", user.getPhone());
        values.put("name", user.getName());
        int isUpdate = db.update("information", values, "_id=?", new String[]{String.valueOf(user.getId())});
        return isUpdate > 0 ? true : false;
    }

    private void init() {
        myHelper = new MyHelper(this); // 初始化数据库
        listView = findViewById(R.id.list_lv);
        //Item之间的间距
        listView.setDividerHeight(1);
        myAdapter = new MyAdapter(users);
        listView.setAdapter(myAdapter);
        fab = findViewById(R.id.fab);
        searchView = findViewById(R.id.sv_cus);
    }

}