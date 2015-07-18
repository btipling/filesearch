package FileSearch;

import org.apache.xerces.impl.xpath.regex.RegularExpression;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher implements FileVisitor<Path> {

    Search search;
    private SearchStrategy searchStrategy;

    private interface SearchStrategy {
        void setSearcher(String searchString, boolean caseSensitive);
        boolean match(String toCheck);
    }

    private class SearchStrategyString implements SearchStrategy {

        private String searchString = null;

        @Override
        public void setSearcher(String searchString, boolean caseSensitive) {
            if (!caseSensitive) {
                this.searchString = searchString.toLowerCase();
            }
            this.searchString = searchString;
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
                pattern = Pattern.compile(searchString, Pattern.CASE_INSENSITIVE);
            } else {
                pattern = Pattern.compile(searchString);
            }
        }

        @Override
        public boolean match(String toCheck) {
            Matcher m =  pattern.matcher(toCheck);
            return m.matches();
        }
    }


    public Searcher(Search search) {
        if (search.searchOptions.regex && search.searchOptions.match != SearchOptions.MatchOption.EXACT_FILE) {
            searchStrategy = new SearchStrategyRegExp();
        } else {
            searchStrategy = new SearchStrategyString();
        }
        searchStrategy.setSearcher(search.searchOptions.searchString, search.searchOptions.caseSensitive);
        this.search = search;
    }

    protected FileVisitResult check(Path dirOrFile, boolean ispreVisitDir) {
        if (Thread.currentThread().isInterrupted()) {
            return FileVisitResult.TERMINATE;
        }
        Path filenamePath = dirOrFile.getFileName();
        if (filenamePath == null) {
            return null;
        }
        String filename = filenamePath.toString();
        String path = dirOrFile.toString();
        if (!search.searchOptions.caseSensitive) {
            filename = filename.toLowerCase();
            path = path.toLowerCase();
        }
        if (ispreVisitDir && !search.searchOptions.searchHiddenDirs) {
            File f = new File(path);
            if (f.isHidden()) {
                return FileVisitResult.SKIP_SUBTREE;
            }
        }
        switch (search.searchOptions.match) {
            case MATCH_PATH:
                if (searchStrategy.match(path)) {
                    search.addResult(new Result(dirOrFile));
                }
                break;
            case EXACT_FILE:
                if (filename.equals(search.searchOptions.searchString)) {
                    search.addResult(new Result(dirOrFile));
                }
                break;
            default:
                if (searchStrategy.match(filename)) {
                    search.addResult(new Result(dirOrFile));
                }
                break;
        }
        return null;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        FileVisitResult r = this.check(dir, true);
        if (r != null) {
            return r;
        }
        if (!search.searchOptions.recursive) {
            return FileVisitResult.SKIP_SUBTREE;
        }
        search.currentStatus(String.format("Searching %s", dir.toString()));
        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
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
