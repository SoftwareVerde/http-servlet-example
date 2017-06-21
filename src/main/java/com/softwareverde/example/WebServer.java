package com.softwareverde.example;

import com.softwareverde.database.Database;
import com.softwareverde.example.api.databasetime.DatabaseTimeApi;
import com.softwareverde.example.api.servertime.ServerTimeApi;
import com.softwareverde.httpserver.DirectoryServlet;
import com.softwareverde.httpserver.HttpServer;
import com.softwareverde.servlet.Endpoint;
import com.softwareverde.servlet.Servlet;

import java.io.File;

public class WebServer {
    private final Configuration.ServerProperties _serverProperties;
    private final Database _database;

    private final HttpServer _apiServer = new HttpServer();

    private <T extends Servlet> void _assignEndpoint(final String path, final T servlet) {
        final Endpoint endpoint = new Endpoint(servlet);
        endpoint.setPath(path);
        endpoint.setStrictPathEnabled(true);
        _apiServer.addEndpoint(endpoint);
    }

    public WebServer(final Configuration.ServerProperties serverProperties, final Database database) {
        _serverProperties = serverProperties;
        _database = database;
    }

    public void start() {
        _apiServer.setPort(_serverProperties.getPort());

        _apiServer.setTlsPort(_serverProperties.getTlsPort());
        _apiServer.setCertificate(_serverProperties.getTlsCertificateFile(), _serverProperties.getTlsKeyFile());
        _apiServer.enableEncryption(true);
        _apiServer.redirectToTls(false);

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
    }

    public void stop() {
        _apiServer.stop();
    }
}