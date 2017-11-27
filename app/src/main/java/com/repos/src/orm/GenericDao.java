package com.repos.src.orm;

import android.support.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Ayman Mahgoub on 6/22/15.
 */
public abstract class GenericDao<Type extends RealmObject> {

    private Class<Type> retrieveItemClass() {
        return (Class<Type>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }


    public void insertOrUpdate(List<Type> items) {
        if (items == null || items.isEmpty())
            return;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realmParam -> {
            for (Type item : items) {
                realmParam.insertOrUpdate(item);
            }
        });
        realm.close();
    }

    public void insertOrUpdate(Type item) {
        if (item == null)
            return;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realmParam -> {
            realmParam.insertOrUpdate(item);
        });
        realm.close();
    }

    /**
     * Used when it's required to find items which has certain value for a certain column.
     * using null for fieldName or fieldValue returns all items.
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public List<Type> find(@Nullable String fieldName, @Nullable Long fieldValue) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Type> results = findAllResults(realm, fieldName, fieldValue);
        List<Type> items = extractItemsFromResults(realm, results);
        realm.close();
        return items;
    }

    public List<Type> findAll() {
        return find(null, null);
    }

    public void deleteAll(@Nullable String fieldName, @Nullable Long fieldValue) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Type> results = findAllResults(realm, fieldName, fieldValue);

        if (!results.isEmpty()) {
            realm.executeTransaction(realmParam -> {
                results.deleteAllFromRealm();
            });
        }
        realm.close();
    }

    private RealmResults<Type> findAllResults(Realm realm, @Nullable String fieldName, @Nullable Long fieldValue) {
        RealmQuery<Type> query = realm.where(retrieveItemClass());

        if (fieldName != null && fieldValue != null) {
            query.equalTo(fieldName, fieldValue);
        }
        return query.findAll();
    }

    protected List<Type> extractItemsFromResults(Realm realm, RealmResults<Type> results) {
        List<Type> items = new ArrayList<Type>();

        for (Type result : results) {
            Type item = realm.copyFromRealm(result);
            extractInverselyLinkedData(realm, item, result);
            items.add(item);
        }
        return items;
    }

    abstract void extractInverselyLinkedData(Realm realm, Type item, Type result);

    public int getNextId() {
        try {
            Realm realm = Realm.getDefaultInstance();
            Number number = realm.where(retrieveItemClass()).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
}
