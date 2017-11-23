export class TestNode {
    constructor(private _workspaceFolder: string, private _parentPath: string, private _name: string, private _children?: TestNode[], private _level: NodeLevel = NodeLevel.Method) {
    }

    public get name(): string {
        return this._name;
    }

    public get fullName(): string {
        let shortName = this._name;        
        if (this._level === NodeLevel.Method) {
            const index = this._name.indexOf('(');
            shortName = shortName.substring(0, index);
        }
        return (this._parentPath ? `${this._parentPath}` + (this._children ? "." : "#") : "") + shortName;
    }

    public get isFolder(): boolean {
        return this._children && this._children.length > 0;
    }

    public get children(): TestNode[] {
        return this._children;
    }

    public get level() : NodeLevel {
        return this._level;
    }

    public get workspaceFolder() : string {
        return this._workspaceFolder;
    }
}

export enum NodeLevel {
    Method,
    Class,
    Package,
    Folder
}
