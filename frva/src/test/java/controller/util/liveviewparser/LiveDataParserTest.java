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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import controller.LiveViewController;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import model.FrvaModel;
import org.junit.Before;
import org.junit.Test;

public class LiveDataParserTest {

  LiveDataParser liveDataParser;
  FrvaModel model;
  LiveViewController mockLiveViewController;

  @Before
  public void setUp() throws Exception {
    model = mock(FrvaModel.class);
    mockLiveViewController = mock(LiveViewController.class);
    liveDataParser = new LiveDataParser(mockLiveViewController, model);

  }

  @Test
  public void startParsing() throws InterruptedException {

    InputStream input = new ByteArrayInputStream("; ; App?".getBytes());
    OutputStream output = new ByteArrayOutputStream();

    liveDataParser.startParsing(input, output);

    Thread.sleep(50);

    assertTrue(liveDataParser.getCommandQueue().removeIf(commandInterface -> {
          return commandInterface instanceof CommandAutoMode;
        }
    ));
  }
}