package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.ActualHttpServer;
import com.github.dreamhead.moco.internal.MocoHttpServer;

public class Runner {
	private static final boolean SHUT_DOWN_GRACEFULLY = true;

	public static void running(HttpServer httpServer, Runnable runnable) throws Exception {
	    doRun((ActualHttpServer) httpServer, SHUT_DOWN_GRACEFULLY, runnable);
    }
    public static void running(HttpServer httpServer, boolean shutDownGracefully, Runnable runnable) throws Exception {
	    doRun((ActualHttpServer) httpServer, shutDownGracefully, runnable);
    }

	private static void doRun(ActualHttpServer httpServer, boolean shutDownGracefully, Runnable runnable) throws Exception {
		MocoHttpServer server = new MocoHttpServer((ActualHttpServer)httpServer);
		try {
		    server.start();
		    runnable.run();
		} finally {
		    server.stop(shutDownGracefully);
		}
	}
}
