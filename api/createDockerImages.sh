#!/bin/sh
sbt 'project scalaFilter' 'docker:publishLocal'
sbt 'project generator' 'docker:publishLocal'

