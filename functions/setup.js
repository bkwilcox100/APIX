const exec = require('child_process').exec;
const os = require('os');
exports.runSetup = function(){
  if (os.platform() == "win32"){
    console.log("Cannot run incorpScript on windows machine");
  } else {
    exec('cd functions && chmod 0764 incorporateNewProject.sh', function(err){
      if (err){
        throw (err);
      }
      console.log("Setup Successful");
    });
  }
}
