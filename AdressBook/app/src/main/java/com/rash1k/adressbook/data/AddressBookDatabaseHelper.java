package com.rash1k.adressbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rash1k.adressbook.data.DatabaseDescription.Contact;

public class AddressBookDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "AddressBook.db";
    public static final int DATABASE_VERSION = 1;

    public AddressBookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    //    Создание таблицы contacts при создании базы данных
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_CONTACTS_TABLE = "CREATE TABLE " +
                Contact.TABLE_NAME + "(" + Contact._ID +
                " integer primary key, " +
                Contact.COLUMN_NAME + " TEXT, " +
                Contact.COLUMN_PHONE + " TEXT, " +
                Contact.COLUMN_EMAIL + " TEXT, " +
                Contact.COLUMN_STREET + " TEXT, " +
                Contact.COLUMN_CITY + " TEXT, " +
                Contact.COLUMN_STATE + " TEXT, " +
                Contact.COLUMN_ZIP + " TEXT);";

        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
