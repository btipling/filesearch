package FileSearch.tests;

import FileSearch.Search;
import FileSearch.SearchOptions;
import FileSearch.Searcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;

import static org.junit.Assert.*;

public class SearcherTest {

    SearchOptions searchOptions;
    Search search;
    Searcher searcher;
    MockPathManager file;

    @Before
    public void setUp() {
        file = new MockPathManager("/foo/bar/foo.txt");
        searchOptions = new SearchOptions();
        searchOptions.regex = false;
        searchOptions.caseSensitive = false;
        searchOptions.recursive = true;
        searchOptions.match = SearchOptions.MatchOption.MATCH_FILE;
        searchOptions.searchString = "foo";
        searchOptions.searchHiddenDirs = true;
        search = new Search(searchOptions);
        searcher = new Searcher(search, new MockFileUtils(true));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testVisitFileResult() throws IOException {
        assertEquals("visitFile should always return continue", FileVisitResult.CONTINUE, searcher.checkFile(file));
        assertEquals("visitFileFailed should always return continue", FileVisitResult.CONTINUE, searcher.visitFileFailed(null, null));
        assertEquals("postVisitDirectory should always return continue", FileVisitResult.CONTINUE, searcher.postVisitDirectory(null, null));
    }

    @Test
    public void testRecursive() {
        assertEquals("Recursive should always continue", FileVisitResult.CONTINUE, searcher.checkDir(file));
        searchOptions.recursive = false;
        assertEquals("Not recursive should skip dirs", FileVisitResult.SKIP_SUBTREE, searcher.checkDir(file));
        searchOptions.recursive = true;
        assertEquals("Resetting recursive to true should continue", FileVisitResult.CONTINUE, searcher.checkDir(file));
    }

    @Test
    public void testMatchFile() {
        assertEquals("Search results should start empty.", 0, search.getResults().size());
        searcher.checkFile(file);
        assertEquals("Search should have added a result", 1, search.getResults().size());
    }

}
