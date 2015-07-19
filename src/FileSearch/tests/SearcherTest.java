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

    @Before
    public void setUp() {
        searchOptions = new SearchOptions();
        searchOptions.regex = false;
        searchOptions.caseSensitive = false;
        searchOptions.recursive = true;
        searchOptions.match = SearchOptions.MatchOption.MATCH_FILE;
        searchOptions.searchString = "foo";
        search = new Search(searchOptions);
        searcher = new Searcher(search);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testVisitFileResult() throws IOException {
        File f = new File("./");
        assertEquals(FileVisitResult.CONTINUE, searcher.visitFile(f.toPath(), null));
    }

}
