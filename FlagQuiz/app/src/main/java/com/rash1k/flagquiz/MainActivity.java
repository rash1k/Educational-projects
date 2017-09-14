package com.rash1k.flagquiz;

        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.ActivityInfo;
        import android.content.pm.PackageManager;
        import android.content.res.Configuration;
        import android.os.Bundle;
        import android.preference.PreferenceActivity;
        import android.preference.PreferenceManager;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.Toast;
        import java.util.Set;

public class MainActivity extends AppCompatActivity {

//   region
    public static final String CHOICES = "pref_numberOfChoices";
    public static final String REGIONS = "pref_regionsToInclude";
    private boolean phoneDevice = true;
    private boolean preferencesChanged = true;
//endregion

    private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                    preferencesChanged = true;

                    MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().
                            findFragmentById(R.id.quizFragment);

                    if (s.equals(CHOICES)) {
                        quizFragment.updateGuessRows(sharedPreferences);
                        quizFragment.resetQuiz(sharedPreferences);
                    } else if (s.equals(REGIONS)) {
                        Set<String> regions = sharedPreferences.getStringSet(REGIONS, null);
                        if (regions != null && regions.size() > 0) {
                            quizFragment.updateRegions(sharedPreferences);
                            quizFragment.resetQuiz(sharedPreferences);
                        } else {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (regions != null) {
                                regions.add(getString(R.string.default_region));
                                editor.putStringSet(REGIONS, regions);
                                editor.apply();

                                Toast.makeText(MainActivity.this, R.string.default_region_message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    Toast.makeText(MainActivity.this, R.string.restarting_quiz, Toast.LENGTH_SHORT).show();
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//Установка значений по умолчанию
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

// Регистрация слушателя
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(preferencesChangeListener);

//Определение размера экрана
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE
                || screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            phoneDevice = false;
        }
// На телефоне разрешена только портретная ориентация
        if (phoneDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged) {

            MainActivityFragment quizFragment = (MainActivityFragment) getSupportFragmentManager().
                    findFragmentById(R.id.quizFragment);

            quizFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateRegions(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.resetQuiz();
            preferencesChanged = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent preferencesIntent = new Intent(this, PreferenceActivity.class);
            startActivity(preferencesIntent);
            return true;
        } else return false;
    }


}