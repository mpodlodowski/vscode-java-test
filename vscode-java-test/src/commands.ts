import * as vscode from 'vscode';
export namespace Commands {
    /**
     * Run test
     */
    export const JAVA_RUN_TEST_COMMAND = 'java.run.test';
    
    /**
     * Debug test
     */
    export const JAVA_DEBUG_TEST_COMMAND = 'java.debug.test';

    export const TEST_EXPLORER_RUN_TEST_COMMAND = 'test.explorer.java.run.test';

    export const TEST_EXPLORER_DEBUG_TEST_COMMAND = 'test.explorer.java.debug.test';

    export const TEST_EXPLORER_RUN_ALL_COMMAND = 'test.explorer.java.run.all';

    export const TEST_EXPLORER_REFRESH_COMMAND = 'test.explorer.refresh';

    export const JAVA_TEST_FETCH_ALL_COMMAND = 'vscode.java.test.fetchall';

    export const JAVA_TEST_COMPUTE_RUNTIME_CLASSPATH = 'vscode.java.test.runtime.classpath';

    export const JAVA_EXECUTE_WORKSPACE_COMMAND = "java.execute.workspaceCommand";

    export function executeJavaLanguageServerCommand(...rest) {
        // TODO: need to handle error and trace telemetry
        return vscode.commands.executeCommand(JAVA_EXECUTE_WORKSPACE_COMMAND, ...rest);
    }
}