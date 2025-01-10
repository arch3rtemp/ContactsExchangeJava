package dev.arch3rtemp.contactexchange.presentation.ui.edit;

import dev.arch3rtemp.contactexchange.domain.model.Card;
import dev.arch3rtemp.contactexchange.presentation.model.CardUi;
import dev.arch3rtemp.ui.base.UiEffect;
import dev.arch3rtemp.ui.base.UiEvent;
import dev.arch3rtemp.ui.base.UiState;

public interface EditCardContract {

    sealed interface EditCardEvent extends UiEvent {
        record OnCardLoad(int id) implements EditCardEvent {}
        record OnUpdateButtonPress(Card card) implements EditCardEvent {}
    }

    sealed interface EditCardEffect extends UiEffect {
        record ShowError(String message) implements EditCardEffect {}
        record NavigateUp() implements EditCardEffect {}
    }

    sealed interface EditCardState extends UiState {
        record Idle() implements EditCardState {}
        record Loading() implements EditCardState {}
        record Error() implements EditCardState {}
        record Success(CardUi card) implements EditCardState {}
    }
}
