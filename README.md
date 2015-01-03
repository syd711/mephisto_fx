Updates
=======

The UI has been re-implemented an can be found here: https://github.com/syd711/mephisto_iii

The Radio
=========

The radio using the API can be found here: http://syd711.github.io/mephisto_fx/

Installation
============

# Local Development Installation

## Prequisites

You have a running mpd server most probably running on the Raspberry Pi. Make sure that the mpd server
is reachable in the local network. You may have to configure the mpd.conf file for this. I developed on Windows
and haven't found a propery mpd service for Windows, so I used the Raspi for this.

## Checkout and compile

The following steps assume that you use IntelliJ's IDEA since the project file is already part of the project.

* Clone the repository: *git clone git@github.com:syd711/mephisto_fx.git*
* Start IDEA and open the *mephisto_fx.iml* project file
* Add the mandatory jar files: I wasn't sure if it is allowed to push them on github too, so you have to download them manually.
Additionally, you have to create the googlemusic.jar from github project: https://github.com/jkiddo/gmusic.api
Create a new folder *lib* and copy the mandatory jar files for the project into the directory. These are:
    * commons-configuration-1.9.jar
    * commons-exec-1.1.jar
    * commons-io-2.4.jar
    * commons-lang-2.6.jar
    * commons-logging-1.1.3.jar
    * commons-net-3.3.jar
    * httpclient-4.2.5
    * httpcore-4.2.4
    * googlemusic.jar
    * gson-2.2.4.jar
    * guava-14.0.1.jar
    * jaudiotagger-2.2.0-20130321.142353-1.jar (not sure if actually used)
    * jdom-1.1.3.jar
    * logback-classic-1.0.13.jar
    * logback-core-1.0.13.jar
    * pi4j-core.jar
    * pi4j-device.jar
    * pi4j-example.jar
    * pi4j-gpio-extension.jar
    * pi4j-service.jar
    * rome-1.0.jar
    * slf4j-api-1.7.5.jar
* If everything is configured properly IDEA should know be able to compile the project.

## Configuration for the first start

* Create a new directory called *image_cache* in the project directory. When Google music is loaded, the covers are
cached there for a faster access instead of downloading them each time. If you are using Linux, ensure that the folder is
writeable.
* Configure *.properties* files of the *conf* folder:
    * *google.properties*: You have to create this file and add the property values for google.login and google.password
    * *gpio.properties*: The file configures the rotary encoder, see section "Raspberry Pi Installation".
    * *mpd.properties*: Enter the host and port of the server you have the mpd running (the Raspberry Pi). *6600* is the default port for mpd, so there is no need to change it.
    * *settings.properties*: Configures the screen size of the TFT that is connected, see section "Raspberry Pi Installation".
    * *streams.properties*: Configures the radio stations you want to listen to. It's only mandatory to configure the "<id>.url" properties. The .name property will be resolved automatically and written into the script.
    * *weather.properties*: Configures the location you want to retrieve the weather information from. There a link in the comment of the properties file that helps you to add new locations. The locations are stored as URL, like *http://weather.yahooapis.com/forecastrss?w=2345484&u=c* where *w* is the location and *u* the unit of the temperature.

* Run the *MephistoRadioFX* main. If everyhing is setup properly, you should see that the MPD connection is established correctly. Use the arrow keys for navigation:
    * UP: Long push/next section
    * DOWN: Short push/confirm selection
    * LEFT/RIGHT: move to next station, album, weather info switch

# Raspberry Pi Installation

