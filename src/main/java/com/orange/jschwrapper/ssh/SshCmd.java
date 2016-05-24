package com.orange.jschwrapper.ssh;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SshCmd {

  private final byte[] byteArray = new byte[4096];

  private Ssh ssh;
  private SshCmdArgs sshCmdArgs;
  private boolean processFinished;

  public void execRemoteCommand(SshCmdArgs sshCmdArgs) {
    this.sshCmdArgs = sshCmdArgs;
    checkOutputStream();
    doExecRemoteCommand(sshCmdArgs);
  }

  private void doExecRemoteCommand(SshCmdArgs sshCmdArgs) {
    ssh = new Ssh(sshCmdArgs);
    ssh.initSshChannel();
    getRemoteCommandResults();
    ssh.disconnect();
  }

  private void getRemoteCommandResults() {
    while (!isProcessFinished()) {
      doGetRemoteCommandResultsLoop();
    }
  }

  private void doGetRemoteCommandResultsLoop() {
    try {
      doGetRemoteCommandResultsLoopRaiseException();
    } catch(IOException e) {
      throw new SshException("Failed to get remote command output", e);
    }
  }

  private void doGetRemoteCommandResultsLoopRaiseException() throws IOException, SshException {
    getCommandOutput();
    checkEndOfCommand();
    sleepForAWhile();
  }

  private void getCommandOutput() throws IOException {
    while (newInputAvailable()) {
      int readNbBytes = getInputStream().read(byteArray, 0, byteArray.length);
      if (readNbBytes < 0) {
        break;
      } else {
        getOutputStream().write(byteArray, 0, readNbBytes);
      }
    }
  }

  private void checkEndOfCommand() throws IOException {
    if (ssh.isChannelClosed()) {
      doCheckEndOfCommand();
    }
  }

  private void doCheckEndOfCommand() throws IOException {
    if (!newInputAvailable()) {
      writeExitStatus();
      setProcessFinished();
    }
  }

  private void writeExitStatus() throws IOException {
    int exitStatus = ssh.getChannelExitStatus();
    if (exitStatus == 0) {
      exitOk();
    } else {
      ssh.disconnect();
      throw new SshException(String.format("\n\nCommand Failed, exit status: %d\n", exitStatus));
    }
  }

  private void exitOk() throws IOException {
    String string = "\nCommand executed successfully\n";
    getOutputStream().write(string.getBytes());
  }

  private void sleepForAWhile() {
    Thread.yield();
  }

  private boolean newInputAvailable() throws IOException {
    return getInputStream().available() > 0;
  }

  private InputStream getInputStream() {
    return ssh.getInputStream();
  }

  private OutputStream getOutputStream() {
    return sshCmdArgs.getOutputStream();
  }

  private void checkOutputStream() {
    OutputStream outputStream = sshCmdArgs.getOutputStream();
    if (outputStream == null) {
      throw new SshException("Ssh command output stream is null, please set one");
    }
    if (outputStream == sshCmdArgs.getErrorStream()) {
      throw new SshException("Ssh command output stream can NOT be the same as the error stream, since error stream will be closed too soon by the remote host");
    }
  }

  private boolean isProcessFinished() {
    return processFinished;
  }
  private void setProcessFinished() {
    processFinished = true;
  }
}
