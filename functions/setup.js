const exec = require('child_process').exec;
const process = require('process');
const os = require('os');
exports.runSetup = function(destination) {
  if (os.platform() == "win32") {
    console.log("Cannot run incorpScript on windows machine");
  } else {
    console.log(process.cwd());
    exec('chmod 0764 ' + destination, function(err, stdout, stderr) {
      if (err) {
        throw (err);
      }
    });
  }
}
