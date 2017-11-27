package com.repos.src.controllers;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Islam Salah on 9/26/17.
 *
 * https://github.com/IslamSalah
 * islamsalah007@gmail.com
 */

public class RealmController {

    private static final int SCHEMA_VERSION = 0;
    private static final String DATABASE_NAME = "sela.realm";

    public static void init(Context context) {
        Realm.init(context);
        Realm.setDefaultConfiguration(defaultConfiguration());
    }

    private static RealmConfiguration defaultConfiguration() {
        return new RealmConfiguration.Builder()
                .name(DATABASE_NAME)
                .schemaVersion(SCHEMA_VERSION)
                .deleteRealmIfMigrationNeeded()             // TODO : support migrations before release
                .build();
    }
}
