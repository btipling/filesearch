package FileSearch.test;

import FileSearch.tools.PathManager;

public class MockPathManager implements PathManager {

    String path;

    public MockPathManager(String path) {
        this.path = path;
    }

    @Override
    public String getFileName() {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    @Override
    public String getFullPath() {
        return this.path;
    }

}
