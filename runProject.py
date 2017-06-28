#!/usr/bin/python

import subprocess
import os
from datetime import datetime
import platform

dt = datetime.now()
rightNow = dt.strftime("%A, %B %d %Y %I:%M%p")
print("Now running @ " + rightNow)
systemType = platform.system()
if (systemType == 'Darwin'):
    #os.chdir("/Users/brandonwilcox/Development/OpenAPI/NodeJS/APIX")
    os.system("cd /Users/brandonwilcox/Development/'Open Source'/APIX/")
    os.system("node ./apix.js")
else:
    os.chdir("C:/Users/w767413/Documents/OpenAPI/NodeJS/APIX/js/")
    subprocess.run("node apix.js")
