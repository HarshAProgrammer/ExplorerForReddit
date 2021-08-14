package com.rackluxury.explorerforreddit;

import javax.inject.Singleton;

import dagger.Component;
import com.rackluxury.explorerforreddit.activities.AccountPostsActivity;
import com.rackluxury.explorerforreddit.activities.AccountSavedThingActivity;
import com.rackluxury.explorerforreddit.activities.AnonymousSubscriptionsActivity;
import com.rackluxury.explorerforreddit.activities.CommentActivity;
import com.rackluxury.explorerforreddit.activities.CreateMultiRedditActivity;
import com.rackluxury.explorerforreddit.activities.CustomThemeListingActivity;
import com.rackluxury.explorerforreddit.activities.CustomThemePreviewActivity;
import com.rackluxury.explorerforreddit.activities.CustomizePostFilterActivity;
import com.rackluxury.explorerforreddit.activities.CustomizeThemeActivity;
import com.rackluxury.explorerforreddit.activities.EditCommentActivity;
import com.rackluxury.explorerforreddit.activities.EditMultiRedditActivity;
import com.rackluxury.explorerforreddit.activities.EditPostActivity;
import com.rackluxury.explorerforreddit.activities.FetchRandomSubredditOrPostActivity;
import com.rackluxury.explorerforreddit.activities.FilteredPostsActivity;
import com.rackluxury.explorerforreddit.activities.FullMarkdownActivity;
import com.rackluxury.explorerforreddit.activities.GiveAwardActivity;
import com.rackluxury.explorerforreddit.activities.InboxActivity;
import com.rackluxury.explorerforreddit.activities.LinkResolverActivity;
import com.rackluxury.explorerforreddit.activities.LockScreenActivity;
import com.rackluxury.explorerforreddit.activities.RedditLoginActivity;
import com.rackluxury.explorerforreddit.activities.MainActivity;
import com.rackluxury.explorerforreddit.activities.MultiredditSelectionActivity;
import com.rackluxury.explorerforreddit.activities.PostFilterPreferenceActivity;
import com.rackluxury.explorerforreddit.activities.PostFilterUsageListingActivity;
import com.rackluxury.explorerforreddit.activities.PostGalleryActivity;
import com.rackluxury.explorerforreddit.activities.PostImageActivity;
import com.rackluxury.explorerforreddit.activities.PostLinkActivity;
import com.rackluxury.explorerforreddit.activities.PostTextActivity;
import com.rackluxury.explorerforreddit.activities.PostVideoActivity;
import com.rackluxury.explorerforreddit.activities.RPANActivity;
import com.rackluxury.explorerforreddit.activities.ReportActivity;
import com.rackluxury.explorerforreddit.activities.RulesActivity;
import com.rackluxury.explorerforreddit.activities.SearchActivity;
import com.rackluxury.explorerforreddit.activities.SearchResultActivity;
import com.rackluxury.explorerforreddit.activities.SearchSubredditsResultActivity;
import com.rackluxury.explorerforreddit.activities.SearchUsersResultActivity;
import com.rackluxury.explorerforreddit.activities.SelectUserFlairActivity;
import com.rackluxury.explorerforreddit.activities.SelectedSubredditsAndUsersActivity;
import com.rackluxury.explorerforreddit.activities.SendPrivateMessageActivity;
import com.rackluxury.explorerforreddit.activities.SettingsActivity;
import com.rackluxury.explorerforreddit.activities.SubmitCrosspostActivity;
import com.rackluxury.explorerforreddit.activities.SubredditMultiselectionActivity;
import com.rackluxury.explorerforreddit.activities.SubredditSelectionActivity;
import com.rackluxury.explorerforreddit.activities.SubscribedThingListingActivity;
import com.rackluxury.explorerforreddit.activities.SuicidePreventionActivity;
import com.rackluxury.explorerforreddit.activities.TrendingActivity;
import com.rackluxury.explorerforreddit.activities.ViewImageOrGifActivity;
import com.rackluxury.explorerforreddit.activities.ViewImgurMediaActivity;
import com.rackluxury.explorerforreddit.activities.ViewMultiRedditDetailActivity;
import com.rackluxury.explorerforreddit.activities.ViewPostDetailActivity;
import com.rackluxury.explorerforreddit.activities.ViewPrivateMessagesActivity;
import com.rackluxury.explorerforreddit.activities.ViewRedditGalleryActivity;
import com.rackluxury.explorerforreddit.activities.ViewSubredditDetailActivity;
import com.rackluxury.explorerforreddit.activities.ViewUserDetailActivity;
import com.rackluxury.explorerforreddit.activities.ViewVideoActivity;
import com.rackluxury.explorerforreddit.activities.WebViewActivity;
import com.rackluxury.explorerforreddit.bottomsheetfragments.FlairBottomSheetFragment;
import com.rackluxury.explorerforreddit.fragments.CommentsListingFragment;
import com.rackluxury.explorerforreddit.fragments.FollowedUsersListingFragment;
import com.rackluxury.explorerforreddit.fragments.InboxFragment;
import com.rackluxury.explorerforreddit.fragments.MultiRedditListingFragment;
import com.rackluxury.explorerforreddit.fragments.PostFragment;
import com.rackluxury.explorerforreddit.fragments.SidebarFragment;
import com.rackluxury.explorerforreddit.fragments.SubredditListingFragment;
import com.rackluxury.explorerforreddit.fragments.SubscribedSubredditsListingFragment;
import com.rackluxury.explorerforreddit.fragments.UserListingFragment;
import com.rackluxury.explorerforreddit.fragments.ViewImgurImageFragment;
import com.rackluxury.explorerforreddit.fragments.ViewImgurVideoFragment;
import com.rackluxury.explorerforreddit.fragments.ViewPostDetailFragment;
import com.rackluxury.explorerforreddit.fragments.ViewRPANBroadcastFragment;
import com.rackluxury.explorerforreddit.fragments.ViewRedditGalleryImageOrGifFragment;
import com.rackluxury.explorerforreddit.fragments.ViewRedditGalleryVideoFragment;
import com.rackluxury.explorerforreddit.services.DownloadMediaService;
import com.rackluxury.explorerforreddit.services.DownloadRedditVideoService;
import com.rackluxury.explorerforreddit.services.MaterialYouService;
import com.rackluxury.explorerforreddit.services.SubmitPostService;
import com.rackluxury.explorerforreddit.settings.AdvancedPreferenceFragment;
import com.rackluxury.explorerforreddit.settings.CrashReportsFragment;
import com.rackluxury.explorerforreddit.settings.CustomizeBottomAppBarFragment;
import com.rackluxury.explorerforreddit.settings.CustomizeMainPageTabsFragment;
import com.rackluxury.explorerforreddit.settings.DownloadLocationPreferenceFragment;
import com.rackluxury.explorerforreddit.settings.GesturesAndButtonsPreferenceFragment;
import com.rackluxury.explorerforreddit.settings.MainPreferenceFragment;
import com.rackluxury.explorerforreddit.settings.MiscellaneousPreferenceFragment;
import com.rackluxury.explorerforreddit.settings.NotificationPreferenceFragment;
import com.rackluxury.explorerforreddit.settings.NsfwAndSpoilerFragment;
import com.rackluxury.explorerforreddit.settings.PostHistoryFragment;
import com.rackluxury.explorerforreddit.settings.SecurityPreferenceFragment;
import com.rackluxury.explorerforreddit.settings.ThemePreferenceFragment;
import com.rackluxury.explorerforreddit.settings.TranslationFragment;
import com.rackluxury.explorerforreddit.settings.VideoPreferenceFragment;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(RedditLoginActivity redditLoginActivity);

    void inject(PostFragment postFragment);

    void inject(SubredditListingFragment subredditListingFragment);

    void inject(UserListingFragment userListingFragment);

    void inject(ViewPostDetailActivity viewPostDetailActivity);

    void inject(ViewSubredditDetailActivity viewSubredditDetailActivity);

    void inject(ViewUserDetailActivity viewUserDetailActivity);

    void inject(CommentActivity commentActivity);

    void inject(SubscribedThingListingActivity subscribedThingListingActivity);

    void inject(PostTextActivity postTextActivity);

    void inject(SubscribedSubredditsListingFragment subscribedSubredditsListingFragment);

    void inject(PostLinkActivity postLinkActivity);

    void inject(PostImageActivity postImageActivity);

    void inject(PostVideoActivity postVideoActivity);

    void inject(FlairBottomSheetFragment flairBottomSheetFragment);

    void inject(RulesActivity rulesActivity);

    void inject(CommentsListingFragment commentsListingFragment);

    void inject(SubmitPostService submitPostService);

    void inject(FilteredPostsActivity filteredPostsActivity);

    void inject(SearchResultActivity searchResultActivity);

    void inject(SearchSubredditsResultActivity searchSubredditsResultActivity);

    void inject(FollowedUsersListingFragment followedUsersListingFragment);

    void inject(SubredditSelectionActivity subredditSelectionActivity);

    void inject(EditPostActivity editPostActivity);

    void inject(EditCommentActivity editCommentActivity);

    void inject(AccountPostsActivity accountPostsActivity);

    void inject(PullNotificationWorker pullNotificationWorker);

    void inject(InboxActivity inboxActivity);

    void inject(NotificationPreferenceFragment notificationPreferenceFragment);

    void inject(LinkResolverActivity linkResolverActivity);

    void inject(SearchActivity searchActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(MainPreferenceFragment mainPreferenceFragment);

    void inject(AccountSavedThingActivity accountSavedThingActivity);

    void inject(ViewImageOrGifActivity viewGIFActivity);

    void inject(ViewMultiRedditDetailActivity viewMultiRedditDetailActivity);

    void inject(ViewVideoActivity viewVideoActivity);

    void inject(GesturesAndButtonsPreferenceFragment gesturesAndButtonsPreferenceFragment);

    void inject(CreateMultiRedditActivity createMultiRedditActivity);

    void inject(SubredditMultiselectionActivity subredditMultiselectionActivity);

    void inject(ThemePreferenceFragment themePreferenceFragment);

    void inject(CustomizeThemeActivity customizeThemeActivity);

    void inject(CustomThemeListingActivity customThemeListingActivity);

    void inject(SidebarFragment sidebarFragment);

    void inject(AdvancedPreferenceFragment advancedPreferenceFragment);

    void inject(CustomThemePreviewActivity customThemePreviewActivity);

    void inject(EditMultiRedditActivity editMultiRedditActivity);

    void inject(SelectedSubredditsAndUsersActivity selectedSubredditsAndUsersActivity);

    void inject(ReportActivity reportActivity);

    void inject(ViewImgurMediaActivity viewImgurMediaActivity);

    void inject(ViewImgurVideoFragment viewImgurVideoFragment);

    void inject(DownloadRedditVideoService downloadRedditVideoService);

    void inject(MultiRedditListingFragment multiRedditListingFragment);

    void inject(InboxFragment inboxFragment);

    void inject(ViewPrivateMessagesActivity viewPrivateMessagesActivity);

    void inject(SendPrivateMessageActivity sendPrivateMessageActivity);

    void inject(VideoPreferenceFragment videoPreferenceFragment);

    void inject(ViewRedditGalleryActivity viewRedditGalleryActivity);

    void inject(ViewRedditGalleryVideoFragment viewRedditGalleryVideoFragment);

    void inject(CustomizeMainPageTabsFragment customizeMainPageTabsFragment);

    void inject(DownloadMediaService downloadMediaService);

    void inject(DownloadLocationPreferenceFragment downloadLocationPreferenceFragment);

    void inject(SubmitCrosspostActivity submitCrosspostActivity);

    void inject(FullMarkdownActivity fullMarkdownActivity);

    void inject(SelectUserFlairActivity selectUserFlairActivity);

    void inject(SecurityPreferenceFragment securityPreferenceFragment);

    void inject(NsfwAndSpoilerFragment nsfwAndSpoilerFragment);

    void inject(CustomizeBottomAppBarFragment customizeBottomAppBarFragment);

    void inject(GiveAwardActivity giveAwardActivity);

    void inject(TranslationFragment translationFragment);

    void inject(FetchRandomSubredditOrPostActivity fetchRandomSubredditOrPostActivity);

    void inject(MiscellaneousPreferenceFragment miscellaneousPreferenceFragment);

    void inject(CustomizePostFilterActivity customizePostFilterActivity);

    void inject(PostHistoryFragment postHistoryFragment);

    void inject(PostFilterPreferenceActivity postFilterPreferenceActivity);

    void inject(PostFilterUsageListingActivity postFilterUsageListingActivity);

    void inject(SearchUsersResultActivity searchUsersResultActivity);

    void inject(MultiredditSelectionActivity multiredditSelectionActivity);

    void inject(ViewImgurImageFragment viewImgurImageFragment);

    void inject(ViewRedditGalleryImageOrGifFragment viewRedditGalleryImageOrGifFragment);

    void inject(ViewPostDetailFragment viewPostDetailFragment);

    void inject(SuicidePreventionActivity suicidePreventionActivity);

    void inject(WebViewActivity webViewActivity);

    void inject(CrashReportsFragment crashReportsFragment);

    void inject(AnonymousSubscriptionsActivity anonymousSubscriptionsActivity);

    void inject(LockScreenActivity lockScreenActivity);

    void inject(MaterialYouService materialYouService);

    void inject(RPANActivity rpanActivity);

    void inject(ViewRPANBroadcastFragment viewRPANBroadcastFragment);

    void inject(PostGalleryActivity postGalleryActivity);

    void inject(TrendingActivity trendingActivity);
}
