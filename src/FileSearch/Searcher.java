package FileSearch;

import FileSearch.impl.PathManagerImpl;
import FileSearch.impl.ResultImpl;
import FileSearch.tools.FileUtils;
import FileSearch.tools.PathManager;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher implements FileVisitor<Path> {

    private Search search;
    private SearchStrategy searchStrategy;
    private boolean top = true;
    FileUtils fileUtils;

    private interface SearchStrategy {
        void setSearcher(String searchString, boolean caseSensitive);
        boolean match(String toCheck);
    }

    private class SearchStrategyString implements SearchStrategy {

        private String searchString = null;

        @Override
        public void setSearcher(String searchString, boolean caseSensitive) {
            this.searchString = searchString;
            if (!caseSensitive) {
                this.searchString = searchString.toLowerCase();
            }
        }

        @Override
        public boolean match(String toCheck) {
            return toCheck.contains(searchString);
        }
    }

    private class SearchStrategyRegExp implements SearchStrategy {

        private Pattern pattern = null;

        @Override
        public void setSearcher(String searchString, boolean caseSensitive) {

            if (caseSensitive) {
                pattern = Pattern.compile(searchString);
            } else {
                pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
            }
        }

        @Override
        public boolean match(String toCheck) {
            Matcher m =  pattern.matcher(toCheck);
            return m.matches();
        }
    }

    public Searcher(Search search, FileUtils fileUtils) {
        this.fileUtils = fileUtils;
        if (search.searchOptions.regex && search.searchOptions.match != SearchOptions.MatchOption.EXACT_FILE) {
            searchStrategy = new SearchStrategyRegExp();
        } else {
            searchStrategy = new SearchStrategyString();
        }
        searchStrategy.setSearcher(search.searchOptions.searchString, search.searchOptions.caseSensitive);
        this.search = search;
    }

    protected FileVisitResult check(PathManager dirOrFile, boolean isPreVisitDir) {
        if (Thread.currentThread().isInterrupted()) {
            return FileVisitResult.TERMINATE;
        }
        String filename = dirOrFile.getFileName();
        String path = dirOrFile.getFullPath();
        String searchString = search.searchOptions.searchString;
        if (!search.searchOptions.caseSensitive) {
            filename = filename.toLowerCase();
            path = path.toLowerCase();
            searchString = searchString.toLowerCase();
        }
        if (isPreVisitDir && !search.searchOptions.searchHiddenDirs) {
            if (fileUtils.isHidden(path)) {
                return FileVisitResult.SKIP_SUBTREE;
            }
        }
        switch (search.searchOptions.match) {
            case MATCH_PATH:
                if (searchStrategy.match(path)) {
                    search.addResult(new ResultImpl(dirOrFile));
                }
                break;
            case EXACT_FILE:
                if (filename.equals(searchString)) {
                    search.addResult(new ResultImpl(dirOrFile));
                }
                break;
            default:
                if (searchStrategy.match(filename)) {
                    search.addResult(new ResultImpl(dirOrFile));
                }
                break;
        }
        return null;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return checkDir(new PathManagerImpl(dir));
    }

    public FileVisitResult checkDir(PathManager dir) {
        FileVisitResult r = this.check(dir, true);
        if (r != null) {
            return r;
        }
        if (!top && !search.searchOptions.recursive) {
            return FileVisitResult.SKIP_SUBTREE;
        }
        // Otherwise we would never search anything, we need to search the first directory.
        top = false;
        search.currentStatus(String.format("Searching %s", dir.getFullPath()));
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        return checkFile(new PathManagerImpl(file));
    }

    public FileVisitResult checkFile(PathManager file) {
        FileVisitResult r = this.check(file, false);
        if (r != null) {
            return r;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        if (Thread.currentThread().isInterrupted()) {
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (Thread.currentThread().isInterrupted()) {
            return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
    }

}
