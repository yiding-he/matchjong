rem Windows 下要打包，需要安装 wix 工具，并将其加入到 PATH 环境变量
%JDK_20%\bin\jpackage.exe --name matchjong ^
  --input target\dist ^
  --main-jar matchjong-1.0-SNAPSHOT.jar ^
  --type app-image ^
  --dest target
