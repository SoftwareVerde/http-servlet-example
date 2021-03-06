package com.softwareverde.example.configuration;

import com.softwareverde.database.properties.*;
import com.softwareverde.util.*;

import java.io.*;
import java.util.*;

public class Configuration {
    protected final Properties _properties;
    protected DatabaseProperties _databaseProperties;
    protected ServerProperties _serverProperties;

    protected void _loadDatabaseProperties() {
        _databaseProperties = new DatabaseProperties();
        _databaseProperties._hostname = _properties.getProperty("database.url", "");
        _databaseProperties._schema = _properties.getProperty("database.schema", "");
        _databaseProperties._port =  Util.parseInt(_properties.getProperty("database.port", ""));

        final String username = _properties.getProperty("database.username", "");
        final String password = _properties.getProperty("database.password", "");
        _databaseProperties._databaseCredentials = new DatabaseCredentials(username, password);
    }

    protected void _loadServerProperties() {
        _serverProperties = new ServerProperties();
        _serverProperties._rootDirectory = _properties.getProperty("server.rootDirectory", "");
        _serverProperties._port = Util.parseInt(_properties.getProperty("server.httpPort", "80"));
        _serverProperties._tlsPort = Util.parseInt(_properties.getProperty("server.tlsPort", "443"));
        _serverProperties._socketPort = Util.parseInt(_properties.getProperty("server.socketPort", "444"));
        _serverProperties._tlsCertificateFile = _properties.getProperty("server.tlsCertificateFile", "");
        _serverProperties._tlsKeyFile = _properties.getProperty("server.tlsKeyFile", "");
    }

    public Configuration(final File configurationFile) {
        _properties = new Properties();

        try {
            _properties.load(new FileInputStream(configurationFile));
        }
        catch (final IOException exception) { }

        _loadDatabaseProperties();

        _loadServerProperties();

    }

    public DatabaseProperties getDatabaseProperties() { return _databaseProperties; }
    public ServerProperties getServerProperties() { return _serverProperties; }
}
