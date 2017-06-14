import subprocess
import os
from datetime import datetime
import platform

dt = datetime.now()
rightNow = dt.strftime("%A, %B %d %Y %I:%M%p")
print("Now running Server @ " + rightNow)
systemType = platform.system()
if (systemType == 'Darwin'):
    #os.chdir("/Users/brandonwilcox/Development/OpenAPI/NodeJS/APIX")
    os.system("cd /Users/brandonwilcox/Development/'Open Source'/APIX/js")
    os.system("node /Users/brandonwilcox/Development/'Open Source'/APIX/js/main.js")
else:
    os.chdir("C:/Users/w767413/Documents/OpenAPI/NodeJS/APIX/js/")
    subprocess.run("node main.js")
