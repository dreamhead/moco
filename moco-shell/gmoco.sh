#!/bin/bash
if (( $# != 2 ))
then
	echo "Usage: run.sh [PORT_NUMBER] [CONFIG_SCRIPT_NAME]"
	echo "Example: run.sh 8888 moco_config_script"
	echo "will run moco at port 8888 with configuration from moco_config_script.groovy"
	exit 1
fi

if env | grep -q ^GROOVY_HOME=
then
	echo "Groovy is already installed here $GROOVY_HOME"
else
	echo "Groovy not found - will have to download it"
	GROOVY_DOWNLOAD_DIR="/home/$USER/.groovy" 
	GROOVY_VERSION="2.1.9"

	if [ ! -d $GROOVY_DOWNLOAD_DIR ]
	then
		mkdir $GROOVY_DOWNLOAD_DIR
		echo "Downloading groovy binary to $GROOVY_DOWNLOAD_DIR"
		wget http://dist.groovy.codehaus.org/distributions/groovy-binary-$GROOVY_VERSION.zip -P $GROOVY_DOWNLOAD_DIR
		unzip $GROOVY_DOWNLOAD_DIR/groovy-binary-$GROOVY_VERSION.zip -d $GROOVY_DOWNLOAD_DIR/
	fi
	GROOVY_HOME="$GROOVY_DOWNLOAD_DIR/groovy-$GROOVY_VERSION"
	echo "Binding GROOVY_HOME to $GROOVY_HOME"  
fi

nohup $GROOVY_HOME/bin/groovy moco_script.groovy $1 $2 > moco.log 2>&1&
echo "Running Moco with PID [$!]"