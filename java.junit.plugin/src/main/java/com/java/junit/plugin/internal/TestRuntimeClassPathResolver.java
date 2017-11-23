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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;

public class TestRuntimeClassPathResolver {
	
	private final URI rootFolder;
	
	public TestRuntimeClassPathResolver(URI rootFolder) {
		this.rootFolder = rootFolder;
	}

	public String[] resolveRunTimeClassPath() throws CoreException {
		Set<IJavaProject> projects = ProjectUtils.parseProjects(this.rootFolder);
		HashSet<String> classPaths = new HashSet<>();
		for (IJavaProject project : projects) {
			classPaths.addAll(Arrays.asList(JavaRuntime.computeDefaultRuntimeClassPath(project)));
		}
		return classPaths.toArray(new String[classPaths.size()]); 
	}
}
