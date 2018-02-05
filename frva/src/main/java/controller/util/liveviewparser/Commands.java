/*
 *     This file is part of FRVA
 *     Copyright (C) 2018 Andreas Hüni
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

