package fr.spoonlabs.flacoco.core.coverage.framework;


import eu.stamp_project.testrunner.EntryPoint;
import eu.stamp_project.testrunner.listener.CoveredTestResultPerTestMethod;
import eu.stamp_project.testrunner.runner.ParserOptions;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.core.test.TestContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.TimeoutException;

public abstract class TestFrameworkStrategy {

	private Logger logger = Logger.getLogger(TestFrameworkStrategy.class);
	private FlacocoConfig config = FlacocoConfig.getInstance();


	public abstract CoveredTestResultPerTestMethod execute(TestContext testContext) throws TimeoutException;

	/**
	 * Auxiliary method to setup test-runners default options
	 */
	protected void setupTestRunnerEntryPoint() {
		EntryPoint.coverageDetail = ParserOptions.CoverageTransformerDetail.DETAIL_COMPRESSED;
		EntryPoint.workingDirectory = new File(this.config.getWorkspace());
		EntryPoint.verbose = this.config.isTestRunnerVerbose();
		EntryPoint.timeoutInMs = this.config.getTestRunnerTimeoutInMs();
		EntryPoint.JVMArgs = this.config.getTestRunnerJVMArgs();
		EntryPoint.jUnit5Mode = false;
		if (this.config.isCoverTests()) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Auxiliary method to compute the classpath according to the test framework and the custom confirguration
	 *
	 * @return Classpath for test-runner execution
	 */
	protected String computeClasspath() {
		String classpath = this.config.getClasspath() + File.pathSeparatorChar +
				this.config.getBinJavaDir().stream().reduce((x, y) -> x + File.pathSeparatorChar + y) +
				File.pathSeparatorChar +
				this.config.getBinTestDir().stream().reduce((x, y) -> x + File.pathSeparatorChar + y);
		String mavenHome = this.config.getMavenHome();
		String junitClasspath;
		String jacocoClassPath;

		junitClasspath = mavenHome + "junit/junit/4.12/junit-4.12.jar" + File.pathSeparatorChar
				+ mavenHome + "org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar" + File.pathSeparatorChar
				+ mavenHome + "org/junit/jupiter/junit-jupiter-api/5.3.2/junit-jupiter-api-5.3.2.jar" + File.pathSeparatorChar
				+ mavenHome + "org/apiguardian/apiguardian-api/1.0.0/apiguardian-api-1.0.0.jar" + File.pathSeparatorChar
				+ mavenHome + "org/opentest4j/opentest4j/1.1.1/opentest4j-1.1.1.jar" + File.pathSeparatorChar
				+ mavenHome + "org/junit/platform/junit-platform-commons/1.3.2/junit-platform-commons-1.3.2.jar" + File.pathSeparatorChar
				+ mavenHome + "org/junit/jupiter/junit-jupiter-engine/5.3.2/junit-jupiter-engine-5.3.2.jar" + File.pathSeparatorChar
				+ mavenHome + "org/junit/jupiter/junit-jupiter-params/5.3.2/junit-jupiter-params-5.3.2.jar" + File.pathSeparatorChar
				+ mavenHome + "org/junit/platform/junit-platform-engine/1.3.2/junit-platform-engine-1.3.2.jar" + File.pathSeparatorChar
				+ mavenHome + "org/junit/platform/junit-platform-launcher/1.3.2/junit-platform-launcher-1.3.2.jar";

		jacocoClassPath = mavenHome + "org/jacoco/org.jacoco.core/0.8.3/org.jacoco.core-0.8.3.jar";

		// Add JUnit dependencies
		if (this.config.getCustomJUnitClasspath() != null)
			junitClasspath = this.config.getCustomJUnitClasspath();
		// Add jacoco dependencies
		if (this.config.getCustomJacocoClasspath() != null)
			jacocoClassPath = this.config.getCustomJacocoClasspath();

		return junitClasspath + File.pathSeparatorChar + jacocoClassPath + File.pathSeparatorChar +
				classpath + File.pathSeparatorChar;
	}

}
