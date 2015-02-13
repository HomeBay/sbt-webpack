var path = require("path");

module.exports = {
  entry: path.join(__dirname, "/src/main/assets/javascripts/entry.js"),
  output: {
    path: __dirname,
    filename: "bundle.js"
  }
};
