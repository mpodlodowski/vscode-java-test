import { ITestItem } from '../../protocols';
import { BaseRunner } from '../baseRunner/BaseRunner';
import { BaseRunnerResultAnalyzer } from '../baseRunner/BaseRunnerResultAnalyzer';
import { SpockRunnerResultAnalyzer } from './SpockRunnerResultAnalyzer';

export class SpockRunner extends BaseRunner {
    public constructCommandParams(): string[] {
        return [...super.constructCommandParams(), 'spock', ...this.tests.map((t: ITestItem) => t.fullName)];
    }

    public getTestResultAnalyzer(): BaseRunnerResultAnalyzer {
        return new SpockRunnerResultAnalyzer(this.tests);
    }
}
