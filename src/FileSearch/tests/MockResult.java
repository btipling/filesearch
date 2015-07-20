package FileSearch.tests;

import FileSearch.tools.Result;

public class MockResult implements Result {

    private final String path;

    public MockResult(String path) {
        this.path = path;
    }

    @Override
    public String getValue() {
        return path;
    }
}
