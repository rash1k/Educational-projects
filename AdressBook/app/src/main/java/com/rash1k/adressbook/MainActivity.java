package com.rash1k.adressbook;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends BaseActivity
        implements ContactsFragment.ContactsFragmentListener,
        DetailContactFragment.DetailContactFragmentListener,
        AddEditFragment.AddEditFragmentListener {

    public static final String CONTACT_URI = "contact_uri"; //Ключ для сохранения Uri контакта в Bundle
    private static final String TAG = "MainActivity";
    private ContactsFragment mContactsFragment; // Вывод списка контактов
    private CollapsingToolbarLayout mCollapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);

//          Если активность восстанавливается или создается заново при изменение конфигурации
        if (savedInstanceState == null && findViewById(R.id.fragmentContainer) != null) {
            mContactsFragment = new ContactsFragment(); // Создаем ContactFragment

//            Добавляем фрагмента в FrameLayout
            getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .add(R.id.fragmentContainer, mContactsFragment, "ContactsFragment")
                    .commit();
        } else {
            mContactsFragment = (ContactsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.contactsFragment);
        }
    }


    //      Отображение фрагмента(DetailFragment) для выбраного контакта
    @Override
    public void onContactSelected(Uri uriContact) {
        if (findViewById(R.id.fragmentContainer) != null) { //Телефон
            displayContact(uriContact, R.id.fragmentContainer);
        } else {                                            // Планшет
            getSupportFragmentManager().popBackStack();
            displayContact(uriContact, R.id.rightPaneContainer);
        }
    }

    //     Отображение информации о контакте
    private void displayContact(Uri uriContact, int fragmentId) {
        DetailContactFragment detailContactFragment = new DetailContactFragment();

//        Передача Uri в аргументе фрагмента
        Bundle bundleUri = new Bundle();
        bundleUri.putParcelable(CONTACT_URI, uriContact);
        detailContactFragment.setArguments(bundleUri);

//        Отображение фрагмента в активности путем замены предыдущего фрагмента
        getSupportFragmentManager().beginTransaction()
                .replace(fragmentId, detailContactFragment, "ContactFragment")
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

    }

    //   Отображение AddEditFragment для добавления нового контакта
    @Override
    public void onAddContact() {

        if (findViewById(R.id.fragmentContainer) != null) {
            displayAddEditFragment(R.id.fragmentContainer, null);
        } else {
            displayAddEditFragment(R.id.rightPaneContainer, null);
        }

    }

    // Отображается фрагмент для добавления нового или изменения существующего
    private void displayAddEditFragment(int fragmentId, Uri contactUri) {
        AddEditFragment addEditFragment = new AddEditFragment();
//            При изменении передается Uri

        if (contactUri != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(CONTACT_URI, contactUri);
            addEditFragment.setArguments(bundle);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentId, addEditFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    //    Запуск Активности настроек
    @Override
    public void onSettingsSelected() {


        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void onContactDeleted() {
//        Удаление с вершины стека
        getSupportFragmentManager().popBackStack();
        mContactsFragment.updateContactList();
    }

    @Override
    public void onEditContact(Uri contactUri) {
        if (findViewById(R.id.fragmentContainer) != null) {
            displayAddEditFragment(R.id.fragmentContainer, contactUri);
        } else {
            displayAddEditFragment(R.id.rightPaneContainer, contactUri);
        }
    }

    //    Завершение добавления или редактирования контакта
    @Override
    public void onAddEditCompleted(Uri contactUri) {
//        Удаление из вершины стека возврата AddEditFragment
        getSupportFragmentManager().popBackStack();
//        Обновление списка контактов
        mContactsFragment.updateContactList();

        if (findViewById(R.id.fragmentContainer) == null) {
            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

    @Override
    public void onFindContact() {

        /*if (accessLocation) {
            startActivity(new Intent(this, MapsActivity.class));
        } else {
            onSettingsSelected();
        }*/
        startActivity(new Intent(this, MapsActivity.class));
    }

    @Override
    public void onLogoutSelected() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }
}

