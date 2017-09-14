package ua.rash1k.twittersearches;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.rash1k.twittersearches.R.id.fab;

public class MainActivity extends AppCompatActivity {

    private static final String SEARCHES = "searches";
    private EditText queryEditText;
    private EditText tagEditText;
    private FloatingActionButton saveFloatingActionButton;
    private Set<String> tagList;
    private SharedPreferences savedSearches;
    private SearchesAdapter mSearchesAdapter;

    private View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String tag = ((TextView) v).getText().toString();
            String queryUrl = getString(R.string.search_URL)
                    + Uri.encode(savedSearches.getString(tag, ""), "UTF-8");

            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(queryUrl));
            startActivity(webIntent);
        }
    };
    private View.OnLongClickListener itemLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            final String tag = ((TextView) v).getText().toString();

            longClickDialog(tag);

            return true;
        }
    };
    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String query = queryEditText.getText().toString();
            String tag = tagEditText.getText().toString();

            if (!query.isEmpty() && !tag.isEmpty()) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            addTaggedSearches(tag, query);

            queryEditText.setText("");
            tagEditText.setText("");
            queryEditText.requestFocus();
        }
    };

    private void longClickDialog(final String tag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.share_edit_delete_title, tag));
        builder.setItems(R.array.dialog_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        shareSearch(tag);
                        break;
                    case 1:
                        tagEditText.setText(tag);
                        queryEditText.setText(savedSearches.getString(tag, ""));
                        break;
                    case 2:
                        deleteSearch(tag);
                        break;
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    private void deleteSearch(final String tag) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirm_message, tag));
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tagList.remove(tag);
                savedSearches.edit().remove(tag).apply();
                mSearchesAdapter.notifyDataSetChanged();
            }
        });
        builder.create().show();
    }

    private void shareSearch(String tag) {
        String url = getString(R.string.search_URL) +
                Uri.encode(savedSearches.getString(tag, ""), "UTF-8");

        Intent shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse(url));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, url));
        shareIntent.setType("text/plain");

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_search)));
    }
    private TextWatcher textWatcherListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveFAB();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queryEditText = ((TextInputLayout) findViewById(R.id.queryTextInputLayout))
                .getEditText();
        assert queryEditText != null;
        queryEditText.addTextChangedListener(textWatcherListener);

        tagEditText = ((TextInputLayout) findViewById(R.id.tagTextInputLayout)).getEditText();
        assert tagEditText != null;
        tagEditText.addTextChangedListener(textWatcherListener);

        savedSearches = getSharedPreferences(SEARCHES, MODE_PRIVATE);

        tagList = new HashSet<>(savedSearches.getAll().keySet());
        Collections.sort(new ArrayList<>(tagList), String.CASE_INSENSITIVE_ORDER);

        mSearchesAdapter = new SearchesAdapter(tagList, itemClickListener, itemLongClickListener);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mSearchesAdapter);
        recyclerView.addItemDecoration(new ItemDiver(this));

        saveFloatingActionButton = (FloatingActionButton) findViewById(fab);
        saveFloatingActionButton.setOnClickListener(saveButtonListener);

        updateSaveFAB();
    }

    private void updateSaveFAB() {
        if (queryEditText.getText().toString().isEmpty()
                && tagEditText.getText().toString().isEmpty()) {
            saveFloatingActionButton.hide();
        } else {
            saveFloatingActionButton.show();
        }
    }

    private void addTaggedSearches(String tag, String query) {

        SharedPreferences.Editor editor = savedSearches.edit();

        editor.putString(tag, query);
        editor.apply();

        if (!tagList.contains(tag)) {
            tagList.add(tag);
            Collections.sort(new ArrayList<>(tagList), String.CASE_INSENSITIVE_ORDER);
            mSearchesAdapter.notifyDataSetChanged();
        }
    }

}
