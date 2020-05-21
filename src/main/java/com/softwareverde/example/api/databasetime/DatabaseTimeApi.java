package com.softwareverde.example.api.databasetime;

import com.softwareverde.database.*;
import com.softwareverde.database.query.*;
import com.softwareverde.database.row.*;
import com.softwareverde.example.api.response.*;
import com.softwareverde.http.querystring.*;
import com.softwareverde.http.server.servlet.*;
import com.softwareverde.http.server.servlet.request.*;
import com.softwareverde.http.server.servlet.response.*;
import com.softwareverde.util.*;

import java.util.*;

public class DatabaseTimeApi implements Servlet {
    protected final Database<?> _database;

    public DatabaseTimeApi(final Database<?> database) {
        _database = database;
    }

    @Override
    public Response onRequest(final Request request) {
        try (final DatabaseConnection<?> databaseConnection = _database.newConnection()) {
            final GetParameters getParameters = request.getGetParameters();

            if (Util.parseInt(getParameters.get("select")) > 0) {
                final List<Row> rows = databaseConnection.query(new Query("SELECT UNIX_TIMESTAMP() AS time, NOW() AS formatted_time"));
                if (rows.isEmpty()) {
                    return new JsonResponse(Response.Codes.SERVER_ERROR, new JsonResult(false, "Server error."));
                }

                final Row row = rows.get(0);
                final Long serverTime = (row.getLong("time") * 1000L);
                final String formattedTime = row.getString("formatted_time");

                final DatabaseTimeResult jsonResult = new DatabaseTimeResult();
                jsonResult.setTime(serverTime);
                jsonResult.setFormattedTime(formattedTime);
                return new JsonResponse(Response.Codes.OK, jsonResult);
            }

            return new JsonResponse(Response.Codes.BAD_REQUEST, new JsonResult(false, "Nothing to do."));
        }
        catch (final DatabaseException exception) {
            return new JsonResponse(Response.Codes.SERVER_ERROR, new JsonResult(false, "Could not connect to the database."));
        }
    }
}
