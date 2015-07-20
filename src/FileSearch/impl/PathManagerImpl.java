package FileSearch.impl;

import FileSearch.tools.PathManager;

import java.nio.file.Path;

public class PathManagerImpl implements PathManager {

    private Path path;

    public PathManagerImpl(Path path) {
        this.path = path;
    }

    @Override
    public String getFileName() {
        Path filenamePath = path.getFileName();
        if (filenamePath == null) {
            //Happens when we are searching the root directory, "/"!
            return null;
        }
        return filenamePath.toString();
    }

    @Override
    public String toString() {
        return this.path.toString();
    }
}
