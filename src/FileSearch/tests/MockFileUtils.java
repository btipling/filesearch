package FileSearch.tests;

import FileSearch.tools.FileUtils;

public class MockFileUtils implements FileUtils {

    private final boolean hidden;

    public MockFileUtils(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public void openFile(String path) {

    }

    @Override
    public void openFolder(String path) {

    }

    @Override
    public void copyFolderPath(String path) {

    }

    @Override
    public void copyPath(String path) {

    }

    @Override
    public void openFileWithIDEA(String path) {

    }

    @Override
    public boolean isHidden(String path) {
        return this.hidden;
    }
}
