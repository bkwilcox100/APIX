const exec = require('child_process').exec;
const os = require('os');

var operatingSystem = os.platform();
console.log('Checking for platform...');

if (operatingSystem == 'win32'){
  console.log('Windows Detected');
  exec('npm install chalk clear inquirer js-yaml mkdirp underscore xml2js', function(err, stdout, stderr){
    if (err) {
      console.error('exec error: ${err}');
      return;
    }
    console.log('Preinstall Complete');
  });

} else {
  console.log('Current Platform: ' + operatingSystem);
}
