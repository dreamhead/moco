import com.github.dreamhead.moco.HttpServer

import static com.github.dreamhead.moco.Moco.by
import static com.github.dreamhead.moco.Moco.uri

// Returns array of closures with services
[
	fooService = { HttpServer server ->
		server.request(by(uri("/foo"))).response "foo"
		println "Configured fooService"
	}
	,
	barService = { HttpServer server ->	
		server.request(by(uri("/bar"))).response "bar"
		println "Configured barService"
	}
]