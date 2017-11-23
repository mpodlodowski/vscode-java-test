import {Commands} from './commands';
import * as path from 'path';
import * as vscode from 'vscode';
import {TestNode, NodeLevel} from './testNode';

export class TestExplorer implements vscode.TreeDataProvider<TestNode> {
    private _onDidChangeTreeData: vscode.EventEmitter<TestNode | undefined> = new vscode.EventEmitter<TestNode | undefined>();
	readonly onDidChangeTreeData: vscode.Event<TestNode | undefined> = this._onDidChangeTreeData.event;

	constructor(private workspaceRoot: string, private context: vscode.ExtensionContext) {
	}

	refresh(): void {
		this._onDidChangeTreeData.fire();
	}

	getTreeItem(element: TestNode): vscode.TreeItem {
		return {
            label: element.name,
            collapsibleState: element.isFolder ? vscode.TreeItemCollapsibleState.Collapsed : void 0,
            iconPath: {
                dark: this.context.asAbsolutePath(path.join("resources", "dark", this.getIcon(element))),
                light: this.context.asAbsolutePath(path.join("resources", "light", this.getIcon(element))),
            },
            contextValue: element.level.toString()
        };
    }
    
    getChildren(element?: TestNode): TestNode[] | Thenable<TestNode[]> {
		if (element) {
            return element.children;
        }

        const workspaceFolder = vscode.workspace.workspaceFolders[0].uri.toString();
        return fetchAllTests(workspaceFolder).then((tests: string[]) => {
            const structuredTests = {}; 
            tests.forEach((name: string) => {
            const parts = name.split('#');
                this.addToObject(structuredTests, parts);
            });
            const root = this.createTestNode(workspaceFolder, "", structuredTests, NodeLevel.Package);
            return root;
        })
    }

    private getIcon(element: TestNode): string {
        switch (element.level) {
            case NodeLevel.Method:
                return "method.png";
            case NodeLevel.Class:
                return "class.png";
            case NodeLevel.Package:
                return "package.png";
            case NodeLevel.Folder:
                return "folder.png";
        }
    }

    private addToObject(container: object, parts: string[]): void {
        const title = parts.splice(0, 1)[0];

        if (parts.length > 1) {
            if (!container[title]) {
                container[title] = {};
            }
            this.addToObject(container[title], parts);
        } else {
            if (!container[title]) {
                container[title] = [];
            }

            if (parts.length === 1) {
                container[title].push(parts[0]);
            }
        }
    }

    private createTestNode(workspaceFolder: string, parentPath: string, test: object | string, level: NodeLevel): TestNode[] {
        if (Array.isArray(test)) {
            return test.map((t) => {
                return new TestNode(workspaceFolder, parentPath, t);
            });
        } else if (typeof test === "object") {
            return Object.keys(test).map((key) => {
                return new TestNode(workspaceFolder, parentPath, key, this.createTestNode(workspaceFolder, (parentPath ? `${parentPath}.` : "") + key, test[key], NodeLevel.Class), level);

            });
        } else {
            return [new TestNode(workspaceFolder, parentPath, test)];
        }
    }
}

function fetchAllTests(workspaceFolder) {
    return Commands.executeJavaLanguageServerCommand(Commands.JAVA_TEST_FETCH_ALL_COMMAND, workspaceFolder);
}