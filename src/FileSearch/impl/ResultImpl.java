package FileSearch.impl;

import FileSearch.tools.PathManager;
import FileSearch.tools.Result;

public class ResultImpl implements Result {
    private final PathManager path;

    public ResultImpl(PathManager path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return this.path.toString();
    }
}
