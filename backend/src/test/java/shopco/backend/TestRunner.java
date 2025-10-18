package shopco.backend;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * Test runner for all backend tests
 * Run this class to execute all tests in the project
 * 
 * Test Coverage:
 * - Controllers: 9 controllers with comprehensive endpoint testing
 * - Services: 9 services with business logic testing
 * - Integration: 6 integration tests with database testing
 * - Total: 100+ test methods covering all major functionality
 */
@Suite
@SelectPackages({
    "shopco.backend.controller",
    "shopco.backend.service", 
    "shopco.backend.integration"
})
public class TestRunner {
    // This class serves as a test suite runner
    // All tests in the specified packages will be executed
}
