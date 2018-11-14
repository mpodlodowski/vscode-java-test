package com.microsoft.java.test.plugin.searcher;

import com.microsoft.java.test.plugin.model.TestKind;

public class SpockTestSearcher extends BaseFrameworkSearcher {

    public SpockTestSearcher() {
        super(null);
    }

    public TestKind getTestKind() {
        return TestKind.Spock;
    }

}
