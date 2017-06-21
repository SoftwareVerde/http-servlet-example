package com.softwareverde.example.api.servertime;

import com.softwareverde.example.api.response.JsonResult;
import com.softwareverde.servlet.GetParameters;
import com.softwareverde.servlet.Servlet;
import com.softwareverde.servlet.request.Request;
import com.softwareverde.servlet.response.JsonResponse;
import com.softwareverde.servlet.response.Response;
import com.softwareverde.util.DateUtil;
import com.softwareverde.util.Util;

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
            return new JsonResponse(Response.ResponseCodes.OK, jsonResult);
        }

        return new JsonResponse(Response.ResponseCodes.BAD_REQUEST, new JsonResult(false, "Nothing to do."));
    }
}
