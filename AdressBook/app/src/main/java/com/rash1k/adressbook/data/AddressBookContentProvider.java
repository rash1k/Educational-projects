package com.rash1k.adressbook.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.rash1k.adressbook.R;

import static com.rash1k.adressbook.data.DatabaseDescription.AUTHORITY;
import static com.rash1k.adressbook.data.DatabaseDescription.Contact;
import static com.rash1k.adressbook.data.DatabaseDescription.Contact.TABLE_NAME;

public class AddressBookContentProvider extends ContentProvider {

    //    Для обращения к базе данных
    private AddressBookDatabaseHelper mDatabaseHelper;

    //    UriMatcher помогает ContentProvider определить выполянемую операцию
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);


    //    Константы используемые для определения выполянемой операции
    public static final int ONE_CONTACT = 1;
    public static final int CONTACTS = 2;

    //  Ствтический блок для настройки UriMatcher объекта ContentProvider
    static {

//    URI для таблицы
        URI_MATCHER.addURI(AUTHORITY, TABLE_NAME, CONTACTS);

//    URI для контакта с заданным идентификатором

        URI_MATCHER.addURI(AUTHORITY, TABLE_NAME + "/#", ONE_CONTACT);
    }

    public AddressBookContentProvider() {

    }

    //    Вызывается при создании AddressBookContentProvider
    @Override
    public boolean onCreate() {
        mDatabaseHelper = new AddressBookDatabaseHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

//        Создаем SQLireQueryBuilder для запроса к таблице contacts

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contact.TABLE_NAME);

        switch (URI_MATCHER.match(uri)) {

            case ONE_CONTACT:
//                Выбрать один контакт с заданным идентификатором
//                queryBuilder.appendWhere(Contact._ID + "=" + uri.getLastPathSegment());
                queryBuilder.appendWhere(Contact._ID + "=" + ContentUris.parseId(uri));
                break;
            case CONTACTS: // Выбрать все контакты
                break;
            default:
                throw new UnsupportedOperationException(getContext()
                        .getString(R.string.invalid_query_uri) + uri);

        }

//        Выполнить запрос для одного или всех контактов
        Cursor cursor = queryBuilder.query(mDatabaseHelper.getReadableDatabase(), projection,
                selection, selectionArgs, null, null, sortOrder);

//        Настройка изменений в контенте
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

//    Вставка нового контакта в базу данных

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri newContactUri = null;

        switch (URI_MATCHER.match(uri)) {
            case CONTACTS:
//                При успехе вохвращается идентификатор строки нового контакта

                long rowId = mDatabaseHelper.getWritableDatabase().insert(Contact.TABLE_NAME, null, values);

//                Если контакт был вставлен то создать Uri, в противном случае исключение

                if (rowId > 0) {
                    newContactUri = Contact.buildContactUri(rowId);

//                    Опевестить наблюдаделей об изменениях в базе данных
                    getContext().getContentResolver().notifyChange(uri, null);

                } else
                    throw new SQLException(getContext().getString(R.string.insert_failed) + uri);
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_insert_uri) + uri);
        }
        return newContactUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int numberOfRowsUpdater;

        switch (URI_MATCHER.match(uri)) {
            case ONE_CONTACT:
//              id контакта

                String id = uri.getLastPathSegment();

//                Обновление контакта
                numberOfRowsUpdater = mDatabaseHelper.getWritableDatabase()
                        .update(Contact.TABLE_NAME, values, Contact._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext()
                        .getString(R.string.invalid_update_uri) + uri);
        }
        if (numberOfRowsUpdater != 0) {
//                    Если были внесены изменения, оповестить наблюдателей

            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsUpdater;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numberOfRowsDeleted;

        switch (URI_MATCHER.match(uri)) {
            case ONE_CONTACT:
//              id контакта

                String id = uri.getLastPathSegment();

//                Удаление контакта
                numberOfRowsDeleted = mDatabaseHelper.getWritableDatabase()
                        .delete(Contact.TABLE_NAME, Contact._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(getContext()
                        .getString(R.string.invalid_delete_uri) + uri);
        }
        if (numberOfRowsDeleted != 0) {
//                    Если были внесены изменения, оповестить наблюдателей

            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsDeleted;

    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
