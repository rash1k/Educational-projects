package com.rash1k.adressbook;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.rash1k.adressbook.data.DatabaseDescription.Contact;

public class DetailContactFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int CONTACT_LOADER = 0;
    private static final String KEY_PREF_LOCATION = "location";
    private DetailContactFragmentListener mListener;
    private Uri contactUri;

    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView streetTextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView zipTextView;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (DetailContactFragmentListener) context;
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
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_contact_details, container, false);

        Bundle arguments = getArguments();

        if (arguments != null) {
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        }

        if (contactUri != null) {
            getLoaderManager().initLoader(CONTACT_LOADER, null, this);
        }

        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
        emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        streetTextView = (TextView) view.findViewById(R.id.streetTextView);
        cityTextView = (TextView) view.findViewById(R.id.cityTextView);
        stateTextView = (TextView) view.findViewById(R.id.stateTextView);
        zipTextView = (TextView) view.findViewById(R.id.zipTextView);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                mListener.onEditContact(contactUri);
                break;
            case R.id.action_delete:
                deleteContact();
                break;
            case R.id.action_find_location:
//                findContactLocation();
                mListener.onFindContact();
                break;
        }
        return true;
    }


    private void deleteContact() {
        onCreateDialogForDeleteContact(null).show();
    }

   /* private void findContactLocation() {
        boolean accessLocation = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(KEY_PREF_LOCATION, false);
        if (accessLocation) {
            mListener.onFindContact(true);
        } else {
            onCreateDialogForAccessLocation(null).show();
        }
    }*/

    //    DialogFragment для подтверждения удаления контакта
    private Dialog onCreateDialogForDeleteContact(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_message)
                .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getContentResolver().delete(contactUri, null, null);
                        mListener.onContactDeleted();
                    }
                })
                .setNegativeButton(R.string.button_cancel, null);
        return builder.create();
    }

   /* private Dialog onCreateDialogForAccessLocation(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.confirm_title)
                .setMessage(R.string.confirm_message_access_location)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onFindContact(false);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CONTACT_LOADER:
                return new CursorLoader(getActivity(), contactUri, null, null, null, null);
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

            // Заполнение TextView полученными данными
            nameTextView.setText(data.getString(nameIndex));
            phoneTextView.setText(data.getString(phoneIndex));
            emailTextView.setText(data.getString(emailIndex));
            streetTextView.setText(data.getString(streetIndex));
            cityTextView.setText(data.getString(cityIndex));
            stateTextView.setText(data.getString(stateIndex));
            zipTextView.setText(data.getString(zipIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface DetailContactFragmentListener {

        void onContactDeleted();

        void onEditContact(Uri contactUri);

        void onFindContact();
    }


}