package com.repos.src.orm;

import android.content.Context;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Ayman Mahgoub on 6/22/15.
 */
public abstract class GenericDao<Type extends RealmObject> {

    private static Context context;
    private Realm realm;

    private Class<Type> getRealmClass() {
        return (Class<Type>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public void insertAll(List<Type> list, Context context) {
        Realm realm = getRealmInstance(context);
        realm.beginTransaction();

        if (list != null) {

            for (Type type : list) {

                try {
                    realm.copyToRealmOrUpdate(type);
                } catch (IllegalArgumentException exception) {
                    exception.printStackTrace();
                }
            }
        }
        realm.commitTransaction();
        realm.close();
    }

    public ArrayList<Type> findAll(Context context) {
        Realm realm = getRealmInstance(context);
        realm.beginTransaction();
        Class<Type> realmClass = getRealmClass();
        RealmQuery<Type> query = realm.where(realmClass);
        RealmResults<Type> result = query.findAll();
        realm.commitTransaction();
        ArrayList<Type> results = convertRealmResults(realm, result);
        realm.close();
        return results;
    }

    public Type findById(Realm realm, int id) {
        Class<Type> realmClass = (Class<Type>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        String originalEntityClassName = realmClass.getName();
        String[] originalEntityClassNameTokens = originalEntityClassName.split("\\.");
        originalEntityClassName = originalEntityClassNameTokens[originalEntityClassNameTokens.length - 1];
        originalEntityClassName = originalEntityClassName.toLowerCase();
        Type searchResult = null;

        if (id <= 0)
            return searchResult;

        //Get By Id
        RealmQuery<Type> query = realm.where(realmClass).equalTo(originalEntityClassName + "Id", id);
        RealmResults<Type> results = query.findAll();

        if (results == null || results.size() == 0)
            return searchResult;
        Type result = results.get(0);
        return result;
    }

    private ArrayList<Type> convertRealmResults(Realm realm, RealmResults<Type> result) {
        // Parse ObjectRealm to Object
        ArrayList<Type> allObjects = new ArrayList<>();

        for (Type type : result) {
            Type converted = realm.copyFromRealm(type);
            allObjects.add(converted);
        }
        return allObjects;
    }

    private Realm getRealmInstance(Context context) {
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(context).build());
        Realm realm = Realm.getDefaultInstance();
        return realm;
    }
}
