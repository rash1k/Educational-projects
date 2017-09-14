package com.rash1k.adressbook.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;

import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class AddressBookDatabaseHelperTest {
    @Test
    public void onCreate() throws Exception {

        SQLiteDatabase database = mock(SQLiteDatabase.class);
        Context context = mock(Context.class);
        AddressBookDatabaseHelper addressBookDatabaseHelper = new AddressBookDatabaseHelper(context);

        addressBookDatabaseHelper.onCreate(database);

        verify(database,atMost(1)).execSQL("TEST");

    }

}