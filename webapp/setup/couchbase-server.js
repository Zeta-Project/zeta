const request = require('requestretry');

class CouchbaseServer {
  constructor(settings, callback) {
    this.settings = settings;
    this.callback = callback;
  }

  _log(message) {
    console.log('Setup server -', message);
  }

  _error(message, error) {
    console.error('Setup server -', message, error);
    process.exit(1);
  }

  setup() {
    request.get({
      url: this.settings.address + '/pools/default/buckets/' + this.settings.bucket,
      maxAttempts: 5,
      retryDelay: 5000,
      retryStrategy: request.RetryStrategies.HTTPOrNetworkError
    }, (error, response, body) => {

      if (error) {
        this._error('request error:', error);
      }

      if (response.statusCode === 200) {
        this._log('bucket already exist. Not setup needed');
        this.callback();
      } else {
        this._setupServices();
      }
    });
  }

  _setupServices() {
    request.post({
      url: this.settings.address + '/node/controller/setupServices',
      form: {
        services: 'data,index,query'
      }
    }, (error, response, body) => {

      if (error) {
        this._error('request error:', error);
      }

      this._log('setup services was successful');
      this._createNode();
    });
  }

  _createNode() {
    request.post({
      url: this.settings.address + '/nodes/self/controller/settings ',
      form: {
        path: '/opt/couchbase/var/lib/couchbase/data',
        index_path: '/opt/couchbase/var/lib/couchbase/data',
      }
    }, (error, response, body) => {

      if (error) {
        this._error('request error:', error);
      }

      this._log('create node was successful');
      this._createUser();
    });
  }

  _createUser() {
    request.post({
      url: this.settings.address + '/settings/web',
      form: {
        username: this.settings.username,
        password: this.settings.password
      }
    }, (error, response, body) => {

      if (error) {
        this._error('request error:', error);
      }

      this._log('create user was successful');
      this._createBucket();
    });
  }

  _createBucket() {
    request.post({
      'url': this.settings.address + '/pools/default/buckets',
      'auth': {
        'user': this.settings.username,
        'pass': this.settings.password
      },
      'form': {
        'name': this.settings.bucket,
        'ramQuotaMB': 256,
        'authType': 'sasl',
      }
    }, (error, response, body) => {

      if (error) {
        this._error('request error:', error);
      }
      
      this._log('create bucket was successful');
      this._setIndexRamQuota();
    });
  }

  _setIndexRamQuota() {
    request.post({
      url: this.settings.address + '/pools/default',
      form: {
        memoryQuota: 256,
        indexMemoryQuota: 256
      }
    }, (error, response, body) => {

      if (error) {
        this._error('request error:', error);
      }

      this._log('set ram index quota was successful');
      this.callback();
    });
  }
}

module.exports = (settings, callback) => {
  var db = new CouchbaseServer(settings, callback);
  db.setup();
}
