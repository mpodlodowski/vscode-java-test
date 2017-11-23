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

package com.java.junit.plugin.internal;

import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;

public class TestDelegateCommandHandler implements IDelegateCommandHandler {

    public static String FETCH_ALL_TESTS = "vscode.java.test.fetchall";
    public static String COMPUTE_RUNTIME_CLASSPATH = "vscode.java.test.runtime.classpath";

    @Override
    public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress) throws Exception {
        if (FETCH_ALL_TESTS.equals(commandId)) {
            return new TestFetcher(new URI((String)arguments.get(0))).fetchAllTests(progress);
        } else if (COMPUTE_RUNTIME_CLASSPATH.equals(commandId)) {
        	return new TestRuntimeClassPathResolver(new URI((String)arguments.get(0))).resolveRunTimeClassPath();
        }

        throw new UnsupportedOperationException(String.format("Java test plugin doesn't support the command '%s'.", commandId));
    }

}

