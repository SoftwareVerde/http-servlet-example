package com.softwareverde.example;

import com.softwareverde.database.mysql.*;
import com.softwareverde.example.configuration.*;
import com.softwareverde.logging.*;
import com.softwareverde.logging.log.*;

import java.io.*;

public class Main {
    protected static void _exitFailure() {
        System.exit(1);
    }

    protected static void _printError(final String errorMessage) {
        System.err.println(errorMessage);
    }

    protected static void _printUsage() {
        _printError("Usage: java -jar " + System.getProperty("java.class.path") + " <configuration-file>");
    }

    protected static MysqlDatabase _loadDatabase(final DatabaseProperties databaseProperties) {
        final MysqlDatabase database = new MysqlDatabase(databaseProperties);
        database.setSchema(databaseProperties.getSchema());
        return database;
    }

    protected static Configuration _loadConfigurationFile(final String configurationFilename) {
        final File configurationFile =  new File(configurationFilename);
        if (! configurationFile.isFile()) {
            _printError("[ERROR: Invalid configuration file.]");
            _exitFailure();
        }

        return new Configuration(configurationFile);
    }

    public static void main(final String[] commandLineArguments) {
        Logger.setLog(AnnotatedLog.getInstance());
        Logger.setLogLevel(LogLevel.ON);
        Logger.setLogLevel("com.softwareverde.util", LogLevel.ERROR);

        if (commandLineArguments.length != 1) {
            _printUsage();
            _exitFailure();
        }

        final String configurationFilename = commandLineArguments[0];

        final Configuration configuration = _loadConfigurationFile(configurationFilename);
        final MysqlDatabase database = _loadDatabase(configuration.getDatabaseProperties());

        final ServerProperties serverProperties = configuration.getServerProperties();

        Logger.debug("[Starting Web Server]");
        final WebServer webServer = new WebServer(serverProperties, database);
        webServer.start();

        while (true) {
            try { Thread.sleep(500); } catch (final Exception e) { }
        }
    }
}