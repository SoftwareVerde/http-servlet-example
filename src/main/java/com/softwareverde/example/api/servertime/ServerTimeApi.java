package com.softwareverde.example.api.servertime;

import com.softwareverde.example.api.response.*;
import com.softwareverde.http.querystring.*;
import com.softwareverde.http.server.servlet.*;
import com.softwareverde.http.server.servlet.request.*;
import com.softwareverde.http.server.servlet.response.*;
import com.softwareverde.util.*;

public class ServerTimeApi implements Servlet {
    public ServerTimeApi() { }

    @Override
    public Response onRequest(final Request request) {
        final GetParameters getParameters = request.getGetParameters();

        if (Util.parseInt(getParameters.get("select")) > 0) {
            final Long serverTime = System.currentTimeMillis();
            final String formattedTime = DateUtil.timestampToDatetimeString(serverTime);

            final ServerTimeResult jsonResult = new ServerTimeResult();
            jsonResult.setTime(serverTime);
            jsonResult.setFormattedTime(formattedTime);
            return new JsonResponse(Response.Codes.OK, jsonResult);
        }

        return new JsonResponse(Response.Codes.BAD_REQUEST, new JsonResult(false, "Nothing to do."));
    }
}
