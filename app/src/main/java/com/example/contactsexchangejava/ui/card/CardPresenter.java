package com.example.contactsexchangejava.ui.card;

import android.content.Context;

import com.example.contactsexchangejava.db.DataManager;
import com.example.contactsexchangejava.db.models.Contact;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CardPresenter implements ICardContract.Presenter {

    private ICardContract.View view;
    private DataManager dataManager;
    private CompositeDisposable compositeDisposable;

    public CardPresenter(ICardContract.View view) {
        this.view = view;
    }

    @Override
    public void onViewCreated(Context context) {
        dataManager = new DataManager(context.getApplicationContext());
    }

    @Override
    public void getContactById(int id) {
        dataManager.loadContactById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Contact>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Contact contact) {
                        view.onCardLoaded(contact);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    public void onDestroy() {
        this.view = null;
        compositeDisposable.clear();
    }


}