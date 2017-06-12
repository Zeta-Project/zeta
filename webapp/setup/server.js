const request = require('requestretry');
const Database = require('./src/database');
const data = require('./src/mock');
const CouchbaseServer = require('./couchbase-server.js');

function main() {
  var couchdbSyncGateway = process.env.npm_package_config_couchdbSyncGateway;
  var settings = {
    address: process.env.npm_package_config_couchdbServer,
    bucket: process.env.npm_package_config_couchdbServerBucket,
    username: process.env.npm_package_config_couchdbServerUsername,
    password: process.env.npm_package_config_couchdbServerPassword,
  };

  console.log('CouchbaseServer - setup started');
  var couchDb = new CouchbaseServer(settings.address);
  couchDb.getBucket(settings.bucket).then(({response, body}) => {
    if (response.statusCode === 200) {
      console.log('CouchbaseServer - bucket already exist. Not setup needed');
      setupGenerator(couchdbSyncGateway);
    } else {
      
      couchDb.createNode({
        'path': '/opt/couchbase/var/lib/couchbase/data',
        'index_path': '/opt/couchbase/var/lib/couchbase/data',
      }).then(() => {
        console.log('CouchbaseServer - create node was successful');
        return couchDb.renameNode({ 'hostname': '127.0.0.1' });
      }).then(() => {
        console.log('CouchbaseServer - rename node was successful');
        return couchDb.setIndex({ 'storageMode': 'forestdb' });
      }).then(() => {
        console.log('CouchbaseServer - set index was successful');
        return couchDb.setupNodeServices({ 'services': 'kv,index,n1ql' });
      }).then(() => {
        console.log('CouchbaseServer - setup services was successful');
        return couchDb.setPool({ 'indexMemoryQuota': 256, 'memoryQuota': 512 });
      }).then(() => {
        console.log('CouchbaseServer - set ram index quota was successful');
        return couchDb.createBucket({
          'authType': 'sasl',
          'bucketType': 'membase',
          'evictionPolicy': 'valueOnly',
          'flushEnabled': 0,
          'ignoreWarnings': true,
          'isNew': true,
          'isWizard': true,
          'name': settings.bucket,
          'otherBucketsRamQuotaMB': 0,
          'ramQuotaMB': 100,
          'replicaIndex': 0,
          'replicaNumber': 0,
          'threadsNumber': 3
        });
      }).then(() => {
        console.log('CouchbaseServer - create bucket was successful');
        return couchDb.setStats({ 'sendStats': false });
      }).then(() => {
        console.log('CouchbaseServer - disable stats was successful');
        return couchDb.createUser({ 'username': settings.username, 'password': settings.password, 'port': 'SAME' });
      }).then(() => {
        console.log('CouchbaseServer - create user was successful');
        return couchDb.login({ 'username': settings.username, 'password': settings.password });
      }).then(() => {
        console.log('CouchbaseServer - login was successful');
        console.log('CouchbaseServer - setup finished');
        setupGenerator(couchdbSyncGateway);
      }).catch((reason) => console.error('CouchbaseServer setup failed', reason));
    }
  });
}


function setupGenerator(dbConnectString) {
  request.get({
    url: dbConnectString + '_all_docs',
    auth: {
      username: data.user.name,
      password: data.user.password,
    },
    headers: {
      'Accept': 'application/json',
    },
    json: true,
    maxAttempts: 5,
    retryDelay: 5000,
    retryStrategy: request.RetryStrategies.HTTPOrNetworkError
  }, (error, response, body) => createGenerator({ error, response, body, dbConnectString }));
}

function createGenerator({ error, response, body, dbConnectString }) {
  if (error) {
    console.error('Setup generator - request error:', error);
    process.exit(1);
  }
  if (!response || !response.statusCode === 200) {
    console.error('Setup generator - response failed:', response);
    process.exit(1);
  }
  if (body.error) {
    console.error('Setup generator - error in response:', body);
    process.exit(1);
  }

  if (body.total_rows > 0) {
    console.log('Setup generator - data already exists. No setup needed.');
    process.exit(0);
  }

  // create all docs
  let sequence = Promise.resolve();

  // create the database connections
  const db = new Database(dbConnectString, data.user);

  data.docs.forEach((cur) => {
    // Add these actions to the end of the sequence
    sequence = sequence.then(() => {
      const op = db.put(cur.doc);
      op.then((doc) => console.log(`Setup - created ${cur.doc._id}`));
      return op;
    }).catch((err) => {
      console.log(err);
    });
  });
}

main();

