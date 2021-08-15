package com.rackluxury.explorerforreddit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.r0adkll.slidr.Slidr;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.explorerforreddit.ActivityToolbarInterface;
import com.rackluxury.explorerforreddit.FragmentCommunicator;
import com.rackluxury.explorerforreddit.Infinity;
import com.rackluxury.explorerforreddit.MarkPostAsReadInterface;
import com.rackluxury.explorerforreddit.R;
import com.rackluxury.explorerforreddit.RedditDataRoomDatabase;
import com.rackluxury.explorerforreddit.SortType;
import com.rackluxury.explorerforreddit.SortTypeSelectionCallback;
import com.rackluxury.explorerforreddit.bottomsheetfragments.PostLayoutBottomSheetFragment;
import com.rackluxury.explorerforreddit.bottomsheetfragments.SortTimeBottomSheetFragment;
import com.rackluxury.explorerforreddit.bottomsheetfragments.SortTypeBottomSheetFragment;
import com.rackluxury.explorerforreddit.customtheme.CustomThemeWrapper;
import com.rackluxury.explorerforreddit.events.RefreshMultiRedditsEvent;
import com.rackluxury.explorerforreddit.fragments.PostFragment;
import com.rackluxury.explorerforreddit.multireddit.DeleteMultiReddit;
import com.rackluxury.explorerforreddit.multireddit.MultiReddit;
import com.rackluxury.explorerforreddit.post.Post;
import com.rackluxury.explorerforreddit.post.PostDataSource;
import com.rackluxury.explorerforreddit.readpost.InsertReadPost;
import com.rackluxury.explorerforreddit.utils.SharedPreferencesUtils;
import com.rackluxury.explorerforreddit.utils.Utils;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import retrofit2.Retrofit;

