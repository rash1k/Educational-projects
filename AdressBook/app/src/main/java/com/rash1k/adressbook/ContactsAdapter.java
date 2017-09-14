package com.rash1k.adressbook;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.rash1k.adressbook.data.DatabaseDescription.Contact;

class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private Cursor mCursor;
    private final ContactClickListener mListener;

    ContactsAdapter(ContactClickListener listener) {

        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.setRowId(mCursor.getLong(mCursor.getColumnIndex(Contact._ID)));
        holder.mTextView.setText(mCursor.getString(mCursor.getColumnIndex(Contact.COLUMN_NAME)));

    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    void swapCursor(Cursor data) {
        this.mCursor = data;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;
        private long rowId;

        private void setRowId(long rowId) {
            this.rowId = rowId;
        }

        private ViewHolder(View itemView) {
            super(itemView);

            mTextView = (TextView) itemView.findViewById(android.R.id.text1);

            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClick(Contact.buildContactUri(rowId));
                }
            });
        }
    }

    // Интерфейс реализуется ContactsFragment для обработки
    // прикосновения к элементу в списке RecyclerView
    interface ContactClickListener {
        void onClick(Uri contactUri);
    }
}
