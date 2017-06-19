const request = require('requestretry');

class CouchbaseServer {
  constructor(address) {
    this.address = address;
  }

  getBucket(bucket) {
    return new Promise((resolve, reject) => {
      request.get({
        url: this.address + '/pools/default/buckets/' + bucket,
        maxAttempts: 5,
        retryDelay: 5000,
        retryStrategy: request.RetryStrategies.HTTPOrNetworkError
      }, (error, response, body) => {
        if (error) {
          reject(error);
        } else {
          resolve({ response, body });
        }
      });
    });
  }

  _post(url, data) {
    return new Promise((resolve, reject) => {
      request.post({
        url: this.address + url,
        form: data
      }, (error, response, body) => {
        if (error) {
          reject(error);
        } else {
          resolve({ response, body });
        }
      });
    });
  }

  createNode(data) {
    return this._post('/nodes/self/controller/settings', data);
  }

  renameNode(data) {
    return this._post('/node/controller/rename', data);
  }

  setIndex(data) {
    return this._post('/settings/indexes', data);
  }

  setupNodeServices(data) {
    return this._post('/node/controller/setupServices', data);
  }

  setPool(data) {
    return this._post('/pools/default', data);
  }

  createBucket(data) {
    return this._post('/pools/default/buckets', data);
  }

  setStats(data) {
    return this._post('/settings/stats', data);
  }

  createUser(data) {
    return this._post('/settings/web', data);
  }

  login(data) {
    return this._post('/uilogin', data);
  }
}

module.exports = CouchbaseServer;
