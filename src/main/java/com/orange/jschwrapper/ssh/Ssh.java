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

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class Ssh {

  private final JSch jSch = new JSch();
  private final SshAcknowledge sshAcknowledge = new SshAcknowledge();
  private final SshArgs sshArgs;

  private Session session;
  private JschUserInfoImpl jschUserInfo;
  private Channel channel;
  private InputStream inputStream;
  private OutputStream outputStream;


  Ssh(SshArgs sshArgs) {
    this.sshArgs = sshArgs;
    init();
  }

  private void init() {
    initJschUserInfo();
    initSession();
  }

  private void initJschUserInfo() {
    jschUserInfo = new JschUserInfoImpl(sshArgs.getUserPassword());
  }

  void initSession() {
    try {
      doInitSession();
    } catch (JSchException e) {
      disconnect();
      throw new SshException("Failed to init ssh session", e);
    }
  }

  private void doInitSession() throws JSchException {
    session = jSch.getSession(sshArgs.getUserName(), sshArgs.getRemoteHostName(), 22);
    session.setUserInfo(jschUserInfo);
    session.setTimeout(sshArgs.getSessionTimeout());
    session.connect();
  }

  void initSshChannel() {
    try {
      doInitChannel();
    } catch (JSchException e) {
      throw new SshException("Failed to init scp channel", e);
    }
  }

  private void doInitChannel() throws JSchException {
    channel = session.openChannel("exec");
    initChannelStreams();
    initChannelCommand();
    connectChannel();
  }

  private void initChannelStreams() {
    try {
      doInitChannelStream();
    } catch (IOException e) {
      throw new SshException("Failed to init channel streams", e);
    }
  }

  private void doInitChannelStream() throws IOException {
    inputStream = channel.getInputStream();
    outputStream = channel.getOutputStream();
    if (isSshCmd()) {
      initErrorStream();
    }
  }

  private void initErrorStream() {
    ((ChannelExec)channel).setErrStream(((SshCmdArgs)sshArgs).getErrorStream());
  }

  private void initChannelCommand() {
    String command = sshArgs.getSshCommandType().getSshChannelCommand(sshArgs);
    ((ChannelExec)channel).setCommand(command);
  }

  private void connectChannel() throws JSchException {
    channel.connect();
    if (isScpCmd()) {
      sshAcknowledge.checkAck(inputStream);
    }
  }

  void disconnect() {
    disconnectOutputStream();
    disconnectChannel();
    disconnectSession();
  }

  private void disconnectOutputStream() {
    if (outputStream != null) {
      doDisconnectOutputStream();
    }
  }

  private void doDisconnectOutputStream() {
    try {
      outputStream.close();
    } catch (IOException e) {
      throw new SshException("Failed to close scp output stream", e);
    }
  }

  private void disconnectChannel() {
    if (channel != null) {
      channel.disconnect();
    }
  }

  private void disconnectSession() {
    if (session != null) {
      session.disconnect();
    }
  }


  public boolean isChannelClosed() {
    return channel.isClosed();
  }

  public int getChannelExitStatus() {
    return channel.getExitStatus();
  }

  void sendScpHeader(File sourceFile) {
    String scpHeaderCommand = getScpHeaderCommand(sourceFile.length(), sourceFile.getName());
    writeToOutputStream(scpHeaderCommand.getBytes());
    sshAcknowledge.checkAck(inputStream);
  }

  private String getScpHeaderCommand(long fileSize, String fileName) {
    return String.format("C0644 %d %s\n", fileSize, fileName);
  }

  private void writeToOutputStream(byte[] bytes) {
    try {
      outputStream.write(bytes);
      outputStream.flush();
    } catch (IOException e) {
      throw new SshException("Failed to write output stream", e);
    }
  }

  public void writeTerminateChar() {
    byte[] terminateChar = new byte[1];
    terminateChar[0] = 0;
    writeToOutputStream(terminateChar);
    sshAcknowledge.checkAck(inputStream);
  }

  boolean isSshCmd() {
    return sshArgs instanceof SshCmdArgs;
  }
  boolean isScpCmd() {
    return sshArgs instanceof ScpArgs;
  }

  public InputStream getInputStream() {
    return inputStream;
  }
  public OutputStream getOutputStream() {
    return outputStream;
  }
}
