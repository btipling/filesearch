package FileSearch.impl;

import FileSearch.tools.PathManager;
import FileSearch.tools.Result;
import org.jetbrains.annotations.NotNull;

public class ResultImpl implements Result {
    private final PathManager path;

    public ResultImpl(@NotNull PathManager path) {
        this.path = path;
    }

    @Override
    public String getValue() {
        return this.path.getFullPath();
    }
}
