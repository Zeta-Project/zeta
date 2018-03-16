#!/bin/sh
sbt 'project scalaFilter' 'docker:publishLocal'
sbt 'project basicGenerator' 'docker:publishLocal'

