const express = require('express');
const polyserve = require('polyserve/lib/start_server');
const webpack = require('webpack');
const Server = require('webpack-dev-server/lib/Server');
const WebpackConfig = require('./webpack.config.js');

const app = express();
const compiler = webpack(WebpackConfig);
const server = new Server(compiler, {
  disableHostCheck: true,
});

app.use('/app', polyserve.getApp({}));
//app.use('/static', express.static('dist'));
app.use('/static', server.app);

app.listen(8080, function () {
  console.log('Start server!');
});
