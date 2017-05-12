const request = require('requestretry');
const Database = require('./src/database');
const data = require('./src/mock');

const USER_PORT = 4984;
const ADDRESS = 'http://database'

// database configuration
const userAddress = `${ADDRESS}:${USER_PORT}/db/`;

// create all docs
let sequence = Promise.resolve();

// create the database connections
const db = new Database(userAddress, data.user);

request.get({
  uri: userAddress + '_all_docs',
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
}, function (error, response, body) {
  if (error) {
    console.error('Setup - request error:', userAddress + '_add_docs', error);
    process.exit(1);
  }
  if (!response || !response.statusCode === 200) {
    console.error('Setup - response failed:', response);
    process.exit(1);
  }
  if (body.error) {
    console.error('Setup - error in response:', body);
    process.exit(1);
  }

  if (body.total_rows > 0) {
    console.log('Setup - data already exists. No setup needed.');
    process.exit(0);
  }

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
});

