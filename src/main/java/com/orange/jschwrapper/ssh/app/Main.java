package com.orange.jschwrapper.ssh.app;
/*
 * Copyright (c) 2016, Orange
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.orange.jschwrapper.ssh.SshException;
import com.orange.jschwrapper.ssh.app.LaunchMode.Mode;

public class Main {

  public static void main(String[] args) {
    try {
      MainArgs mainArgs = new MainArgs(args);
      Mode launchMode = mainArgs.retrieveLaunchMode();
      launchMode.launch(mainArgs);
      System.out.println("OK :)");
      System.exit(0);
    } catch (SshException e) {
      showError(args, e);
      System.exit(-1);
    } catch (ArrayIndexOutOfBoundsException e) {
      showUsage(args);
      System.exit(-2);
    }
  }

  private static void showError(String[] args, SshException e) {
    System.out.println(String.format("Exception occurred: %s", e.getMessage()));
    e.printStackTrace(System.out);
    showUsage(args);
  }

  private static void showUsage(String[] args) {
    System.out.println(String.format("You provided %d arguments", args.length));
    System.out.println(
      "\nUSAGE: COMMAND user password remotehost [options]\n" +
        "        where COMMAND is 'SCP' (copy from local to remote host) or 'EXEC' (execute a command on remote host) or 'CONNECTED' (test connection to remote host)\n" +
        "        and [options] is 'sourceFileName destFileName' in SCP mode\n" +
        "        or  [options] is \"ssh command to launch on remote host\" in EXEC mode\n\n"
    );
  }
}
