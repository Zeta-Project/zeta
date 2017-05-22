const request = require('requestretry');
const Database = require('./src/database');
const data = require('./src/mock');

function main() {
  var couchdbSyncGateway = process.env.npm_package_config_couchdbSyncGateway;
  setupGenerator(couchdbSyncGateway);
}

function setupGenerator(dbConnectString) {
  request.get({
    uri: dbConnectString + '_all_docs',
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
  }, createGenerator);
}

function createGenerator(error, response, body) {
  if (error) {
    console.error('Setup generator - request error:', userAddress + '_add_docs', error);
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
  const db = new Database(userAddress, data.user);

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

