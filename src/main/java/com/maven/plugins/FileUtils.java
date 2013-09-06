package com.maven.plugins;

import org.codehaus.plexus.util.StringUtils;

import java.io.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Chris Shayan
 */
public class FileUtils {
    /**
     * Load the property files into an appropriate format that is being used by {@linkplain com.maven.plugins.ResourceBundleMissingKeyValidator}
     * @param resourceBundleDirectory resource bundle directory
     * @param fileNamesCsv file names in CSV format
     * @return An appropriate format to process the further processing
     */
    public static Map<String, Properties> preparePropertyFiles(final File resourceBundleDirectory, final String fileNamesCsv) throws IOException {
        final String[] fileNames = StringUtils.split(fileNamesCsv, ",");
        final Map<String, Properties> propertiesMap = new HashMap<String, Properties>(fileNames.length);
        for (final String fileName : fileNames) {
            final File file = resourceBundleDirectory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (MessageFormat.format("{0}.properties", fileName)).equals(name);
                }
            })[0];
            Properties properties = new Properties();
            properties.load(new FileInputStream(file.getPath()));

            propertiesMap.put(fileName, properties);
        }

        return propertiesMap;
    }
}
