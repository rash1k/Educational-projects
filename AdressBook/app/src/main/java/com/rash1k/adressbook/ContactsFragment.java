package com.rash1k.adressbook;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.rash1k.adressbook.data.DatabaseDescription.Contact;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACTS_LOADER = 0; // Идентификатор Loader

    private ContactsFragmentListener mListener; //Сообщает MainActivity о выборе контакта

    private ContactsAdapter mContactsAdapter;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (ContactsFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // У фрагмента есть меню
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Заполнение GUI и получение ссылки на RecyclerView
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

//        RecyclerView выводит элементы в веритикальном списке
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        Создание адаптера и слушателя щелчков на элементе
        mContactsAdapter = new ContactsAdapter(new ContactsAdapter.ContactClickListener() {
            @Override
            public void onClick(Uri contactUri) {
                mListener.onContactSelected(contactUri);
            }
        });

        recyclerView.setAdapter(mContactsAdapter); // Назначение адаптера

//        Присоеденения ItemDecorator для вывода разделителей

        recyclerView.addItemDecoration(new ItemDivider(getContext()));

//        Улучшает быстродействие, если размер макета RecyclerView не изменяется
        recyclerView.setHasFixedSize(true);

//        Получение FloatingActionButton и назначение слушателя

        view.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAddContact();
            }
        });

        return view;
    }




    /*    Вызывается после создания управляющей активности фрагмента и завершения выполнения метода
onCreateView фрагмента на этой стадии графический интерфейс фрагмента является частью иерархии
 представлений активности.  Нужно для Loader чтобы RecyclerView уже существовал*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(CONTACTS_LOADER, null, this);
    }

    // Вызывается из MainActivity при обновлении базы данных другим фрагментом
    void updateContactList() {
        mContactsAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Создание CursorLoader на основании аргумента id; в этом
        // фрагменте только один объект Loader, и команда switch не нужна

        switch (id) {
            case CONTACTS_LOADER:
                return new CursorLoader(getContext(), Contact.CONTENT_URI, null, null, null,
                        Contact.COLUMN_NAME + " COLLATE NOCASE ASC"); // Сортировка по возрастанию
            //без учета регистра
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mContactsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContactsAdapter.swapCursor(null);
    }

    public interface ContactsFragmentListener {

//        Метод вызывается при выборе контакта

        void onContactSelected(Uri uriContact);

//        Вызывается при добавлении контакта и нажатии кнопки

        void onAddContact();

        //        Вызывается при выборе в меню команды настройки
        void onSettingsSelected();

        void onLogoutSelected();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                mListener.onSettingsSelected();
                break;
            case R.id.action_logout:
                mListener.onLogoutSelected();
                break;
        }
        return true;
    }


}
