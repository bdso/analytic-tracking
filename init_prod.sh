#!/bin/sh

# Clean out dist
rm -rf target/universal/*

# Compile distributable
activator dist

# Unzip components
cd target/universal/

unzip api_eh_mics-1.0-SNAPSHOT.zip

rm -rf api_eh_mics-1.0-SNAPSHOT/RUNNING_PID

api_eh_mics-1.0-SNAPSHOT/bin/api_eh_mics -Dplay.crypto.secret=abcdefghijk -Dconfig.file=api_eh_mics-1.0-SNAPSHOT/conf/prod.conf