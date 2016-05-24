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

import com.orange.jschwrapper.ssh.Scp;
import com.orange.jschwrapper.ssh.ScpArgs;
import com.orange.jschwrapper.ssh.SshArgs;
import com.orange.jschwrapper.ssh.SshCmd;
import com.orange.jschwrapper.ssh.SshCmdArgs;
import com.orange.jschwrapper.ssh.SshIsConnected;
import com.orange.jschwrapper.ssh.SshIsConnectedArgs;

class Launcher {

  void scp(MainArgs mainArgs) {
    ScpArgs scpArgs = getScpArgs(mainArgs);
    getScp().scpToRemote(scpArgs);
  }

  void sshCmd(MainArgs mainArgs) {
    SshCmdArgs sshCmdArgs = getSshCmdArgs(mainArgs);
    getSshCmd().execRemoteCommand(sshCmdArgs);
  }

  void isConnected(MainArgs mainArgs) {
    SshIsConnectedArgs sshIsConnectedArgs = getSshIsConnectedArgs(mainArgs);
    getSshIsConnected().isConnected(sshIsConnectedArgs);
  }

  private SshCmd getSshCmd() {
    return new SshCmd();
  }
  private ScpArgs getScpArgs(MainArgs mainArgs) {
    ScpArgs scpArgs = new ScpArgs();
    initSshArgs(scpArgs, mainArgs);
    scpArgs.setSourceFile(mainArgs.getScpSourceFile());
    scpArgs.setDestFileName(mainArgs.getScpDestFileName());
    return scpArgs;
  }

  private Scp getScp() {
    return new Scp();
  }
  private SshCmdArgs getSshCmdArgs(MainArgs mainArgs) {
    SshCmdArgs sshCmdArgs = new SshCmdArgs();
    initSshArgs(sshCmdArgs, mainArgs);
    sshCmdArgs.setCommand(mainArgs.getExecCommand());
    sshCmdArgs.setOutputStream(System.out);
    sshCmdArgs.setErrorStream(System.err);
    return sshCmdArgs;
  }

  private SshIsConnected getSshIsConnected() {
    return new SshIsConnected();
  }
  private SshIsConnectedArgs getSshIsConnectedArgs(MainArgs mainArgs) {
    SshIsConnectedArgs sshIsConnectedArgs = new SshIsConnectedArgs();
    initSshArgs(sshIsConnectedArgs, mainArgs);
    return sshIsConnectedArgs;
  }

  private void initSshArgs(SshArgs sshArgs, MainArgs mainArgs) {
    sshArgs.setUserName(mainArgs.getUserName());
    sshArgs.setUserPassword(mainArgs.getUserPassword());
    sshArgs.setRemoteHostName(mainArgs.getRemoteHostName());
  }
}
