const path = require("path");
const webpack = require("webpack");
const dotenv = require('dotenv-webpack');

module.exports = {
  entry: "./assets/js/frontend/src/index.js",
  mode: "development",
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /(node_modules|bower_components)/,
        loader: "babel-loader",
        options: { presets: ["@babel/env"] }
      },
      {
        test: /\.css$/,
        use: ["style-loader", "css-loader"]
      },
      {
        test: /\.(png|jpg|gif|svg)$/,
        use: [{
            loader: 'file-loader',
            options: {}
        }]
      }
    ]
  },
  resolve: { 
    extensions: [".*", ".js", ".jsx"], 
    modules: [ path.resolve(__dirname, "./assets/js/frontend/src"), "node_modules"]
  },
  output: {
    path: path.resolve(__dirname, "dist/"),
    publicPath: "/dist/",
    filename: "bundle.js"
  },
  devServer: {
    allowedHosts: 'all',
    historyApiFallback: true,
    static: {
      directory: path.join(__dirname, "public/"),
    },
    port: 3000,
    hot: true,
  },
  plugins: [new webpack.HotModuleReplacementPlugin(), new dotenv({
    path: './assets/js/frontend/src/.env.local'
  })]
};