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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.internal.compiler.classfmt.AnnotationMethodInfoWithAnnotations;
import org.eclipse.jdt.ls.core.internal.JDTUtils;

public class TestFetcher {

	private final String JUNIT_TEST_ANNOTATION = "org.junit.Test";
	private final String JUNIT_RUN_WITH_ANNOTATION = "org.junit.runner.RunWith";
	private final URI rootFolderURI;
	
	public TestFetcher(URI folderPath) {
		this.rootFolderURI = folderPath;
	}
	
	public List<String> fetchAllTests(IProgressMonitor monitor) {
		IRegion region = getRegion();
		SearchPattern runWithPattern = SearchPattern.createPattern(
				JUNIT_RUN_WITH_ANNOTATION,
				IJavaSearchConstants.ANNOTATION_TYPE,
				IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE,
				SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		SearchPattern testPattern = SearchPattern.createPattern(
				JUNIT_TEST_ANNOTATION,
				IJavaSearchConstants.ANNOTATION_TYPE,
				IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE,
				SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);
		SearchPattern pattern = SearchPattern.createOrPattern(runWithPattern, testPattern);
		List<String> tests = new ArrayList<>();
		HashSet<IType> testClasses = new HashSet<>();

	    // This is what we do when we find a match
		SearchRequestor requestor = new SearchRequestor() {
			@Override
			public void acceptSearchMatch( SearchMatch match ) throws CoreException {
				
				Object element = match.getElement();
				if (element instanceof IType || element instanceof IMethod) {
			        IMember member = (IMember) element;
			        IType type =
			            member.getElementType() == IJavaElement.TYPE
			                ? (IType) member
			                : member.getDeclaringType();
			        testClasses.add(type);
				}
			}
		};

		try {
			ITypeHierarchy hierarchy = JavaCore.newTypeHierarchy(region, null, null);
		    IType[] allClasses = hierarchy.getAllClasses();
		    IJavaSearchScope scope =
		            SearchEngine.createJavaSearchScope(allClasses, IJavaSearchScope.SOURCES);
			new SearchEngine().search(
					pattern,
					new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant()},
					scope,
					requestor,
					monitor);
			for (IType type : testClasses) {
				if (JUnitUtility.isAccessibleClass(type) &&
						!Flags.isAbstract(type.getFlags()) &&
						region.contains(type)) {
					tests.addAll(
							Arrays.stream(type.getMethods())
							.filter(m -> JUnitUtility.isTestMethod(m, JUNIT_TEST_ANNOTATION.substring(JUNIT_TEST_ANNOTATION.lastIndexOf('.') + 1)))
							.map(m -> constructTestCaseName(m))
							.collect(Collectors.toList()));
				}
			}
		} catch (CoreException e) {
			// ignore
		}
		return tests;
	}
	
	private String constructTestCaseName(IMethod method) {
		String methodName = constructMethodName(method);
		IType parent = method.getDeclaringType();
		String fq = parent.getFullyQualifiedName();
		String name = fq + "#" + methodName;
		String packageName = parent.getPackageFragment().getElementName();
		if (packageName == "") {
			return "default#" + name.replace('.', '#');
		}
		int index = name.indexOf(packageName);
		if (index < 0) {
			return name.replace('.', '#');
		} else {
			return name.substring(0, index + packageName.length()) + "#" + name.substring(index + packageName.length() + 1).replace('.', '#');
		}
	}
	
	private String constructMethodName(IMethod method) {
		String methodName = method.getElementName();
		try {
			String[] params = method.getParameterNames();
			methodName += "(" + String.join(",", params) + ")";
		} catch (JavaModelException e) {
			// ignore
		}
		return methodName;
	}
	
	private IRegion getRegion() {
	    IRegion result = JavaCore.newRegion();
	    for (IJavaProject project : ProjectUtils.parseProjects(this.rootFolderURI)) {
	    	try {
		        IPackageFragmentRoot[] packageFragmentRoots =
		            ((IJavaProject) project).getPackageFragmentRoots();
		        for (IPackageFragmentRoot packageFragmentRoot : packageFragmentRoots) {
		          if (!packageFragmentRoot.isArchive()) {
		            result.add(packageFragmentRoot);
		          }
		        }
		      } catch (JavaModelException e) {
		        System.out.println("Can't read source folders.");
		      }
	    }
	    return result;
	}
}
