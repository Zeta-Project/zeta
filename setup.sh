pushd api
sbt 'project backend' 'docker:publishLocal'
sbt 'project server' 'docker:publishLocal'
sbt 'project scalaFilter' 'docker:publishLocal'
sbt 'project basicGenerator' 'docker:publishLocal'
sbt 'project fileGenerator' 'docker:publishLocal'
sbt 'project specificGenerator' 'docker:publishLocal'
sbt 'project remoteGenerator' 'docker:publishLocal'
popd

docker-compose build

pushd data
docker build -t modigen:data .
popd
