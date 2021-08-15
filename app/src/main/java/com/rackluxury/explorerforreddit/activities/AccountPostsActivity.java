package com.rackluxury.explorerforreddit.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.r0adkll.slidr.Slidr;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.explorerforreddit.ActivityToolbarInterface;
import com.rackluxury.explorerforreddit.FragmentCommunicator;
import com.rackluxury.explorerforreddit.Infinity;
import com.rackluxury.explorerforreddit.R;
import com.rackluxury.explorerforreddit.SortType;
import com.rackluxury.explorerforreddit.SortTypeSelectionCallback;
import com.rackluxury.explorerforreddit.bottomsheetfragments.PostLayoutBottomSheetFragment;
import com.rackluxury.explorerforreddit.customtheme.CustomThemeWrapper;
import com.rackluxury.explorerforreddit.events.ChangeNSFWEvent;
import com.rackluxury.explorerforreddit.events.SwitchAccountEvent;
import com.rackluxury.explorerforreddit.fragments.PostFragment;
import com.rackluxury.explorerforreddit.post.PostDataSource;
import com.rackluxury.explorerforreddit.utils.SharedPreferencesUtils;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

public class AccountPostsActivity extends BaseActivity implements SortTypeSelectionCallback,
        PostLayoutBottomSheetFragment.PostLayoutSelectionCallback, ActivityToolbarInterface {

    static final String EXTRA_USER_WHERE = "EUW";

    private static final String IS_IN_LAZY_MODE_STATE = "IILMS";
    private static final String FRAGMENT_OUT_STATE = "FOS";

    @BindView(R.id.coordinator_layout_account_posts_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.collapsing_toolbar_layout_account_posts_activity)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.appbar_layout_account_posts_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_account_posts_activity)
    Toolbar toolbar;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("post_layout")
    SharedPreferences mPostLayoutSharedPreferences;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    private boolean isInLazyMode = false;
    private String mAccessToken;
    private String mAccountName;
    private String mUserWhere;
    private Fragment mFragment;
    private Menu mMenu;
    private AppBarLayout.LayoutParams params;
    private PostLayoutBottomSheetFragment postLayoutBottomSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_account_posts);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

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

        mUserWhere = getIntent().getExtras().getString(EXTRA_USER_WHERE);
        if (mUserWhere.equals(PostDataSource.USER_WHERE_UPVOTED)) {
            toolbar.setTitle(R.string.upvoted);
        } else if (mUserWhere.equals(PostDataSource.USER_WHERE_DOWNVOTED)) {
            toolbar.setTitle(R.string.downvoted);
        } else if (mUserWhere.equals(PostDataSource.USER_WHERE_HIDDEN)) {
            toolbar.setTitle(R.string.hidden);
        } else if (mUserWhere.equals(PostDataSource.USER_WHERE_GILDED)) {
            toolbar.setTitle(R.string.gilded);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setToolbarGoToTop(toolbar);

        params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();

        postLayoutBottomSheetFragment = new PostLayoutBottomSheetFragment();

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        mAccountName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_NAME, null);

        if (savedInstanceState != null) {
            isInLazyMode = savedInstanceState.getBoolean(IS_IN_LAZY_MODE_STATE);

            mFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_OUT_STATE);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_account_posts_activity, mFragment).commit();
        } else {
            initializeFragment();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mFragment != null) {
            return ((FragmentCommunicator) mFragment).handleKeyDown(keyCode) || super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
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

    private void initializeFragment() {
        mFragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PostFragment.EXTRA_POST_TYPE, PostDataSource.TYPE_USER);
        bundle.putString(PostFragment.EXTRA_USER_NAME, mAccountName);
        bundle.putString(PostFragment.EXTRA_USER_WHERE, mUserWhere);
        bundle.putString(PostFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
        bundle.putString(PostFragment.EXTRA_ACCOUNT_NAME, mAccountName);
        bundle.putBoolean(PostFragment.EXTRA_DISABLE_READ_POSTS, true);
        mFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_account_posts_activity, mFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_posts_activity, menu);
        applyMenuItemTheme(menu);
        mMenu = menu;
        MenuItem lazyModeItem = mMenu.findItem(R.id.action_lazy_mode_account_posts_activity);
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

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        switch (item.getItemId()) {
            case R.id.action_refresh_account_posts_activity:
                if (mMenu != null) {
                    mMenu.findItem(R.id.action_lazy_mode_account_posts_activity).setTitle(R.string.action_start_lazy_mode);
                }
                if (mFragment != null) {
                    ((PostFragment) mFragment).refresh();
                }
                return true;
            case R.id.action_lazy_mode_account_posts_activity:
                storageReference.child(firebaseAuth.getUid()).child("Premium").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                MenuItem lazyModeItem = mMenu.findItem(R.id.action_lazy_mode_account_posts_activity);
                if (isInLazyMode) {
                    ((FragmentCommunicator) mFragment).stopLazyMode();
                    isInLazyMode = false;
                    lazyModeItem.setTitle(R.string.action_start_lazy_mode);
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                    collapsingToolbarLayout.setLayoutParams(params);
                } else {
                    if (((FragmentCommunicator) mFragment).startLazyMode()) {
                        isInLazyMode = true;
                        lazyModeItem.setTitle(R.string.action_stop_lazy_mode);
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                        collapsingToolbarLayout.setLayoutParams(params);
                    }
                }


                    }
                });
                storageReference.child(firebaseAuth.getUid()).child("Premium").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        premiumDialogue();


                    }
                });
                return true;
            case R.id.action_change_post_layout_account_posts_activity:
                storageReference.child(firebaseAuth.getUid()).child("Premium").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        postLayoutBottomSheetFragment.show(getSupportFragmentManager(), postLayoutBottomSheetFragment.getTag());

                    }
                });
                storageReference.child(firebaseAuth.getUid()).child("Premium").getDownloadUrl().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        premiumDialogue();


                    }
                });
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
    private void premiumDialogue() {


        FirebaseMessaging.getInstance().subscribeToTopic("upgrade_to_pro");
        new FancyGifDialog.Builder(AccountPostsActivity.this)
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

                        Intent intent = new Intent(AccountPostsActivity.this, PremiumActivity.class);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_OUT_STATE, mFragment);
        outState.putBoolean(IS_IN_LAZY_MODE_STATE, isInLazyMode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void sortTypeSelected(SortType sortType) {
        if (mFragment != null) {
            ((PostFragment) mFragment).changeSortType(sortType);
        }
    }

    @Override
    public void sortTypeSelected(String sortType) {

    }

    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        finish();
    }

    @Subscribe
    public void onChangeNSFWEvent(ChangeNSFWEvent changeNSFWEvent) {
        ((FragmentCommunicator) mFragment).changeNSFW(changeNSFWEvent.nsfw);
    }

    @Override
    public void postLayoutSelected(int postLayout) {
        if (mFragment != null) {
            mPostLayoutSharedPreferences.edit().putInt(SharedPreferencesUtils.POST_LAYOUT_USER_POST_BASE + mAccountName, postLayout).apply();
            ((FragmentCommunicator) mFragment).changePostLayout(postLayout);
        }
    }

    @Override
    public void onLongPress() {
        if (mFragment != null) {
            ((PostFragment) mFragment).goBackToTop();
        }
    }
}
