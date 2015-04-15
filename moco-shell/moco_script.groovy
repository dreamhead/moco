import com.github.dreamhead.moco.HttpServer
import com.github.dreamhead.moco.internal.*

import static com.github.dreamhead.moco.Moco.*

SERVER_PORT = this.args[0] as Integer
SCRIPT_CONFIGURATION = "${this.args[1]}"

println "Starting Moco on port [$SERVER_PORT] and configuration defined in script [$SCRIPT_CONFIGURATION]"
println "Grabbing necessary dependencies. Please wait patiently"

@Grapes(
@Grab(group='com.github.dreamhead', module='moco-core', version='[0.10.0,)')
)
configureServer = { HttpServer server ->
    evaluate(new File(SCRIPT_CONFIGURATION)).each { it server }
    new MocoHttpServer((ActualHttpServer)server)
}

println "Dependencies downloaded... Proceeding with setting up Moco"
configureServer(httpServer(SERVER_PORT)).start()
println "Moco has started successfully!"
