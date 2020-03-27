const path = require('path');
const webpack = require('webpack');
const Dotenv = require('dotenv-webpack');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');

const prodMode = process.env.NODE_ENV === "production";
const extractLess = new ExtractTextPlugin({
  filename: "[name].bundle.css",
});

console.log("Production mode " + (prodMode ? "enabled" : "disabled"));

module.exports = {
	entry: {
      "code-editor": "./src/code-graphEditor.js",
      "code-editor-simple": "./src/code-graphEditor-simple.js",
      "diagramm-overview": "./src/diagram-overview.js",
      "webpage": "./src/webpage.js",
      "graphical-meta-model-editor": "./src/graphical-meta-model-graphEditor.js",
      "graphical-model-editor": "./src/graphical-model-graphEditor.js",
      "silhouette": "./src/silhouette.js",
      "yfiles-editor": "./src/yfiles-graphEditor/concept-graphEditor.js",
      "yfiles-editor-dev": "./src/yfiles-graphEditor/devEnv/app-dev.js"
    },
	output: {
		path: path.join(__dirname, "dist"),
		filename: "[name].bundle.js",
		chunkFilename: "[id].chunk.js",
    publicPath: '/static/'
  },
  devtool: prodMode ? 'nosources-source-map' : 'cheap-module-eval-source-map',
	plugins: [
    new webpack.EnvironmentPlugin({
      NODE_ENV: 'development', // use 'development' unless process.env.NODE_ENV is defined
      DEBUG: false
    }),
      extractLess,
      new UglifyJSPlugin({
        parallel: true,
        sourceMap: !prodMode
      }),
      new webpack.ProvidePlugin({
        $: 'jquery',
        jQuery: 'jquery',
      }),
      new Dotenv(),
      new webpack.HotModuleReplacementPlugin()
    ],
  resolve: {
    extensions: ["*", ".js", ".jsx"],
    alias: {
      joint: 'jointjs',
      'backbone1.0': path.resolve(__dirname, 'src', 'graphical-graphEditor', 'backbone1.0.js'),
    }
  },
  module: {
    rules: [
      {
        test: /\.less$/,
        use: extractLess.extract({
          use: [
            {
              loader: "css-loader",
              options: {
                minimize: prodMode
              }
            },
            {
              loader: "less-loader",
              options: {
                strictMath: true,
                noIeCompat: true,
                noJs: true,
                noColor: true,
                strictImports: true,
              }
            }
          ],
          // use style-loader in development
          fallback: "style-loader"
        })
      },
      {
        test: /\.eot(\?v=\d+\.\d+\.\d+)?$/,
        loader: 'url-loader'
      },
      {
        test: /\.(woff|woff2)$/,
        loader: 'url-loader?prefix=font/'
      },
      {
        test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/,
        loader: 'url-loader?mimetype=application/octet-stream'
      },
      {
        test: /\.svg(\?v=\d+\.\d+\.\d+)?$/,
        loader: 'url-loader?mimetype=image/svg+xml'
      },
      {
        test: /\.(png|jpg|gif)$/,
        include: /(node_modules|src)/,        
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 8192,
              name: "[name].[ext]",
              outputPath: 'images/',
            }
          }
        ]
      },
      {
        test: /\.css$/,
        use: extractLess.extract({
          fallback: "style-loader",
          use: {
            loader: 'css-loader',
            options: {
              minimize: prodMode
            }
          }
        })
      },
      {
        test: /\.(png|jpg|gif|svg)$/,
        include: /assets/,
        use: [
          {
            loader: 'file-loader',
            options: {
              name: "[name].[ext]",
              outputPath: 'images/',
            }
          }
        ]
      },
      {
        test: /\.(js|jsx)$/,
        exclude: /(node_modules|bower_components)/,
        loader: "babel-loader",
        options: { presets: ["@babel/env"] }
      },

    ]
  },
  devServer: {
    contentBase: path.join(__dirname, './src/yfiles-graphEditor'),
    compress: true,
    port: 9003
  }
};