public class ViewMultiRedditDetailActivity extends BaseActivity implements SortTypeSelectionCallback,
        PostLayoutBottomSheetFragment.PostLayoutSelectionCallback, ActivityToolbarInterface, MarkPostAsReadInterface {

    public static final String EXTRA_MULTIREDDIT_DATA = "EMD";
    public static final String EXTRA_MULTIREDDIT_PATH = "EMP";

    private static final String FRAGMENT_OUT_STATE_KEY = "FOSK";
    private static final String IS_IN_LAZY_MODE_STATE = "IILMS";

    @BindView(R.id.coordinator_layout_view_multi_reddit_detail_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_view_multi_reddit_detail_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar_layout_view_multi_reddit_detail_activity)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar_view_multi_reddit_detail_activity)
    Toolbar toolbar;
    @Inject
    @Named("no_oauth")
    Retrofit mRetrofit;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("sort_type")
    SharedPreferences mSortTypeSharedPreferences;
    @Inject
    @Named("post_layout")
    SharedPreferences mPostLayoutSharedPreferences;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;
    private String mAccessToken;
    private String mAccountName;
    private String multiPath;
    private boolean isInLazyMode = false;
    private Fragment mFragment;
    private Menu mMenu;
    private AppBarLayout.LayoutParams params;
    private SortTypeBottomSheetFragment sortTypeBottomSheetFragment;
    private SortTimeBottomSheetFragment sortTimeBottomSheetFragment;
    private PostLayoutBottomSheetFragment postLayoutBottomSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_multi_reddit_detail);

        ButterKnife.bind(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            Slidr.attach(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();

            if (isChangeStatusBarIconColor()) {
                addOnOffsetChangedListener(appBarLayout);
            }

            if (isImmersiveInterface()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    coordinatorLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                } else {
                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }
                adjustToolbar(toolbar);
            }
        }

        MultiReddit multiReddit = getIntent().getParcelableExtra(EXTRA_MULTIREDDIT_DATA);
        if (multiReddit == null) {
            multiPath = getIntent().getStringExtra(EXTRA_MULTIREDDIT_PATH);
            if (multiPath != null) {
                toolbar.setTitle(multiPath.substring(multiPath.lastIndexOf("/", multiPath.length() - 2) + 1));
            } else {
                Toast.makeText(this, R.string.error_getting_multi_reddit_data, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            multiPath = multiReddit.getPath();
            toolbar.setTitle(multiReddit.getDisplayName());
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarGoToTop(toolbar);

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        mAccountName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_NAME, null);

        if (savedInstanceState != null) {
            isInLazyMode = savedInstanceState.getBoolean(IS_IN_LAZY_MODE_STATE);

            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_OUT_STATE_KEY);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_view_multi_reddit_detail_activity, mFragment).commit();
        } else {
            initializeFragment();
        }

        sortTypeBottomSheetFragment = new SortTypeBottomSheetFragment();
        Bundle bottomSheetBundle = new Bundle();
        bottomSheetBundle.putBoolean(SortTypeBottomSheetFragment.EXTRA_NO_BEST_TYPE, true);
        sortTypeBottomSheetFragment.setArguments(bottomSheetBundle);

        sortTimeBottomSheetFragment = new SortTimeBottomSheetFragment();

        postLayoutBottomSheetFragment = new PostLayoutBottomSheetFragment();

        params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
    }

    private void initializeFragment() {
        mFragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PostFragment.EXTRA_NAME, multiPath);
        bundle.putInt(PostFragment.EXTRA_POST_TYPE, PostDataSource.TYPE_MULTI_REDDIT);
        bundle.putString(PostFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
        bundle.putString(PostFragment.EXTRA_ACCOUNT_NAME, mAccountName);
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_view_multi_reddit_detail_activity, mFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_multi_reddit_detail_activity, menu);
        applyMenuItemTheme(menu);
        mMenu = menu;
        MenuItem lazyModeItem = mMenu.findItem(R.id.action_lazy_mode_view_multi_reddit_detail_activity);
        if (isInLazyMode) {
            lazyModeItem.setTitle(R.string.action_stop_lazy_mode);
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
            collapsingToolbarLayout.setLayoutParams(params);
        } else {
            lazyModeItem.setTitle(R.string.action_start_lazy_mode);
            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
            collapsingToolbarLayout.setLayoutParams(params);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_sort_view_multi_reddit_detail_activity) {
            sortTypeBottomSheetFragment.show(getSupportFragmentManager(), sortTypeBottomSheetFragment.getTag());
            return true;
        } else if (itemId == R.id.action_search_view_multi_reddit_detail_activity) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_refresh_view_multi_reddit_detail_activity) {
            if (mMenu != null) {
                mMenu.findItem(R.id.action_lazy_mode_view_multi_reddit_detail_activity).setTitle(R.string.action_start_lazy_mode);
            }
            if (mFragment instanceof FragmentCommunicator) {
                ((FragmentCommunicator) mFragment).refresh();
            }
            return true;
        } else if (itemId == R.id.action_lazy_mode_view_multi_reddit_detail_activity) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();
            storageReference.child(firebaseAuth.getUid()).child("Premium").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    MenuItem lazyModeItem = mMenu.findItem(R.id.action_lazy_mode_view_multi_reddit_detail_activity);
                    if (isInLazyMode) {
                        isInLazyMode = false;
                        ((FragmentCommunicator) mFragment).stopLazyMode();
                        lazyModeItem.setTitle(R.string.action_start_lazy_mode);
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                        collapsingToolbarLayout.setLayoutParams(params);
                    } else {
                        isInLazyMode = true;
                        if (((FragmentCommunicator) mFragment).startLazyMode()) {
                            lazyModeItem.setTitle(R.string.action_stop_lazy_mode);
                            appBarLayout.setExpanded(false);
                            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                            collapsingToolbarLayout.setLayoutParams(params);
                        } else {
                            isInLazyMode = false;
                        }
                    }

                }
            });
            storageReference.child(firebaseAuth.getUid()).child("Premium").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseMessaging.getInstance().subscribeToTopic("upgrade_to_pro");
                    new FancyGifDialog.Builder(ViewMultiRedditDetailActivity.this)
                            .setTitle("Upgrade to pro.")
                            .setMessage("Upgrade to Pro to access a lot of cool features.")
                            .setTitleTextColor(R.color.colorHeadline)
                            .setDescriptionTextColor(R.color.colorDescription)
                            .setNegativeBtnText("Cancel")
                            .setPositiveBtnBackground(R.color.colorYes)
                            .setPositiveBtnText("Ok")
                            .setNegativeBtnBackground(R.color.colorNo)
                            .setGifResource(R.drawable.premium_gif)
                            .isCancellable(true)
                            .OnPositiveClicked(new FancyGifDialogListener() {
                                @Override
                                public void OnClick() {

                                    Intent intent = new Intent(ViewMultiRedditDetailActivity.this, PremiumActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .OnNegativeClicked(new FancyGifDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            })
                            .build();


                }
            });

            return true;
        } else if (itemId == R.id.action_change_post_layout_view_multi_reddit_detail_activity) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReference();


            storageReference.child(firebaseAuth.getUid()).child("Premium").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {


            postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());

                }
            });
            storageReference.child(firebaseAuth.getUid()).child("Premium").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseMessaging.getInstance().subscribeToTopic("upgrade_to_pro");
                    new FancyGifDialog.Builder(ViewMultiRedditDetailActivity.this)
                            .setTitle("Upgrade to pro.")
                            .setMessage("Upgrade to Pro to access a lot of cool features.")
                            .setTitleTextColor(R.color.colorHeadline)
                            .setDescriptionTextColor(R.color.colorDescription)
                            .setNegativeBtnText("Cancel")
                            .setPositiveBtnBackground(R.color.colorYes)
                            .setPositiveBtnText("Ok")
                            .setNegativeBtnBackground(R.color.colorNo)
                            .setGifResource(R.drawable.premium_gif)
                            .isCancellable(true)
                            .OnPositiveClicked(new FancyGifDialogListener() {
                                @Override
                                public void OnClick() {

                                    Intent intent = new Intent(ViewMultiRedditDetailActivity.this, PremiumActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .OnNegativeClicked(new FancyGifDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            })
                            .build();


                }
            });
            return true;
        } else if (itemId == R.id.action_edit_view_multi_reddit_detail_activity) {
            Intent editIntent = new Intent(this, EditMultiRedditActivity.class);
            editIntent.putExtra(EditMultiRedditActivity.EXTRA_MULTI_PATH, multiPath);
            startActivity(editIntent);
            return true;
        } else if (itemId == R.id.action_delete_view_multi_reddit_detail_activity) {
            new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.delete_multi_reddit_dialog_message)
                    .setPositiveButton(R.string.delete, (dialogInterface, i)
                            -> DeleteMultiReddit.deleteMultiReddit(mExecutor, new Handler(), mOauthRetrofit, mRedditDataRoomDatabase,
                            mAccessToken, mAccountName, multiPath, new DeleteMultiReddit.DeleteMultiRedditListener() {
                                @Override
                                public void success() {
                                    Toast.makeText(ViewMultiRedditDetailActivity.this,
                                            R.string.delete_multi_reddit_success, Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().post(new RefreshMultiRedditsEvent());
                                    finish();
                                }

                                @Override
                                public void failed() {
                                    Toast.makeText(ViewMultiRedditDetailActivity.this,
                                            R.string.delete_multi_reddit_failed, Toast.LENGTH_SHORT).show();
                                }
                            }))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_IN_LAZY_MODE_STATE, isInLazyMode);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_OUT_STATE_KEY, mFragment);
    }

    @Override
    public void sortTypeSelected(SortType sortType) {
        ((PostFragment) mFragment).changeSortType(sortType);
        displaySortType();
    }

    @Override
    public void sortTypeSelected(String sortType) {
        Bundle bundle = new Bundle();
        bundle.putString(SortTimeBottomSheetFragment.EXTRA_SORT_TYPE, sortType);
        sortTimeBottomSheetFragment.setArguments(bundle);
        sortTimeBottomSheetFragment.show(getSupportFragmentManager(), sortTimeBottomSheetFragment.getTag());
    }

    @Override
    public void postLayoutSelected(int postLayout) {
        if (mFragment != null) {
            mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_MULTI_REDDIT_POST_BASE + multiPath, postLayout).apply();
            ((FragmentCommunicator) mFragment).changePostLayout(postLayout);
        }
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return mSharedPreferences;
    }

    @Override
    protected CustomThemeWrapper getCustomThemeWrapper() {
        return mCustomThemeWrapper;
    }

    @Override
    protected void applyCustomTheme() {
        coordinatorLayout.setBackgroundColor(mCustomThemeWrapper.getBackgroundColor());
        applyAppBarLayoutAndToolbarTheme(appBarLayout, toolbar);
    }

    @Override
    public void onLongPress() {
        if (mFragment != null) {
            ((PostFragment) mFragment).goBackToTop();
        }
    }

    @Override
    public void displaySortType() {
        if (mFragment != null) {
            SortType sortType = ((PostFragment) mFragment).getSortType();
            Utils.displaySortTypeInToolbar(sortType, toolbar);
        }
    }

    @Override
    public void markPostAsRead(Post post) {
        InsertReadPost.insertReadPost(mRedditDataRoomDatabase, mExecutor, mAccountName, post.getId());
    }
}
