package FileSearch.tests;


import FileSearch.Search;
import FileSearch.SearchOptions;
import FileSearch.SearchResultListener;
import FileSearch.tools.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SearchTest {

    private Search search;
    private SearchOptions searchOptions;
    private String lastReceivedStatus;
    private Result lastReceivedResult;
    private Search lastReceivedSearch;
    private int finishedCount;
    private int receivedResultCount;
    private int statusUpdateCount;
    private int updateResultCount;

    @Before
    public void setUp() {
        lastReceivedStatus = null;
        lastReceivedResult = null;
        lastReceivedSearch = null;
        finishedCount = 0;
        receivedResultCount = 0;
        statusUpdateCount = 0;
        updateResultCount = 0;
        searchOptions = new SearchOptions();
        search = new Search(searchOptions);
        search.addListener(createListener());
    }

    @After
    public void tearDown() {
    }

    private SearchResultListener createListener() {
        return new SearchResultListener() {
            @Override
            public void onFinishedResults(Search search) {
                lastReceivedSearch = search;
                finishedCount += 1;
            }

            @Override
            public void onReceivedResult(Search search, Result result) {
                lastReceivedSearch = search;
                lastReceivedResult = result;
                receivedResultCount += 1;
            }

            @Override
            public void onStatusUpdate(String status) {
                lastReceivedStatus = status;
                statusUpdateCount += 1;
            }

            @Override
            public void onResultsUpdate(Search search) {
                lastReceivedSearch = search;
                updateResultCount += 1;
            }
        };
    }

    private void checkResults(String msg, int receivedCount, int statusCount, int updateCount, int finishedCountCheck) {
        assertEquals(String.format("Should have had corrected received count. %s", msg), receivedCount, receivedResultCount);
        assertEquals(String.format("Should have had corrected status count. %s", msg), statusCount, statusUpdateCount);
        assertEquals(String.format("Should have had corrected update count. %s", msg), updateCount, updateResultCount);
        assertEquals(String.format("Should have had corrected finished count. %s", msg), finishedCountCheck, finishedCount);
    }

    @Test
    public void testAddResult() {
        lastReceivedSearch = null;
        lastReceivedResult = null;
        String path = "/foo/bar.txt";
        assertEquals("Should start with no results.", 0, search.getResults().size());;
        checkResults("Should have received no result evenst.", 0, 0, 0, 0);
        Result result = new MockResult(path);
        search.addResult(result);
        assertEquals("Should have added a result.", 1, search.getResults().size());
        checkResults("Should have received 1 result event.", 1, 0, 0, 0);
        assertSame("Add should event have received a search.", search, lastReceivedSearch);
        assertSame("Add should have received the correct result.", result, lastReceivedResult);
    }

    @Test
    public void testClear() {
        String path = "/foo/bar.txt";
        search.addResult(new MockResult(path));
        assertEquals("Should have added a result.", 1, search.getResults().size());
        lastReceivedSearch = null;
        search.clear();
        assertEquals("Should have removed the result.", 0, search.getResults().size());
        checkResults("Should have received 1 result update event.", 1, 0, 1, 0);
        assertSame("Clear update event have received a search.", search, lastReceivedSearch);
    }

    @Test
    public void testFinish() {
        String path = "/foo/bar.txt";
        search.addResult(new MockResult(path));
        assertEquals("Should have added a result.", 1, search.getResults().size());
        lastReceivedSearch = null;
        search.finished();
        checkResults("Should have received 1 finished event.", 1, 0, 0, 1);
        assertSame("Finish event have received a search.", search, lastReceivedSearch);
        assertTrue("Search should report as stopped", search.isStopped());
    }

    @Test
    public void testStatus() {
        String status = "Foobar.";
        search.currentStatus(status);
        checkResults("Should have received 1 status event.", 0, 1, 0, 0);
        assertEquals("Status event have received the message.", status, lastReceivedStatus);
    }

    @Test
    public void testCancel() {
        String path = "/foo/bar.txt";
        search.addResult(new MockResult(path));
        lastReceivedResult = null;
        lastReceivedSearch = null;
        receivedResultCount = 0;
        search.cancel();
        assertEquals("Canceling should not remove all results.", 1, search.getResults().size());
        path = "/new/foo/bar.txt";
        search.addResult(new MockResult(path));
        assertEquals("After canceling you can't add new results.", 1, search.getResults().size());
        String status = "Foo";
        search.currentStatus(status);
        search.clear();
        search.finished();
        checkResults("After canceling no more events should fire.", 0, 0, 0, 0);
        assertNull("Should not have any search results after canceling", lastReceivedResult);
        assertNull("Should not have any received search after canceling", lastReceivedSearch);
        assertNull("Should not have any search status after canceling", lastReceivedStatus);
    }
}
