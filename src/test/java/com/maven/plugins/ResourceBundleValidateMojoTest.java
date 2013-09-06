package com.maven.plugins;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

/**
 * @author Chris Shayan
 */
public class ResourceBundleValidateMojoTest extends AbstractMojoTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testError1() throws Exception {
        final File pom = new File(getBasedir(), "src/test/resources/com/maven/plugins/plugin-config-error1.xml");
        ResourceBundleValidateMojo mojo = (ResourceBundleValidateMojo) lookupMojo("validateBundlesGoal", pom);

        assertNotNull(mojo);
        try {
            mojo.execute();
        } catch (MojoExecutionException e) {
            assertNotNull(e);
            assertEquals("Resource Bundle Directory should already exist.", e.getMessage());
        }
    }

    public void testError2() throws Exception {
        final File pom = new File(getBasedir(), "src/test/resources/com/maven/plugins/plugin-config-error2.xml");
        ResourceBundleValidateMojo mojo = (ResourceBundleValidateMojo) lookupMojo("validateBundlesGoal", pom);

        assertNotNull(mojo);
        try {
            mojo.execute();
        } catch (MojoExecutionException e) {
            assertNotNull(e);
            assertEquals("Resource Bundles are not sync.", e.getMessage());
        }
    }
}
