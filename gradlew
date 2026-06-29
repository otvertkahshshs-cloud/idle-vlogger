#!/bin/sh
APP_HOME=$(dirname "$0")
exec "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" 2>/dev/null || true
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
