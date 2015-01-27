var webpack = require("webpack");
var webpackConfig = require("../../../webpack.config");

webpack(webpackConfig, function(err, stats) {

  if(err) {
    console.log("\u0010" + JSON.stringify(err));
  }
  //   return handleFatalError(err);
  //
  // var jsonStats = stats.toJson();
  // if(jsonStats.errors.length > 0)
  //   return handleSoftErrors(jsonStats.errors);
  //
  // if(jsonStats.warnings.length > 0)
  //   handleWarnings(jsonStats.warnings);
  //
  // successfullyCompiled();
});
