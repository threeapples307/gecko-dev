package org.mozilla.gecko.tests;

import org.mozilla.gecko.*;

import android.view.View;
import android.widget.ListView;

import java.util.Arrays;
import java.util.ArrayList;

public class testBookmark extends AboutHomeTest {
    private static String BOOKMARK_URL;
    private static int WAIT_FOR_BOOKMARKED_TIMEOUT = 10000;

    public void testBookmark() {
        BOOKMARK_URL = getAbsoluteUrl(StringHelper.ROBOCOP_BLANK_PAGE_01_URL);
        runAboutHomeTest();
        runMenuTest();
    }

    public void runMenuTest() {
        mAsserter.is(mDatabaseHelper.isBookmark(BOOKMARK_URL), false, "Page is not bookmarked initially");
        setUpBookmark(); // loads the page, taps the star button, and waits for the "Bookmark Added" message
        waitForBookmarked(true);

        cleanUpBookmark(); // loads the page, taps the star button, and waits for the "Bookmark Removed" message
        waitForBookmarked(false);
    }

    public void runAboutHomeTest() {
        blockForGeckoReady();
        for (String url:StringHelper.DEFAULT_BOOKMARKS_URLS) {
            mAsserter.ok(mDatabaseHelper.isBookmark(url), "Checking that " + url + " is bookmarked by default", url + " is bookmarked");
        }

        mDatabaseHelper.addOrUpdateMobileBookmark(StringHelper.ROBOCOP_BLANK_PAGE_01_TITLE, BOOKMARK_URL);
        waitForBookmarked(true);

        isBookmarkDisplayed(BOOKMARK_URL);
        loadBookmark(BOOKMARK_URL);
        waitForText(StringHelper.ROBOCOP_BLANK_PAGE_01_TITLE);
        verifyPageTitle(StringHelper.ROBOCOP_BLANK_PAGE_01_TITLE);

        mDatabaseHelper.deleteBookmark(BOOKMARK_URL);
        waitForBookmarked(false);
    }

    private void waitForBookmarked(final boolean isBookmarked) {
        boolean bookmarked = waitForTest(new BooleanTest() {
            @Override
            public boolean test() {
                return (isBookmarked) ?
                    mDatabaseHelper.isBookmark(BOOKMARK_URL) :
                    !mDatabaseHelper.isBookmark(BOOKMARK_URL);
            }
        }, WAIT_FOR_BOOKMARKED_TIMEOUT);
        mAsserter.is(bookmarked, true, BOOKMARK_URL + " was " + (isBookmarked ? "added as a bookmark" : "removed from bookmarks"));
    }

    private void setUpBookmark() {
        // Bookmark a page for the test
        loadUrl(BOOKMARK_URL);
        waitForText(StringHelper.ROBOCOP_BLANK_PAGE_01_TITLE);
        toggleBookmark();
        mAsserter.is(waitForText(StringHelper.BOOKMARK_ADDED_LABEL), true, "bookmark added successfully");
    }

    private void cleanUpBookmark() {
        // Go back to the page we bookmarked
        loadUrl(BOOKMARK_URL);
        waitForText(StringHelper.ROBOCOP_BLANK_PAGE_01_TITLE);
        toggleBookmark();
        mAsserter.is(waitForText(StringHelper.BOOKMARK_REMOVED_LABEL), true, "bookmark removed successfully");
    }
}
