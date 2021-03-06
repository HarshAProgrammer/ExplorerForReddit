package com.rackluxury.explorerforreddit.subscribeduser;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.rackluxury.explorerforreddit.RedditDataRoomDatabase;

public class SubscribedUserRepository {
    private final SubscribedUserDao mSubscribedUserDao;
    private final LiveData<List<SubscribedUserData>> mAllSubscribedUsers;
    private final LiveData<List<SubscribedUserData>> mAllFavoriteSubscribedUsers;

    SubscribedUserRepository(RedditDataRoomDatabase redditDataRoomDatabase, String accountName) {
        mSubscribedUserDao = redditDataRoomDatabase.subscribedUserDao();
        mAllSubscribedUsers = mSubscribedUserDao.getAllSubscribedUsers(accountName);
        mAllFavoriteSubscribedUsers = mSubscribedUserDao.getAllFavoriteSubscribedUsers(accountName);
    }

    LiveData<List<SubscribedUserData>> getAllSubscribedSubreddits() {
        return mAllSubscribedUsers;
    }

    LiveData<List<SubscribedUserData>> getAllFavoriteSubscribedSubreddits() {
        return mAllFavoriteSubscribedUsers;
    }

    public void insert(SubscribedUserData subscribedUserData) {
        new SubscribedUserRepository.insertAsyncTask(mSubscribedUserDao).execute(subscribedUserData);
    }

    private static class insertAsyncTask extends AsyncTask<SubscribedUserData, Void, Void> {

        private final SubscribedUserDao mAsyncTaskDao;

        insertAsyncTask(SubscribedUserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final SubscribedUserData... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
