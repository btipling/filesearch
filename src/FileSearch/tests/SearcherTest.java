package FileSearch.tests;

import FileSearch.Search;
import FileSearch.SearchOptions;
import FileSearch.Searcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;

import static org.junit.Assert.assertEquals;

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
        searcher = new Searcher(search, new MockFileUtils(false));
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

    @Test
    public void testHidden() {
        searcher = new Searcher(search, new MockFileUtils(true));
        assertEquals("Hidden dir should continue", FileVisitResult.CONTINUE, searcher.checkDir(file));
        searcher.checkFile(file);
        assertEquals("Search should have added a hidden result", 2, search.getResults().size());
        searchOptions.searchHiddenDirs = false;
        assertEquals("Hidden dir should not continue", FileVisitResult.SKIP_SUBTREE, searcher.checkDir(file));
        assertEquals("Search should not have added a hidden directory", 2, search.getResults().size());
        searcher.checkFile(file);
        assertEquals("Search should have added a hidden file even if we skip hidden dirs", 3, search.getResults().size());
    }

    @Test
    public void testRegex() {
        searchOptions.regex = true;
        searchOptions.searchString = "f.obar\\.txt";
        search = new Search(searchOptions);
        searcher = new Searcher(search, new MockFileUtils(false));
        file = new MockPathManager("/foo/bar/foobar.txt");
        searcher.checkFile(file);
        assertEquals("Regex should match.", 1, search.getResults().size());
        file = new MockPathManager("/foo/bar/fuobar.txt");
        searcher.checkFile(file);
        assertEquals("Regex should match.", 2, search.getResults().size());
        file = new MockPathManager("/foo/bar/fobar.txt");
        searcher.checkFile(file);
        assertEquals("Regex should not have matched.", 2, search.getResults().size());
    }

    @Test
    public void testExactFileNameSearch() {
        searchOptions.match = SearchOptions.MatchOption.EXACT_FILE;
        searchOptions.searchString = "foobar.txt";
        search = new Search(searchOptions);
        searcher = new Searcher(search, new MockFileUtils(false));
        file = new MockPathManager("/foo/bar/foobar.txt");
        searcher.checkFile(file);
        assertEquals("File name should match.", 1, search.getResults().size());
        file = new MockPathManager("/foo/bar/nope_foobar.txt");
        assertEquals("File name should not match.", 1, search.getResults().size());
        file = new MockPathManager("/foo/bar/foobar.txtnope");
        assertEquals("File name should still not match.", 1, search.getResults().size());
    }

    @Test
    public void testPathSearch() {
        searchOptions.match = SearchOptions.MatchOption.MATCH_PATH;
        searchOptions.searchString = "foo";
        search = new Search(searchOptions);
        searcher = new Searcher(search, new MockFileUtils(false));
        file = new MockPathManager("/foo/bar/bar.txt");
        searcher.checkFile(file);
        assertEquals("Path should match.", 1, search.getResults().size());
        file = new MockPathManager("/bar/bar/foo.txt");
        searcher.checkFile(file);
        assertEquals("File name should  match.", 2, search.getResults().size());
        file = new MockPathManager("/nothing/here/matches.nope");
        assertEquals("Nothing matches.", 2, search.getResults().size());
    }

    @Test
    public void testFileNameOnly() {
        searchOptions.match = SearchOptions.MatchOption.MATCH_FILE;
        searchOptions.searchString = "foo";
        search = new Search(searchOptions);
        searcher = new Searcher(search, new MockFileUtils(false));
        file = new MockPathManager("/foo/bar/bar.txt");
        searcher.checkFile(file);
        assertEquals("Path should not match.", 0, search.getResults().size());
        file = new MockPathManager("/bar/bar/foo.txt");
        searcher.checkFile(file);
        assertEquals("File name should  match.", 1, search.getResults().size());
        file = new MockPathManager("/nothing/here/matches.nope");
        assertEquals("Nothing matches.", 1, search.getResults().size());
    }

    @Test
    public void testCaseSensitive() {
        searchOptions.caseSensitive = true;
        searchOptions.searchString = "fOo";
        search = new Search(searchOptions);
        searcher = new Searcher(search, new MockFileUtils(false));
        file = new MockPathManager("/foo/bar/Foo.txt");
        searcher.checkFile(file);
        assertEquals("File name should not match.", 0, search.getResults().size());
        file = new MockPathManager("/bar/bar/fOo.txt");
        searcher.checkFile(file);
        assertEquals("File name should  match.", 1, search.getResults().size());
        file = new MockPathManager("/nothing/here/matches.nope");
        assertEquals("Nothing matches.", 1, search.getResults().size());
        searchOptions.caseSensitive = false;
        searchOptions.searchString = "fOo";
        search = new Search(searchOptions);
        searcher = new Searcher(search, new MockFileUtils(false));
        file = new MockPathManager("/foo/bar/Foo.txt");
        searcher.checkFile(file);
        assertEquals("File name should now match.", 1, search.getResults().size());
        file = new MockPathManager("/bar/bar/fOo.txt");
        searcher.checkFile(file);
        assertEquals("File name should  match.", 2, search.getResults().size());
        file = new MockPathManager("/nothing/here/matches.nope");
        assertEquals("Nothing matches.", 2, search.getResults().size());
    }

    @Test
    public void testCaseSensitiveRegex() {
        searchOptions.caseSensitive = true;
        searchOptions.regex = true;
        searchOptions.searchString = "f.O\\.txt";
        search = new Search(searchOptions);
        searcher = new Searcher(search, new MockFileUtils(false));
        file = new MockPathManager("/foo/bar/Foo.txt");
        searcher.checkFile(file);
        assertEquals("File name should not match.", 0, search.getResults().size());
        file = new MockPathManager("/bar/bar/foO.txt");
        searcher.checkFile(file);
        assertEquals("File name should match.", 1, search.getResults().size());
        file = new MockPathManager("/nothing/here/matches.nope");
        assertEquals("Nothing matches.", 1, search.getResults().size());
        searchOptions.caseSensitive = false;
        searchOptions.searchString = "f.O\\.txt";
        search = new Search(searchOptions);
        searcher = new Searcher(search, new MockFileUtils(false));
        file = new MockPathManager("/foo/bar/Foo.txt");
        searcher.checkFile(file);
        assertEquals("File name should now match.", 1, search.getResults().size());
        file = new MockPathManager("/bar/bar/fOo.txt");
        searcher.checkFile(file);
        assertEquals("File name should still match.", 2, search.getResults().size());
        file = new MockPathManager("/nothing/here/matches.nope");
        assertEquals("Nothing matches.", 2, search.getResults().size());
    }

}
