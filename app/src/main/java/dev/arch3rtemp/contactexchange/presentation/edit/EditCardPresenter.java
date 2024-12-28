package dev.arch3rtemp.contactexchange.presentation.edit;

import dev.arch3rtemp.contactexchange.R;
import dev.arch3rtemp.contactexchange.domain.model.Card;
import dev.arch3rtemp.contactexchange.domain.usecase.GetCardByIdUseCase;
import dev.arch3rtemp.contactexchange.domain.usecase.UpdateCardUseCase;
import dev.arch3rtemp.contactexchange.presentation.util.CardBlankChecker;

import javax.inject.Inject;

import dev.arch3rtemp.ui.base.BasePresenter;
import dev.arch3rtemp.ui.util.StringResourceManager;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EditCardPresenter extends BasePresenter<EditCardContract.EditCardEvent, EditCardContract.EditCardEffect, EditCardContract.EditCardState> {

    private final GetCardByIdUseCase getCardById;
    private final UpdateCardUseCase updateCard;
    private final StringResourceManager resourceManager;
    private final CardBlankChecker cardBlankChecker;

    @Inject
    public EditCardPresenter(GetCardByIdUseCase getCardById, UpdateCardUseCase updateCard, StringResourceManager resourceManager, CardBlankChecker cardBlankChecker) {
        this.getCardById = getCardById;
        this.updateCard = updateCard;
        this.resourceManager = resourceManager;
        this.cardBlankChecker = cardBlankChecker;
    }

    @Override
    protected EditCardContract.EditCardState createInitialState() {
        return new EditCardContract.EditCardState.Idle();
    }

    @Override
    protected void handleEvent(EditCardContract.EditCardEvent event) {
        if (event instanceof EditCardContract.EditCardEvent.OnCardLoad onCardLoad) {
            getCard(onCardLoad.id());
        } else if (event instanceof EditCardContract.EditCardEvent.OnUpdateButtonPressed onSaveButtonPressed) {
            updateCard(onSaveButtonPressed.card());
        }
    }

    private void getCard(int id) {
        var disposable = getCardById.invoke(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((card) -> {
                    setState((Card) -> new EditCardContract.EditCardState.Success(card));
                }, throwable -> {
                    setState((String) -> new EditCardContract.EditCardState.Error());
                    setEffect(() -> new EditCardContract.EditCardEffect.ShowError(throwable.getLocalizedMessage()));
                });
        disposables.add(disposable);
    }

    private void updateCard(Card newCard) {
        if (cardBlankChecker.check(newCard)) {
            if (getCurrentState() instanceof EditCardContract.EditCardState.Success current) {
                var mergedCard = mergeCard(current.card(), newCard);

                var disposable = updateCard.invoke(mergedCard)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            setEffect(EditCardContract.EditCardEffect.Finish::new);
                        }, throwable -> {
                            setEffect(() -> new EditCardContract.EditCardEffect.ShowError(throwable.getLocalizedMessage()));
                        });
                disposables.add(disposable);

            } else {
                setEffect(() -> new EditCardContract.EditCardEffect.ShowError(resourceManager.string(R.string.msg_could_not_save)));
            }
        } else {
            setEffect(() -> new EditCardContract.EditCardEffect.ShowError(resourceManager.string(R.string.msg_all_fields_required)));
        }
    }

    private Card mergeCard(Card current, Card newCard) {
        return new Card(
                current.id(),
                newCard.name(),
                newCard.job(),
                newCard.position(),
                newCard.email(),
                newCard.phoneMobile(),
                newCard.phoneOffice(),
                newCard.createDate(),
                current.color(),
                current.isMy()
        );
    }
}
