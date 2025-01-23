package dev.arch3rtemp.contactexchange.ui.card;

import android.content.Context;

import dev.arch3rtemp.contactexchange.db.AppDatabase;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CardPresenter implements ICardContract.Presenter {

    private ICardContract.View view;
    private AppDatabase appDatabase;
    private CompositeDisposable compositeDisposable;

    public CardPresenter(ICardContract.View view) {
        this.view = view;
    }

    @Override
    public void onViewCreated(Context context) {
        appDatabase = AppDatabase.getDBInstance(context.getApplicationContext());
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void getContactById(int id) {
        var disposable = appDatabase.contactDao().getContactById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contact -> view.onCardLoaded(contact),
                        throwable -> view.showMessage(throwable.getLocalizedMessage()));

        compositeDisposable.add(disposable);
    }

    @Override
    public void deleteContact(int id) {
        var disposable = appDatabase.contactDao().delete(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        compositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        this.view = null;
    }
}
