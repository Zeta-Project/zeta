const Database = require('./src/database');
const data = require('./src/mock');

const USER_PORT = 4984;
const ADDRESS = 'http://database'

// save reference to database connetions
const db = {};

// database configuration
const userAddress = `${ADDRESS}:${USER_PORT}/db/`;

// create all docs
let sequence = Promise.resolve();

// create the database connections for the different users.
for (const user of data.users) {
  // add the database connection for the user
  db[user.name] = new Database(userAddress, user);
}

// Loop through all docs and create them in sequence
data.docs.forEach((cur) => {
  // Add these actions to the end of the sequence
  sequence = sequence.then(() => {
    const op = db[cur.user].put(cur.doc);
    op.then((doc) => console.log(`created ${cur.doc._id}`));
    return op;
  }).catch((err) => {
    console.log(err);
  });
});
