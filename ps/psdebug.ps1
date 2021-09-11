$ROOT_DIR = "$PSScriptRoot/..";
Import-Module $ROOT_DIR/ps/logsloth.psm1 -Force
cd $ROOT_DIR/cpp-sample/src;
rm -rf CMakeCache.txt, CMakeFiles/, logsloth_test
cmake .
make
./logsloth_test | logsloth