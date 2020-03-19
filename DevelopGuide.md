## Development Guide

### We can use Github Actions to build when commiting. To download, have a look at readme.md

# Tools
* Java Development Kit 8
* IntelliJ Idea or other java IDEs, even notepad

# Step 1
Download spigot 1.13.2 jar file to build.([My mirror](https://raw.githubusercontent.com/Untitled/UPLOADS/master/spigot-1.13.2.jar))

# Step 2
Clone this repository into your computer.
use
``git clone https://github.com/czm23333/IndustrialWorld.git`` or ``git clone git@github.com:czm23333/IndustrialWorld.git``

# Step 3
Open in your IDE and edit.

# Step 4
Build in your IDE or use maven commandline:
``mvn -B -e -U clean install --file pom.xml``

# Step 5
Get your jar file and copy it into plugins/
