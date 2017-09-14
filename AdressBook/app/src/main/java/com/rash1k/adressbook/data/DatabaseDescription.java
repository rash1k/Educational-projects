package com.rash1k.adressbook.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {

//    Имя ContentProvider обычно совпадает с именем пакета

    public static final String AUTHORITY = "com.rash1k.adressbook.data";

//    Базовый URI для взамодействия с ContentProvider

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    public static  final class Contact implements BaseColumns {

        public static final String TABLE_NAME = "contact";

//        Объект URI для таблицы контактов

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(TABLE_NAME)
                .build();

        //        Имена столбцов таблицы
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ZIP = "zip";

//        Создание Uri для конкретного контакта

        public static Uri buildContactUri(long id) {

            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
