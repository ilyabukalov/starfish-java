
package connection_check;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class AssumingConnection implements TestRule {

    private ConnectionChecker checker;

    public AssumingConnection(ConnectionChecker checker) {
        this.checker = checker;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if (!checker.connect()) {
                    throw new AssertionError("Integration Test failed as " + "Server (" + checker.toString() + ") is not reachable.");
                } else {
                    base.evaluate();
                }
            }
        };
    }

}

