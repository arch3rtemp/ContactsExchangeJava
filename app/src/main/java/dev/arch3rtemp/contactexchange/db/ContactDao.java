package dev.arch3rtemp.contactexchange.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import dev.arch3rtemp.contactexchange.db.model.Contact;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM contact_table WHERE is_my == 0 ORDER BY name ASC")
    Observable<List<Contact>> selectScannedContacts();

    @Query("SELECT * FROM contact_table WHERE is_my == 1 ORDER BY job ASC")
    Observable<List<Contact>> selectMyCards();

    @Query("SELECT * FROM contact_table WHERE id = :id LIMIT 1")
    Observable<Contact> selectContactById(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insert(Contact contact);

    @Update
    Completable update(Contact contact);

    @Query("DELETE FROM contact_table WHERE id == :id")
    Completable delete(int id);
}
