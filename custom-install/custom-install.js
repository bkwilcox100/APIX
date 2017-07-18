const exec = require('child_process').exec;
const os = require('os');
const _ = require('underscore');

var operatingSystem = os.platform();
console.log('Checking for platform...');

if (operatingSystem == 'win32'){
  console.log('Windows Detected');
  console.log('Switching to Custom Windows Install');
  console.log('Please wait...');
  if (_.contains(process.argv, '-g') || _.contains(process.argv, '--global')){
    exec('npm install chalk commander clear inquirer js-yaml mkdirp underscore xml2js -g', function(err, stdout, stderr){
      if (err) {
        console.error('Execution Error: ' + err);
        return;
      }
      console.log('Global Preinstall Complete');
    });
  } else {
    exec('npm install chalk commander clear inquirer js-yaml mkdirp underscore xml2js', function(err, stdout, stderr){
      if (err) {
        console.error('Execution Error: ' + err);
        return;
      }
      console.log('Preinstall Complete');
    });
  }

} else {
  console.log('Current Platform: ' + operatingSystem);
}
