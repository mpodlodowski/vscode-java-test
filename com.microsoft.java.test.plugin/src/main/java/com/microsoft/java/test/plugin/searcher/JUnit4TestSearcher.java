/*******************************************************************************
 * Copyright (c) 2017 Microsoft Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Microsoft Corporation - initial API and implementation
 *******************************************************************************/

package com.microsoft.java.test.plugin.searcher;

import com.microsoft.java.test.plugin.model.TestKind;
import com.microsoft.java.test.plugin.util.TestSearchUtils;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class JUnit4TestSearcher extends BaseFrameworkSearcher {

    protected static final String TEST_METHOD_ANNOTATION = "org.junit.Test";

    public JUnit4TestSearcher() {
        super(TEST_METHOD_ANNOTATION);
    }

    @Override
    public TestKind getTestKind() {
        return TestKind.JUnit;
    }

    @Override
    public boolean isTestMethod(IMethod method) {
        try {
            final int flags = method.getFlags();
            if (!Flags.isPublic(flags)) {
                return false;
            }
            return super.isTestMethod(method) && TestSearchUtils.hasTestAnnotation(method, TEST_METHOD_ANNOTATION);
        } catch (final JavaModelException e) {
            // ignore
            return false;
        }
    }
}
