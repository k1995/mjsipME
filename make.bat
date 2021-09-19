@set JAVAME_SDK_HOME="C:\Program Files (x86)\java\Java_ME_platform_SDK_3.4"
@set JAVA_BIN="C:\Program Files (x86)\java\jdk1.7.0_03\bin"

rmdir /S /Q classes\org
%JAVA_BIN%\javac -target 1.3 -source 1.3 -classpath %JAVAME_SDK_HOME%\lib\cldc_1.1.jar;%JAVAME_SDK_HOME%\lib\midp_2.1.jar;%JAVAME_SDK_HOME%\lib\mmapi.jar -d classes src\org\zoolu\microutil\*.java src\org\zoolu\net\*.java src\org\zoolu\sound\*.java src\org\zoolu\sound\codec\*.java  src\org\zoolu\util\*.java src\org\mjsip\media\*.java src\org\mjsip\microua\*.java src\org\mjsip\net\*.java src\org\mjsip\rtp\*.java src\org\mjsip\sdp\*.java src\org\mjsip\sdp\field\*.java src\org\mjsip\server\*.java src\org\mjsip\sip\address\*.java src\org\mjsip\sip\authentication\*.java src\org\mjsip\sip\call\*.java src\org\mjsip\sip\dialog\*.java src\org\mjsip\sip\header\*.java src\org\mjsip\sip\message\*.java src\org\mjsip\sip\provider\*.java src\org\mjsip\sip\transaction\*.java src\org\mjsip\ua\*.java src\org\mjsip\ua\cli\*.java src\org\mjsip\ua\gui\*.java

rmdir /S /Q tmpclasses\org
%JAVAME_SDK_HOME%\bin\preverify -classpath %JAVAME_SDK_HOME%\lib\cldc_1.1.jar;%JAVAME_SDK_HOME%\lib\midp_2.1.jar -d tmpclasses classes

%JAVA_BIN%\jar cfm bin\mjuaME.jar bin\MANIFEST.MF -C tmpclasses . -C resources .
java JadUpdate bin\mjuaME
