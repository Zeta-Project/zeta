pushd api
sbt 'project scalaFilter' 'docker:publishLocal'
sbt 'project basicGenerator' 'docker:publishLocal'
sbt 'project fileGenerator' 'docker:publishLocal'
sbt 'project specificGenerator' 'docker:publishLocal'
sbt 'project remoteGenerator' 'docker:publishLocal'
popd

docker-compose build

