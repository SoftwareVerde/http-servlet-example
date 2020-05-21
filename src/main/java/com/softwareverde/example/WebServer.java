package com.softwareverde.example;

import com.softwareverde.database.*;
import com.softwareverde.example.api.databasetime.*;
import com.softwareverde.example.api.servertime.*;
import com.softwareverde.example.configuration.*;
import com.softwareverde.http.server.*;
import com.softwareverde.http.server.endpoint.*;
import com.softwareverde.http.server.servlet.*;
import com.softwareverde.logging.*;
import com.softwareverde.util.Util;

import java.io.*;

public class WebServer {
    protected final ServerProperties _serverProperties;
    protected final Database<?> _database;

    protected final HttpServer _apiServer = new HttpServer();

    protected <T extends Servlet> void _assignEndpoint(final String path, final T servlet) {
        final Endpoint endpoint = new Endpoint(servlet);
        endpoint.setPath(path);
        endpoint.setStrictPathEnabled(true);
        _apiServer.addEndpoint(endpoint);
    }

    protected Boolean _isSslEnabled() {
        if (_serverProperties.getTlsPort() < 1) {
            return false;
        }

        if (Util.isBlank(_serverProperties.getTlsCertificateFile())) {
            return false;
        }

        if (Util.isBlank(_serverProperties.getTlsKeyFile())) {
            return false;
        }

        return true;
    }

    public WebServer(final ServerProperties serverProperties, final Database<?> database) {
        _serverProperties = serverProperties;
        _database = database;
    }

    public void start() {
        _apiServer.setPort(_serverProperties.getPort());

        final boolean sslIsEnabled = _isSslEnabled();

        { // Configure SSL/TLS...
            if (sslIsEnabled) {
                _apiServer.setTlsPort(_serverProperties.getTlsPort());
                _apiServer.setCertificate(_serverProperties.getTlsCertificateFile(), _serverProperties.getTlsKeyFile());
            }

            _apiServer.enableEncryption(sslIsEnabled);
            _apiServer.redirectToTls(false);
        }

        { // Server Time Api
            // Path:                /api/server/time
            // GET (Methods):       select
            // POST (Parameters):
            _assignEndpoint("/api/server/time", new ServerTimeApi());
        }

        { // Server Time Api
            // Path:                /api/database/time
            // GET (Methods):       select
            // POST (Parameters):
            _assignEndpoint("/api/database/time", new DatabaseTimeApi(_database));
        }

        { // Static Content
            final File servedDirectory = new File(_serverProperties.getRootDirectory() +"/");
            final DirectoryServlet indexServlet = new DirectoryServlet(servedDirectory);
            indexServlet.setShouldServeDirectories(true);
            indexServlet.setIndexFile("index.html");

            final Endpoint endpoint = new Endpoint(indexServlet);
            endpoint.setPath("/");
            endpoint.setStrictPathEnabled(false);
            _apiServer.addEndpoint(endpoint);
        }

        _apiServer.start();

        final Integer httpPort = _serverProperties.getPort();
        final Integer tlsPort = _serverProperties.getTlsPort();
        Logger.debug("[Server Listening on " + httpPort + (sslIsEnabled ? (" / " + tlsPort) : "") + "]");
    }

    public void stop() {
        _apiServer.stop();
    }
}