#!/bin/bash
java -XX:InitialRAMPercentage=40.0 -XX:MaxRAMPercentage=50.0 -jar -Dspring.profiles.active=${ACTIVE_PROFILE} api-0.0.1.jar
