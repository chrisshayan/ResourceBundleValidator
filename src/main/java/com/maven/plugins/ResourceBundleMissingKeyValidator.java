package com.maven.plugins;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.util.*;

/**
 * @author Chris Shayan
 */
public class ResourceBundleMissingKeyValidator {
    final static Log LOGGER = new SystemStreamLog();

    private Map<String, Properties> resourceBundles;

    public ResourceBundleMissingKeyValidator(Map<String, Properties> resourceBundles) {
        this.resourceBundles = resourceBundles;
    }

    /**
     * This method will look for missing keys
     *
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        final Map<String, Integer> sizes = new HashMap<String, Integer>(resourceBundles.size());
        final Map<String, List<String>> resourceBundleKeys = new HashMap<String, List<String>>(resourceBundles.size());

        initialize(sizes, resourceBundleKeys);
        validate(resourceBundleKeys, union(resourceBundleKeys));
    }

    /**
     * Validate the keys
     * @param resourceBundleKeys bundles
     * @param union A union of property files
     * @throws MojoExecutionException See: {@linkplain MojoExecutionException}
     */
    @SuppressWarnings("unchecked")
    private void validate(Map<String, List<String>> resourceBundleKeys, List<String> union) throws MojoExecutionException {
        boolean shouldThrowException = false;
        final Set<String> keys = resourceBundleKeys.keySet();
        for (String key : keys) {
            final List<String> subtracted = (List<String>) CollectionUtils.subtract(union, resourceBundleKeys.get(key));
            if(CollectionUtils.isNotEmpty(subtracted)) {
                LOGGER.error(String.format("Following keys in file [%s] are missing:%s", key, subtracted));
                shouldThrowException = true;
            }
        }
        if(shouldThrowException) {
            throw new MojoExecutionException("Resource Bundles are not sync.");
        }
    }

    /**
     * Make a union of keys of different resource bundles
     * @param resourceBundleKeys key is the resournce bundle name / value is the list of keys within properties
     * @return A union of property files
     */
    @SuppressWarnings("unchecked")
    private List<String> union(Map<String, List<String>> resourceBundleKeys) {
        List<String> union = new ArrayList<String>();
        final Set<String> keys = resourceBundleKeys.keySet();
        for (String key : keys) {
            union = (List<String>) CollectionUtils.union(union, resourceBundleKeys.get(key));
        }
        return union;
    }

    /**
     * Loads the basic data
     */
    private void initialize(final Map<String, Integer> sizes, final Map<String, List<String>> resourceBundleKeys) {
        final Set<String> keys = resourceBundles.keySet();
        CollectionUtils.forAllDo(keys, new Closure() {
            /** {@inheritDoc} */
            @Override
            public void execute(Object input) {
                final String key = (String) input;
                final Properties properties = resourceBundles.get(key);
                sizes.put(key, properties.size());

                final Set<Object> rbKeys = properties.keySet();
                final List<String> rbKeysHolder = new ArrayList<String>(rbKeys.size());
                for (Object rbKey : rbKeys) {
                    rbKeysHolder.add((String) rbKey);
                }
                resourceBundleKeys.put(key, rbKeysHolder);
            }
        });
    }

}
