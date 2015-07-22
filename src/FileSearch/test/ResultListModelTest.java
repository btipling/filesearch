package FileSearch.test;

import FileSearch.ResultsListModel;
import FileSearch.tools.Result;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ResultListModelTest {

    ResultsListModel resultsListModel;
    ListDataListener dataListener;
    int firedChangeListener;
    int firedAddListener;
    int firedRemoveListener;
    ListDataEvent lastDataEvent;
    List<Result> results = new ArrayList<>();

    ListDataListener createDataListener() {
        return new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                lastDataEvent = e;
                firedAddListener += 1;
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                lastDataEvent = e;
                firedRemoveListener += 1;
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                lastDataEvent = e;
                firedChangeListener += 1;
            }
        };
    }

    void assertListenerCounts(String msg, int changed, int added, int removed) {
        assertEquals(String.format("Changed count incorrect. %s", msg), changed, firedChangeListener);
        assertEquals(String.format("Added count incorrect. %s", msg), added, firedAddListener);
        assertEquals(String.format("Removed count incorrect. %s", msg), removed, firedRemoveListener);
    }

    @Before
    public void setUp() {
        firedChangeListener = 0;
        firedAddListener = 0;
        firedRemoveListener = 0;
        lastDataEvent = null;
        resultsListModel = new ResultsListModel();
        dataListener = createDataListener();
        resultsListModel.addListDataListener(dataListener);
        resultsListModel.update(results);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetup() {
        assertListenerCounts("Should have fired changed once in setup.", 1, 0, 0);
        assertEquals("Should have created a changed event", lastDataEvent.getType(),
                ListDataEvent.CONTENTS_CHANGED);
        assertEquals("Should not have any elements.", 0, resultsListModel.getSize());
    }

    @Test
    public void testAdd() {
        String path = "/foo/bar/so/unique/foobar.txt";
        results.add(new MockResult(path));
        resultsListModel.addedResults(1);
        assertListenerCounts("Should have fired add event.", 1, 1, 0);
        assertEquals("Should have created an add event", lastDataEvent.getType(),
                ListDataEvent.INTERVAL_ADDED);
        assertEquals("Should not have any elements.", 1, resultsListModel.getSize());
        assertEquals("Should have returned the correct path", path, resultsListModel.getElementAt(0));
    }

    @Test
    public void testAddListeners() {
        dataListener = createDataListener();
        resultsListModel.addListDataListener(dataListener);
        String path = "/foo/bar/so/unique/foobar.txt";
        results.add(new MockResult(path));
        resultsListModel.addedResults(1);
        assertListenerCounts("Should have fired add twice with two listeners.", 1, 2, 0);
    }

    @Test
    public void testRemListeners() {
        resultsListModel.removeListDataListener(dataListener);
        String path = "/foo/bar/so/unique/foobar.txt";
        results.add(new MockResult(path));
        resultsListModel.addedResults(1);
        assertListenerCounts("Should not have fired add event with no listeners.", 1, 0, 0);
    }

    @Test
    public void testSmallUpdate() {
        String path = "/foo/bar/so/unique/foobar.txt";
        results.add(new MockResult(path));
        resultsListModel.addedResults(1);
        assertEquals("Should have added result", resultsListModel.getSize(), 1);
        results = new ArrayList<>();
        resultsListModel.update(results);
        assertListenerCounts("Should have fired add two change events.", 2, 1, 0);
        assertEquals("Should have no results", resultsListModel.getSize(), 0);
    }

    @Test
    public void testLargeUpdate() {
        String path = "/foo/bar/so/unique/foobar.txt";
        results.add(new MockResult(path));
        resultsListModel.addedResults(1);
        assertEquals("Should have added result", resultsListModel.getSize(), 1);
        results = new ArrayList<>();
        results.add(new MockResult(path));
        path = "/foo/bar/so/unique/foobar2.txt";
        results.add(new MockResult(path));
        resultsListModel.update(results);
        assertListenerCounts("Should have fired add two change events.", 2, 1, 0);
        assertEquals("Should have no results", resultsListModel.getSize(), 2);
    }


}
