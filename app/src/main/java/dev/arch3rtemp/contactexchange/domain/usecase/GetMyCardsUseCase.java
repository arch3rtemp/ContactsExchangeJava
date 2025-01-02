package dev.arch3rtemp.contactexchange.domain.usecase;

import dev.arch3rtemp.contactexchange.domain.model.Card;
import dev.arch3rtemp.contactexchange.domain.repository.CardRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public final class GetMyCardsUseCase {

    private final CardRepository repository;

    @Inject
    public GetMyCardsUseCase(CardRepository repository) {
        this.repository = repository;
    }

    public Observable<List<Card>> invoke() {
        return repository.getMyCards();
    }
}
