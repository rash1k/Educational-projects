package com.rash1k.adressbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.rash1k.adressbook.data.DatabaseDescription.Contact;

public class AddEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface AddEditFragmentListener {

        void onAddEditCompleted(Uri contactUri);

    }

    private final static int CONTACT_LOADER = 0;
    private AddEditFragmentListener mListener; // MainActivity
    private Uri contactUri; // Выбор нового контакта
    private boolean addingNewContact = true; // Сохранение(true) нового или изменение (false)

    private TextInputLayout nameTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout streetTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipTextInputLayout;
    private FloatingActionButton saveContactFAB;
    private CoordinatorLayout mCoordinatorLayout; // SnackBar

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (AddEditFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // Будут команды меню

        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        nameTextInputLayout = (TextInputLayout) view.findViewById(R.id.nameTextInputLayout);

        if (nameTextInputLayout.getEditText() != null) {
            nameTextInputLayout.getEditText().addTextChangedListener(nameChangedListener);
        }

        phoneTextInputLayout = (TextInputLayout) view.findViewById(R.id.phoneTextInputLayout);
        emailTextInputLayout = (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
        streetTextInputLayout = (TextInputLayout) view.findViewById(R.id.streetTextInputLayout);
        cityTextInputLayout = (TextInputLayout) view.findViewById(R.id.cityTextInputLayout);
        stateTextInputLayout = (TextInputLayout) view.findViewById(R.id.stateTextInputLayout);
        zipTextInputLayout = (TextInputLayout) view.findViewById(R.id.zipTextInputLayout);

        saveContactFAB = (FloatingActionButton) view.findViewById(R.id.saveFloatingActionButton);
        saveContactFAB.setOnClickListener(saveContactButtonClicked);
        updateSaveButtonFAB();

//        Для SnackBar
        mCoordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);

        Bundle arguments = getArguments(); // null присоздании контакта

        if (arguments != null) {
            addingNewContact = false;
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        if (contactUri != null) {
            getLoaderManager().initLoader(CONTACT_LOADER, null, this);
        }

        return view;
    }

    private final TextWatcher nameChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private final View.OnClickListener saveContactButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Скрыть клавиатуру
            ((InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getView().getWindowToken(), 0);

            saveContact(); // Сохранение контакта в базе данных
        }
    };


    // Сохранение контакта в базе данных
    private void saveContact() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(Contact.COLUMN_NAME, nameTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_PHONE, phoneTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_EMAIL, emailTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_STREET, streetTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_CITY, cityTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_STATE, stateTextInputLayout.getEditText().getText().toString());
        contentValues.put(Contact.COLUMN_ZIP, zipTextInputLayout.getEditText().getText().toString());

        if (addingNewContact) {
            Uri newContactUri = getActivity().getContentResolver().insert(
                    Contact.CONTENT_URI, contentValues);

            if (newContactUri != null) {
                Snackbar.make(mCoordinatorLayout,
                        R.string.contact_added, Snackbar.LENGTH_LONG).show();
                mListener.onAddEditCompleted(newContactUri);
            } else
                Snackbar.make(mCoordinatorLayout,
                        R.string.contact_no_added,
                        Snackbar.LENGTH_LONG).show();
        } else {
            int updateRows = getActivity().getContentResolver().
                    update(contactUri, contentValues,
                    null, null);

            mListener.onAddEditCompleted(null);

            if (updateRows > 0) {
                Snackbar.make(mCoordinatorLayout,
                        R.string.contact_updated, Snackbar.LENGTH_LONG).show();
            } else
                Snackbar.make(mCoordinatorLayout,
                        R.string.contact_no_updated, Snackbar.LENGTH_LONG).show();
        }

    }

    private void updateSaveButtonFAB() {

        String input = null;
        if (nameTextInputLayout.getEditText() != null) {
            input = nameTextInputLayout.getEditText().getText().toString();
        }

        if (input.trim().length() != 0) {
            saveContactFAB.show();
        } else {
            saveContactFAB.hide();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case CONTACT_LOADER:
                return new CursorLoader(getContext(), contactUri, null, null, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
// Если контакт существует в базе данных, вывести его информацию

        if (data != null && data.moveToFirst()) {

            // Получение индекса столбца для каждого элемента данных
            int nameIndex = data.getColumnIndex(Contact.COLUMN_NAME);
            int phoneIndex = data.getColumnIndex(Contact.COLUMN_PHONE);
            int emailIndex = data.getColumnIndex(Contact.COLUMN_EMAIL);
            int streetIndex = data.getColumnIndex(Contact.COLUMN_STREET);
            int cityIndex = data.getColumnIndex(Contact.COLUMN_CITY);
            int stateIndex = data.getColumnIndex(Contact.COLUMN_STATE);
            int zipIndex = data.getColumnIndex(Contact.COLUMN_ZIP);

            // Заполнение компонентов EditText полученными данными
            nameTextInputLayout.getEditText().setText(
                    data.getString(nameIndex));
            phoneTextInputLayout.getEditText().setText(
                    data.getString(phoneIndex));
            emailTextInputLayout.getEditText().setText(
                    data.getString(emailIndex));
            streetTextInputLayout.getEditText().setText(
                    data.getString(streetIndex));
            cityTextInputLayout.getEditText().setText(
                    data.getString(cityIndex));
            stateTextInputLayout.getEditText().setText(
                    data.getString(stateIndex));
            zipTextInputLayout.getEditText().setText(
                    data.getString(zipIndex));

            updateSaveButtonFAB();

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
