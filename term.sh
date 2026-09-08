#!/bin/bash

mkdir -p $HOME/.local/share/applications

cat > $HOME/.local/share/applications/gnome-terminal-real.desktop <<-EOF
[Desktop Entry]
Name=GNOME Terminal Real
Comment=Use the command line
GenericName=Terminal
Exec=gnome-terminal.real
Icon=org.gnome.Terminal
Type=Application
StartupNotify=true
Categories=GNOME;GTK;System;TerminalEmulator;
Keywords=console;CLI;command;commandline;execute;lines;prompt;terminal;shell;
EOF
