const PouchDB = require('pouchdb');
const request = require('request-promise');

class Database {
  constructor(address, user) {
    this.address = address;
    this.user = user;
    this.db = new PouchDB(address, {
      ajax: {
        cache: false,
        timeout: 10000,
      },
      auth: {
        username: user.name,
        password: user.password,
      },
    });
  }

  // Create new document
  put(doc) {
    return new Promise((resolve, reject) => {
      this.db.put(doc)
      .then((response) => {
        doc._rev = response.rev;
        resolve(doc);
      })
      .catch((error) => {
        reject(error.message);
      });
    });
  }

  // Update a document
  post(doc) {
    return new Promise((resolve, reject) => {
      this.db.post(doc)
      .then((response) => {
        doc._rev = response.rev;
        resolve(doc);
      })
      .catch((error) => {
        const message = `! Creating document failed!
          document: ${JSON.stringify(doc, null, 4)}
          error: ${error.message}
        `;
        reject(message);
      });
    });
  }

  // Get a document by id or document itself
  get(doc) {
    const id = (doc !== null && typeof doc === 'object') ? doc._id : doc;

    return new Promise((resolve, reject) => {
      this.db.get(id)
      .then((response) => {
        resolve(response);
      })
      .catch((error) => {
        reject(error.message);
      });
    });
  }

  // Remove a document
  remove(doc) {
    return new Promise((resolve, reject) => {
      this.db.remove(doc)
      .then(() => {
        resolve(doc);
      })
      .catch((error) => {
        reject(error.message);
      });
    });
  }

  // put an attachment
  putAttachment(doc, attachmentId, file, type) {
    return new Promise((resolve, reject) => {
      const options = {
        method: 'PUT',
        uri: `${this.address + doc._id}/${attachmentId}?rev=${doc._rev}`,
        auth: {
          username: this.user.name,
          password: this.user.password,
        },
        headers: {
          'content-type': 'text/plain',
        },
        body: file,
      };
      request(options)
      .then((response) => {
        response = JSON.parse(response);
        doc._rev = response.rev;
        resolve(doc);
      })
      .catch((err) => {
        const msg = (err && err.error && err.error.reason) ?
          err.error.reason : 'unknown error';
        reject(msg);
      });
    });
  }

  // get an attachment
  getAttachment(doc, attachmentId) {
    return new Promise((resolve, reject) => {
      this.db.getAttachment(doc._id, attachmentId)
      .then((blob) => {
        // parse data and return as buffer
        resolve(new Buffer(JSON.parse(blob)));
      }).catch((error) => {
        reject(error.message);
      });
    });
  }
}

module.exports = Database;
