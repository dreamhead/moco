#!/usr/bin/env bash

# Ensure this file is executable via `chmod a+x lein`, then place it
# somewhere on your $PATH, like ~/bin. The rest of moco will be
# installed upon first run into the ~/.moco directory.

if [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "msys" ]]; then
    delimiter=";"
else
    delimiter=":"
fi

if [ `id -u` -eq 0 ] && [ "$MOCO_ROOT" = "" ]; then
    echo "WARNING: You're currently running as root; probably by accident."
    echo "Press control-C to abort or Enter to continue as root."
    echo "Set MOCO_ROOT to disable this warning."
    read _
fi

export MOCO_HOME="${MOCO_HOME:-"$HOME/.moco"}"
VERSION_LOG_FILE="$MOCO_HOME/.version"

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

if [ "$HTTP_CLIENT" = "" ]; then
    if type -p curl >/dev/null 2>&1; then
        if [ "$https_proxy" != "" ]; then
            CURL_PROXY="-x $https_proxy"
        fi
        HTTP_CLIENT="curl $CURL_PROXY -f -L -o"
    else
        HTTP_CLIENT="wget -O"
    fi
fi

function download_failed_message {
    echo "Failed to download $1"
    echo "It's possible your HTTP client's certificate store does not have the"
    echo "correct certificate authority needed. This is often caused by an"
    echo "out-of-date version of libssl. Either upgrade it or set HTTP_CLIENT"
    echo "to turn off certificate checks:"
    echo "  export HTTP_CLIENT=\"wget --no-check-certificate -O\" # or"
    echo "  export HTTP_CLIENT=\"curl --insecure -f -L -o\""
    echo "It's also possible that you're behind a firewall haven't yet"
    echo "set HTTP_PROXY and HTTPS_PROXY."
}


function download {
    $HTTP_CLIENT "$2.pending" "$1"
    if [ $? == 0 ]; then
        # TODO: checksum
        mv -f "$2.pending" "$2"
    else
        rm "$2.pending" 2> /dev/null
        download_failed_message "$1"
        exit 1
    fi
}

function parse_tag {
   tag_value=`grep "<$2>.*<.$2>" $1 | sed -e "s/^.*<$2/<$2/" | cut -f2 -d">"| cut -f1 -d"<"`
}

function parse_maven_metadata {
    MOCO_METADATA_URL="https://oss.sonatype.org/content/repositories/snapshots/com/github/dreamhead/moco-runner/maven-metadata.xml"
    MOCO_METADATA="/tmp/maven-metadata.xml"
    download $MOCO_METADATA_URL $MOCO_METADATA
    parse_tag $MOCO_METADATA latest
    LATEST_VERSION=$tag_value
}

function parse_latest_maven_metadata {
    parse_maven_metadata
    MOCO_LATEST_MAVEN_METADATA_URL="https://oss.sonatype.org/content/repositories/snapshots/com/github/dreamhead/moco-runner/$LATEST_VERSION/maven-metadata.xml"
    MOCO_LATEST_MAVEM_METADATA="/tmp/latest-maven-metadata.xml"
    download $MOCO_LATEST_MAVEN_METADATA_URL $MOCO_LATEST_MAVEM_METADATA
    parse_tag $MOCO_LATEST_MAVEM_METADATA timestamp
    TIMESTAMP=$tag_value
    parse_tag $MOCO_LATEST_MAVEM_METADATA buildNumber
    BUILD_NUMBER=$tag_value

}

function parse_standalone_latest_url {
    parse_latest_maven_metadata
    VERSION=${LATEST_VERSION%-SNAPSHOT}
    LATEST_MOCO_STANDALONE_JAR="moco-runner-$VERSION-$TIMESTAMP-$BUILD_NUMBER-standalone.jar"
    MOCO_STANDLONE_URL="https://oss.sonatype.org/content/repositories/snapshots/com/github/dreamhead/moco-runner/$LATEST_VERSION/$LATEST_MOCO_STANDALONE_JAR"
}

function install {
    echo "Install moco"
    echo "Parse the latest version of moco"
    parse_standalone_latest_url
    echo "Download the latest moco: $LATEST_VERSION"
    MOCO_STANDALONE="$MOCO_HOME/$LATEST_MOCO_STANDALONE_JAR"
    echo "$MOCO_STANDALONE $LATEST_VERSION" >> $VERSION_LOG_FILE
    download $MOCO_STANDLONE_URL $MOCO_STANDALONE
}

function load_current_version {
    read MOCO_STANDALONE CURRENT_VERSION < $VERSION_LOG_FILE

}


function usage {
  printf "
options:
       help: Show help
       start: Start Server. Use moco start -p 12306 configfile.json to start moco
       upgrade: Upgrade the moco
       *: Show help
"
}


if [ ! -e "$MOCO_HOME" ]
then
    mkdir "$MOCO_HOME"
    install
fi

if [ "$1" = "start" ]; then
    echo "Starting..."
    OTHER_PARAMETERS=${*%%start}
    load_current_version
    exec "$JAVACMD" -jar "$MOCO_STANDALONE" $OTHER_PARAMETERS &
elif [ "$1" = "shutdown" ]; then
    load_current_version
    exec "$JAVACMD" -jar "$MOCO_STANDALONE" $1
elif [ "$1" = "upgrade" ]; then
    echo "Check the new version"
    parse_maven_metadata
    load_current_version

    if [ "$LATEST_VERSION" = "$CURRENT_VERSION" ]; then
        echo "The current version of moco is the latest"
    else
        echo "Upgrading..."
        rm $VERSION_LOG_FILE
        install
    fi
elif [ "$1" = "help" ]; then
    usage
else
    usage
fi