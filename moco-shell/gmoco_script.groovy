import com.github.dreamhead.moco.HttpServer
import com.github.dreamhead.moco.internal.*

import static com.github.dreamhead.moco.Moco.*

SERVER_PORT = this.args[0] as Integer
SCRIPT_CONFIGURATION = "${this.args[1]}"
GMOCO_PID_FILE_NAME = this.args.length > 2 ? this.args[2] : null

println "Starting Moco on port [$SERVER_PORT] and configuration defined in script [$SCRIPT_CONFIGURATION]"

Runtime.runtime.addShutdownHook {
    println "Shutting down..."
    if(GMOCO_PID_FILE_NAME) {        
        File pidFile = new File(GMOCO_PID_FILE_NAME)
        if(pidFile.exists()){
            println "Removing the [$GMOCO_PID_FILE_NAME] file containing the pid"
            pidFile.delete()
        }
    }
}

println "Grabbing necessary dependencies. Please wait patiently"
@Grapes(
@Grab(group='com.github.dreamhead', module='moco-core', version='[0.9,)')
)
configureServer = { HttpServer server ->
    evaluate(new File(SCRIPT_CONFIGURATION)).each { it server }
    new MocoHttpServer((ActualHttpServer)server)
}

println "Dependencies downloaded... Proceeding with setting up Moco"
configureServer(httpserver(SERVER_PORT)).start()
println "Moco has started successfully!"
