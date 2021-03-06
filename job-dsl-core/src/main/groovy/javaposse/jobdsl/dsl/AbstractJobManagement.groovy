package javaposse.jobdsl.dsl

/**
 * Abstract base class providing common functionality for all {@link JobManagement} implementations.
 */
abstract class AbstractJobManagement implements JobManagement {
    final PrintStream outputStream
    private String lastWarning

    protected AbstractJobManagement(PrintStream out) {
        this.outputStream = out
    }

    @Override
    boolean createOrUpdateConfig(String path, String config, boolean ignoreExisting) {
        Item item = new Item(this) {
            @Override
            String getName() {
                path
            }

            @Override
            String getXml() {
                config
            }

            @Override
            Node getNode() {
                throw new UnsupportedOperationException()
            }
        }
        createOrUpdateConfig(item, ignoreExisting)
    }

    @Override
    void logDeprecationWarning() {
        List<StackTraceElement> currentStackTrack = DslScriptHelper.stackTrace
        String details = DslScriptHelper.getSourceDetails(currentStackTrack)
        logDeprecationWarning(currentStackTrack[0].methodName, details)
    }

    @Override
    void logDeprecationWarning(String subject) {
        logDeprecationWarning(subject, DslScriptHelper.sourceDetails)
    }

    @Override
    void logDeprecationWarning(String subject, String scriptName, int lineNumber) {
        logDeprecationWarning(subject, DslScriptHelper.getSourceDetails(scriptName, lineNumber))
    }

    @Override
    void requirePlugin(String pluginShortName) {
        requirePlugin(pluginShortName, false)
    }

    @Override
    void requireMinimumPluginVersion(String pluginShortName, String version) {
        requireMinimumPluginVersion(pluginShortName, version, false)
    }

    protected void logDeprecationWarning(String subject, String details) {
        logWarning("${subject} is deprecated", details)
    }

    protected static void validateUpdateArgs(String jobName, String config) {
        validateNameArg(jobName)
        validateConfigArg(config)
    }

    protected static void validateConfigArg(String config) {
        if (config == null || config.empty) {
            throw new ConfigurationMissingException()
        }
    }

    protected static void validateNameArg(String name) {
        if (name == null || name.empty) {
            throw new NameNotProvidedException()
        }
    }

    /**
     * @since 1.36
     */
    protected void logWarning(String message, String details = DslScriptHelper.getSourceDetails()) {
        String warning = "Warning: ($details) $message"
        if (warning != lastWarning) {
            outputStream.println(warning)
            lastWarning = warning
        }
    }
}
