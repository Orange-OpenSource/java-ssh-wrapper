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

import java.io.File;

class MainArgs {

  private final String[] args;

  MainArgs(String[] args) {
    this.args = args;
  }

  Mode retrieveLaunchMode() {
    try {
      return Mode.valueOf(getModeCandidate());
    } catch (IllegalArgumentException e) {
      throw new SshException(String.format("Provided Ssh mode is not supported: %s", getModeCandidate()), e);
    }
  }

  String getUserName() {
    return args[1];
  }

  String getUserPassword() {
    return args[2];
  }

  String getRemoteHostName() {
    return args[3];
  }

  private String getModeCandidate() {
    return args[0];
  }

  public File getScpSourceFile() {
    return new File(args[4]);
  }

  public String getScpDestFileName() {
    return args[5];
  }

  public String getExecCommand() {
    return args[4];
  }
}
