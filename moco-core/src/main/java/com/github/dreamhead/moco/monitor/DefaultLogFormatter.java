package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.HttpRequest;
import com.github.dreamhead.moco.HttpResponse;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;
import com.github.dreamhead.moco.dumper.Dumper;
import com.github.dreamhead.moco.dumper.HttpRequestDumper;
import com.github.dreamhead.moco.dumper.HttpResponseDumper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Optional;

public final class DefaultLogFormatter implements LogFormatter {
    private static final Map<Class<? extends Request>, Dumper<Request>> REQUEST_DUMPERS = Map.of(
            HttpRequest.class, new HttpRequestDumper()
    );

    private static final Map<Class<? extends Response>, Dumper<Response>> RESPONSE_DUMPERS = Map.of(
            HttpResponse.class, new HttpResponseDumper()
    );

    @Override
    public String format(final Request request) {
        return """
               Request received:

               %s
               """.formatted(findDumper(request, REQUEST_DUMPERS).dump(request)).stripTrailing();
    }

    @Override
    public String format(final Response response) {
        return """
               Response return:

               %s
               """.formatted(findDumper(response, RESPONSE_DUMPERS).dump(response)).stripTrailing();
    }

    @Override
    public String format(final Throwable e) {
        return """
               Exception thrown:

               %s
               """.formatted(stackTraceToString(e)).stripTrailing();
    }

    private String stackTraceToString(final Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    private <T> Dumper<T> findDumper(final T target, final Map<Class<? extends T>, Dumper<T>> dumperClasses) {
        Optional<Class<? extends T>> dumpClass = dumperClasses.keySet().stream()
                .filter(input -> input.isInstance(target))
                .findFirst();

        final Class<? extends T> clazz = dumpClass.orElseThrow(() ->
                new IllegalArgumentException("Unknown target type:" + target.getClass()));
        return dumperClasses.get(clazz);
    }
}
