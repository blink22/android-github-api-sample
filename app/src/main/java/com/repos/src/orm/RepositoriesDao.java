package com.repos.src.orm;

import com.repos.src.models.Repository;

/**
 * Created by Ayman Mahgoub on 6/22/15.
 */
public class RepositoriesDao extends GenericDao<Repository> {

    private static RepositoriesDao repositoriesDao;

    private RepositoriesDao() {
        super();
    }

    public static RepositoriesDao getInstance() {

        if (repositoriesDao == null)
            repositoriesDao = new RepositoriesDao();
        return repositoriesDao;
    }


}
