package com.github.dreamhead.moco.junit5;

import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.Runner;
import com.github.dreamhead.moco.resource.ContentResource;
import com.github.dreamhead.moco.resource.Resource;
import com.google.common.base.Strings;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import static com.github.dreamhead.moco.Moco.file;
import static com.github.dreamhead.moco.Moco.pathResource;
import static com.github.dreamhead.moco.MocoJsonRunner.jsonHttpServer;
import static com.github.dreamhead.moco.MocoJsonRunner.jsonHttpsServer;
import static com.github.dreamhead.moco.Runner.runner;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

public final class MocoExtension implements TestInstancePostProcessor, BeforeEachCallback, AfterEachCallback, Extension {
    private static final ExtensionContext.Namespace MOCO = create("com.github.dreamhead.moco.junit5");
    private static final String SERVER = "server";

    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        Runner runner = context.getStore(MOCO).get(SERVER, Runner.class);
        runner.stop();
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        Runner runner = context.getStore(MOCO).get(SERVER, Runner.class);
        if (runner == null) {
            throw new IllegalStateException("No Moco server found. Please check if @MocoConfiguration is added.");
        }

        runner.start();
    }

    @Override
    public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) throws Exception {
        Class<?> testInstanceClass = testInstance.getClass();
        MocoHttpServer configuration = testInstanceClass.getAnnotation(MocoHttpServer.class);
        MocoCertificate certificate = testInstanceClass.getAnnotation(MocoCertificate.class);

        if (configuration == null) {
            throw new IllegalStateException("No Moco server found. Please check if @MocoConfiguration is added.");
        }

        context.getStore(MOCO).put(SERVER, runner(newServer(configuration, certificate)));
    }

    private HttpServer newServer(final MocoHttpServer configuration, final MocoCertificate certificate) {
        Resource resource = getResource(configuration);
        int port = configuration.port();

        if (certificate != null) {
            return jsonHttpsServer(port, resource, newCertificate(certificate));
        }

        return jsonHttpServer(port, resource);
    }

    private HttpsCertificate newCertificate(final MocoCertificate certificate) {
        return HttpsCertificate.certificate(getResource(certificate), certificate.keyStorePassword(), certificate.certPassword());
    }

    private ContentResource getResource(final MocoHttpServer configuration) {
        return getResource(configuration.filepath(), configuration.classpath());
    }

    private ContentResource getResource(final MocoCertificate certificate) {
        return getResource(certificate.filepath(), certificate.classpath());
    }

    private static ContentResource getResource(final String filepath, final String classpath) {
        if (!Strings.isNullOrEmpty(filepath)) {
            return file(filepath);
        }

        if (!Strings.isNullOrEmpty(classpath)) {
            return pathResource(classpath);
        }

        throw new IllegalArgumentException("No configuration found");
    }
}
