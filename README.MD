##Java ssh wrapper

This library is a wrapper around the [JSch library](http://www.jcraft.com/jsch)

As [JSch library](http://www.jcraft.com/jsch) is quite painful to use directly, we have developed here a wrapping API above JSch to ease the integration and usage of ssh connexions in a java environment.

Today the following features are supported:
- check if we can connect to a remote SSH host
- execute a command on a remote SSH host
- copy a file to a remote SSH host

## Build it
The library is not published on maven central yet.

To build it, just run:
<code>
./gradlew fatJar
</code>

## Use it from the command line

You can play directly with the built jar on the command line (wich is executable), so for instance to get the help:
<code>

$ java -jar build/libs/java-ssh-wrapper.jar 
You provided 0 arguments

USAGE: COMMAND user password remotehost [options]
        where COMMAND is 'SCP' (copy from local to remote host) or 'EXEC' (execute a command on remote host) or 'CONNECTED' (test connection to remote host)
        and [options] is 'sourceFileName destFileName' in SCP mode
        or  [options] is "ssh command to launch on remote host" in EXEC mode
</code>

To test if you can connect to a host:
<code>

$ java -jar build/libs/java-ssh-wrapper.jar CONNECTED guest password localhost

OK :)
</code>

To launch a command on a host:
<code>

$ java -jar build/libs/java-ssh-wrapper.jar EXEC guest password localhost uname

Linux

Command executed successfully

OK :)
</code>

To copy a file to a host:
<code>

$ java -jar build/libs/java-ssh-wrapper.jar SCP guest password localhost README.MD DEST_README.MD
</code>

## Use it as a library in your application

Just grab the jar file and add it to your project.
Have a look at the [Launcher class](https://github.com/Orange-OpenSource/java-ssh-wrapper/blob/master/src/main/java/com/orange/jschwrapper/ssh/app/Launcher.java) (command line launcher) to understand how to use it, I know it is a bit rude, but it is a good simple example.

## License
Java ssh wrapper is distributed under the terms of the BSD license, see the license file
