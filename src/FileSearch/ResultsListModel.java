package FileSearch;

import FileSearch.impl.ResultImpl;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public class ResultsListModel implements ListModel<String> {

    List<ResultImpl> results = new ArrayList<>();
    List<ListDataListener> listeners = new ArrayList<>();

    private void broadcast(ListDataEvent e) {
        for (ListDataListener l : listeners) {
            l.contentsChanged(e);
        }
    }

    public void update(List<ResultImpl> newResults) {
        List<ResultImpl> oldResults = this.results;
        int maxSize = oldResults.size();
        if (newResults.size() > maxSize) {
            maxSize = newResults.size();
        }
        results = newResults;
        broadcast(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, maxSize));
    }

    public void addedResults(int n) {
        int resultSizedIndex = results.size() - n;
        broadcast(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, resultSizedIndex - n, resultSizedIndex));
    }

    @Override
    public int getSize() {
        return results.size();
    }

    @Override
    public String getElementAt(int index) {
        ResultImpl res = this.results.get(index);
        return res.getValue();
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }
}
