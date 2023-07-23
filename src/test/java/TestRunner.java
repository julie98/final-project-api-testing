import io.cucumber.junit.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "classpath:features",
//        features = "src/test/resources/features/projectManagement.feature",
        glue = "classpath:stepDefinitions",
        plugin = "html:target/cucumber-html-report.html" // file format, file path
)
public class TestRunner extends AbstractTestNGCucumberTests {
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}