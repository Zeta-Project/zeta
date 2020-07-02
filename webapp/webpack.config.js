const path = require('path');
const webpack = require('webpack');
const Dotenv = require('dotenv-webpack');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const { options } = require('less');

const prodMode = process.env.NODE_ENV === "production";
console.log("Production mode " + (prodMode ? "enabled" : "disabled"));

module.exports = {
	entry: {
      "code-editor": "./src/code-editor.js",
      "code-editor-simple": "./src/code-editor-simple.js",
      "diagramm-overview": "./src/diagram-overview.js",
      "webpage": "./src/webpage.js",
      "graphical-meta-model-editor": "./src/graphical-meta-model-editor.js",
      "graphical-model-editor": "./src/graphical-model-editor.js",
      "silhouette": "./src/silhouette.js",
      "yfiles-editor": "./src/yfiles-editor/concept-editor.js",
      "yfiles-editor-dev": "./src/yfiles-editor/devEnv/app-dev.js"
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
    extractLess = new MiniCssExtractPlugin({
      filename: "[name].bundle.css"
    }),
    new UglifyJSPlugin({
      parallel: true,
      sourceMap: !prodMode
    }),
    new webpack.ProvidePlugin({
      $: 'jquery',
      jQuery: 'jquery',
    }),
    new Dotenv()
  ],
  resolve: {
    alias: {
      joint: 'jointjs',
      'backbone1.0': path.resolve(__dirname, 'src', 'graphical-editor', 'backbone1.0.js'),
    }
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /(node_modules|bower_components)/,
        use: {
          loader: 'babel-loader',
        }
      },
      {
        test: /\.(less)$/,
        /*use: extractLess.extract({
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
                strictImports: true
              }
            }
          ],
          fallback: "style-loader"
        }),*/
        use: [
          {
            loader: MiniCssExtractPlugin.loader
          },
          {
            loader: 'css-loader',
            options: {
              minimize: prodMode
            }
          },
          {
            loader: 'less-loader',
            options: {
              staticImports: true
            }
          }
        ]
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
        use: [
          MiniCssExtractPlugin.loader, 
          'css-loader'
        ]
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
      }
    ]
  },
  devServer: {
    inline: false,
    contentBase: path.join(__dirname, './src/yfiles-editor'),
    compress: true,
    port: 9003
  }
};
