package controller.util.liveviewparser;

public enum Commands {
  C, //Lists all commands
  B, // - Connect App
  fc, // - send calibration file
  f1, // - transfers the current raw file QE
  f2, // - transfers the current raw file FLAME
  A, // - go back to automatic mode
  O, // - Optimise
  M, // - measure without Optimisation
  m, // - measure with Optimisation
  I, // x - set integration time to x ms(eg. I 500)
  IM, // x - set maximum integration time to x ms (eg. IM 1000
  i, // x - set the interval between measurements to x s (eg. i 60)
  iL, // x - sets the cycles between FLED to x cycles, 0=off
  S, // x - sets the resolution of sent bytes to x
  a1, // x - set the QE averages to x (eg. a1 3)
  a2, // x - set the FLAME averages to x (eg. a2 3)
  G, // - Show GPS position
  c, // - ReadIn config.txt
  T, // - to set date+time
  ss, // - toggle serial stream
  st, // - toggle serial data transfer
  FLAME; // - toggle FLAME spectrometer
}

