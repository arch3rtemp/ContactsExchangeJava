package dev.arch3rtemp.contactexchange.data.repository;

import androidx.core.os.OperationCanceledException;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;

import org.json.JSONException;

import javax.inject.Inject;

import dev.arch3rtemp.contactexchange.R;
import dev.arch3rtemp.contactexchange.data.mapper.JsonToCardMapper;
import dev.arch3rtemp.contactexchange.domain.model.Card;
import dev.arch3rtemp.contactexchange.domain.repository.ScannerRepository;
import dev.arch3rtemp.ui.util.StringResourceManager;
import io.reactivex.rxjava3.core.Single;

public class ScannerRepositoryImpl implements ScannerRepository {

    private final GmsBarcodeScanner scanner;
    private final JsonToCardMapper mapper;
    private final StringResourceManager resourceManager;

    @Inject
    public ScannerRepositoryImpl(GmsBarcodeScanner scanner, JsonToCardMapper mapper, StringResourceManager resourceManager) {
        this.scanner = scanner;
        this.mapper = mapper;
        this.resourceManager = resourceManager;
    }

    @Override
    public Single<Card> scan() {
        return Single.create(emitter -> {
            scanner.startScan()
                    .addOnSuccessListener(barcode -> {
                        String rawValue = barcode.getRawValue();
                        try {
                            if (!emitter.isDisposed()) {
                                emitter.onSuccess(mapper.fromJson(rawValue));
                            }
                        } catch (JSONException e) {
                            if (!emitter.isDisposed()) {
                                emitter.onError(e);
                            }
                        }
                    }).addOnCanceledListener(() -> {
                        if (!emitter.isDisposed()) {
                            emitter.onError(new OperationCanceledException(resourceManager.string(R.string.msg_scan_cancelled)));
                        }
                    }).addOnFailureListener(e -> {
                        if (!emitter.isDisposed()) {
                            emitter.onError(e);
                        }
                    });
        });
    }

}