This section describes how to install the software on a Raspberry Pi for a production use. The following
steps assume that you have the latest wheezy image installed, a TFT connected on the component out (like this one:http://www.amazon.com/Sunnvalleytek-Digital-Monitor-swivel-stand/dp/B005DP9QHA/ref=sr_1_1?s=electronics&ie=UTF8&qid=1391182409&sr=1-1&keywords=4.3+inch+tft).
You should a rotary encoder with push button at hand, including jumper wires.

* Install Java: I think there are already wheezy images out there with Java 8 pre-installed. I did it manually be using the Oracle documentation (http://www.oracle.com/technetwork/articles/java/raspberrypi-1704896.html).
I installed *jdk-8-ea-b109-linux-arm-vfp-hflt-25_sep_2013.tar.gz*
* Create a new directory in the *pi* users home folder: *mephisto_fx*
* Copy the *lib*, *conf* and *image_cache* directories into the *mephisto_fx* folder
* Copy the *mephisto.sh* from the *raspi* folder into the *mephisto_fx* folder and make it executable
* Copy the IDEA generated *mephiso_fx.jar* from the *out/artifacts* folder into the *mephisto_fx* folder

You should be able to execute the jar file now using the .sh script: *./mephisto.sh*. You won't be able to send any input
to the program yet and the window of the Java program may not match the screen solution, we come to that...

## TFT screen setup

The *mephisto_fx* does not use the "fullscreen* mode of Java FX. The window is just not filling the whole screen properly, so I tweaked around a lot by configuring
the screen resolution in the *config.txt* of the Raspberry Pi (see folder *raspi/* where a copy of my configuration is located). The program
is running a borderless window now, there is still some space wasted on the bottom of the display. You can configure that actual size
of the dialog by configuring the file *conf/settings.properties*.

## Rotary Encoder setup

Here comes the tricky part. I took me a lot of time to get the encoder running. I hope I'm documenting the following steps correctly:

* Create a new folder called *rotary* in the home folder of the *pi* user
* Install Gaugette's library in this directory, including the mandatory wiringPi, wiringPi-Python: https://github.com/guyc/py-gaugette. You may run into some problems during the installation of the other mandatory software here, but Google will help you to find a quick solution for these problems (I can't remember what kind of errors I got here).
* Copy the files *./raspi/rotary.py* and *./raspi/rotary.sh* into the *rotary* folder
* Make both scripts executable. The *.sh* script doesn't have to be installed as a startup script. It is executed by the Java program. The *sh* itselft executes a python program
 that uses the *gaugette* library to listen to the rotary encoder input. Each input is send to a socket. The Java program has started a socket server to receive these commands and
 execute them as user input. The port and script location may be configured in the python script and the .properties file *conf/gpio.properties*. If you have installed all steps correctly, there is no need to change any of these files.
* Finally connect the rotary encoder to the gpio ports of the Pi using the jumper wires. (Too bad I haven't made a proper picture of this wiring, so I have to explain it here).
    * Connect the push button: the push button is controlled by the Java API Pi4j. The two wires of the push button must to be connected to +3V and GPIO Port 2 which is pin 13 of the pi (see http://pi4j.com/usage.html for details here!)
    * Connect the rotary encoder: connect the center pin of the rotary encoder to ground, connect the signal pins to GPIO 1 and 4 (pin 12 and 16 on the pi)

After the Java program is started, the software should now run properly.

## Final steps

* Disable the default screen saver:
    * edit the *rc.local*: *sudo nano /etc/rc.local*
    * ABOVE the "exit 0" line insert: *setterm -blank 0 -powerdown 0 -powersave off*
* Execute the *mephisto_fx.sh* script on startup, use the following thread for help: http://raspberrypi.stackexchange.com/questions/8734/execute-script-on-start-up
* Use USB sound: there a lot of discussion about how bad the audio output of the Raspberry Pi is. I recommend to use an USB soundcard instead.

# Known Bugs
* Sometimes the radio station info is not updated correctly and the station information of the previous station is shown. I haven't figured out yet what the problem is.

References
==========
* Icons have been used from http://defaulticon.com/
* Install Java on the Raspberry Pi: http://www.oracle.com/technetwork/articles/java/raspberrypi-1704896.html
* Gaugette's Rotary Encoder Library: https://github.com/guyc/py-gaugette
* JKIDDO Google Music API: https://github.com/jkiddo/gmusic.api
* Rotary Encoder for the Raspberry Pi: http://planb.nicecupoftea.org/2013/06/30/rotary-encoder-for-the-raspberry-pi/
* Install a Raspberry Pi start script: http://raspberrypi.stackexchange.com/questions/8734/execute-script-on-start-up
* Disable the Raspberry Pi screen saver: http://www.raspberrypi.org/phpBB3/viewtopic.php?f=29&t=43932
* Pi4J: http://pi4j.com/

Updates
========
* 30.05.2014: Updated documentation
* 30.05.2014: Fixed Google Music Integration
* 30.05.2014: Radio station is changed on push if selection has not changed
