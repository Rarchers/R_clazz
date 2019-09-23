package com.example.r_clazz.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by 25532 on 2019/3/2.
 */

public class LocalDB extends SQLiteOpenHelper {

    private static final String Course = "create table Course (id integer" +
            " primary key autoincrement," +
            "name text," +
            "student text," +
            "ids text," +
            "goals text," +
            "onlinetime text," +
            "time text) ";
    private static final String Creat_Book = "create table Users (id integer primary key autoincrement," +
            "phone text," +
            "password text," +
            "identity text," +
            "identitycode text," +
            "name text)";
    private Context context;

    public LocalDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Creat_Book);
        db.execSQL(Course);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Course");
        db.execSQL("drop table if exists Users");
        onCreate(db);
    }

    //查找本用户名,返回一个Uer类型
    public static Users query_user(String identitycode, LocalDB dbhelper) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Cursor cursor = db.query("Users", null, "identitycode = ?", new String[]{identitycode}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String phone = cursor.getString(cursor.getColumnIndex("phone"));
                String password = cursor.getString(cursor.getColumnIndex("password"));
                String identity = cursor.getString(cursor.getColumnIndex("identity"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                Users user = new Users(name,phone,password,identity,identitycode);
                return user;
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return null;


    }

    //更新当前用户的密码
    public static void updata_user(String phone, String newpass, LocalDB dbhelper) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newpass);
        db.update("Users", values, "phone = ?", new String[]{phone});
    }


    public static void insert_user(Context context, String phone, String password,String identity,String id,String name, LocalDB dbhelper) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("password", password);
        values.put("identitycode", id);
        values.put("name", name);
        values.put("identity", identity);
        db.insert("Users", null, values);
    }

    public static void addData_service(String username, String context, String data, String start_time, String end_time, String special, String codeID, LocalDB localDB, int status) {
        SQLiteDatabase db = localDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", username);
        values.put("context", context);
        values.put("codeID", codeID);
        values.put("data", data);
        values.put("status", status);
        values.put("start_time", start_time);
        values.put("end_time", end_time);
        values.put("special_request", special);
        db.insert("Service", null, values); // 插入数据
        values.clear();
    }

    public static void addData_note(String name, String context, String title, String uid, LocalDB localDB) {
        SQLiteDatabase db = localDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("context", context);
        values.put("uid", uid);
        values.put("name", name);
        db.insert("Notes", null, values); // 插入数据
        values.clear();
    }


}
