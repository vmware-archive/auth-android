#!/bin/bash

set -e
set -x

./gradlew clean connectedAndroidTest
