package com.maven.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Chris Shayan
 */
@Mojo(name = "validateBundlesGoal", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = false)
public class ResourceBundleValidateMojo extends AbstractMojo {

    @Parameter(property = "resourceBundle.directory", required = true)
    private File resourceBundleDirectory;

    @Parameter(property = "resourceBundle.groups", required = true)
    private Properties resourceBundleGroups;

    /**
     * {@inheritDoc}
     */
    public void execute() throws MojoExecutionException {
        validate();

        final Set<Object> objects = resourceBundleGroups.keySet();
        for (final Object key : objects) {
            try {
                final Map<String,Properties> propertiesMap = FileUtils.preparePropertyFiles(resourceBundleDirectory, resourceBundleGroups.getProperty(key.toString()));
                new ResourceBundleMissingKeyValidator(propertiesMap).execute();
            } catch (IOException e) {
                throw new MojoExecutionException("IO Error", e);
            }
        }
    }

    /**
     * This methods run the validation before any action to be taken in this mojo
     */
    private void validate() throws MojoExecutionException {
        if (!resourceBundleDirectory.exists()) {
            throw new MojoExecutionException("Resource Bundle Directory should already exist.");
        }
        if (resourceBundleGroups == null || resourceBundleGroups.size() == 0) {
            throw new MojoExecutionException("Resource Bundle Group Properties should be configured.");
        }

        @SuppressWarnings("unchecked")
        final Set<Object> objects = resourceBundleGroups.keySet();
        for (final Object key : objects) {
            final String value = (String) resourceBundleGroups.get(key);
            if (value == null || "".equals(value)) {
                throw new MojoExecutionException(String.format("Resource Bundle of %s are empty or is not configured properly. Remember it can be a CSV.", key));
            }
        }
    }
}
