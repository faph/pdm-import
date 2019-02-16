/* 
 * The MIT License
 *
 * Copyright 2008-2019 Florenz A. P. Hollebrandse.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hec.dssgui.plugins;

import hec.heclib.util.HecTime;
import hec.hecmath.DSS;
import hec.hecmath.DSSFile;
import hec.hecmath.HecMath;
import hec.hecmath.HecMathException;
import hec.io.TimeSeriesContainer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PdmImporter {

   private String dssFileName;
   private int progress;
   private String message;
   private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
   private String inpFile,  modelFolder;
   private String watershed,  location,  parameter,  units,  valueType;
   private int timeStep;
   private String version,  modelType;
   private int[] startTimes;
   private int[] endTimes;
   private int eventCount,  totalRecordCount;

   public PdmImporter(String dssFileName, String inpFile) {
      this.dssFileName = dssFileName;
      this.inpFile = inpFile;
   }

   public void getMetaData() {
      final String fileNamePartsSeparator = "_";

      setMessage("PDM model file: " + inpFile);
      modelFolder = (new File(inpFile)).getParent();
      String inpFileName = (new File(inpFile)).getName();
      inpFileName = inpFileName.substring(0, inpFileName.length() - 4).toUpperCase();
      String[] fileNameParts = inpFileName.split(fileNamePartsSeparator);

      if (fileNameParts.length > 1) {
         watershed = fileNameParts[0];
         location = fileNameParts[1];
      } else {
         watershed = "";
         location = inpFileName;
      }
      parameter = "FLOW";
      units = "m3/s";
      valueType = "INST-VAL";
      timeStep = 15; //mins
      setMessage("Watershed: " + watershed);
      setMessage("Location: " + location);

      parseInpFile();
      parseFirstPltFile();
   }

   private void parseInpFile() {
      String startDateString;
      String endDateString;
      String startTimeString;
      String endTimeString;
      List startTimes = new ArrayList();
      List endTimes = new ArrayList();
      int eventIndex, endTimeStartChar;

      try {
         BufferedReader openInpFile = new BufferedReader(new FileReader(this.inpFile));
         String strLine = "";
         while (!strLine.startsWith("EVENT")) {
            strLine = openInpFile.readLine().trim().toUpperCase();
         }
         strLine = openInpFile.readLine().trim().toUpperCase();
         while (!strLine.startsWith("/")) {
            startTimeString = strLine.substring(1, 6).replaceAll(":", "");
            startDateString = strLine.substring(7, 18).replaceAll(" ", "");
            endTimeStartChar = strLine.indexOf("'") + 1;
            endTimeStartChar = strLine.indexOf("'", endTimeStartChar) + 1;
            endTimeStartChar = strLine.indexOf("'", endTimeStartChar) + 1;
            endTimeString = strLine.substring(endTimeStartChar, endTimeStartChar + 5).replaceAll(":", "");
            endDateString = strLine.substring(endTimeStartChar + 6, endTimeStartChar + 17).replaceAll(" ", "");
            startTimes.add(new Integer(new HecTime(startDateString + " " + startTimeString).value()));
            endTimes.add(new Integer(new HecTime(endDateString + " " + endTimeString).value()));

            strLine = openInpFile.readLine().trim().toUpperCase();
         }
         openInpFile.close();
      } catch (IOException e) {
      }

      if ((startTimes != null) && (endTimes != null)) {
         eventCount = startTimes.size();
         setMessage("Number of events: " + eventCount);
         this.startTimes = new int[eventCount];
         this.endTimes = new int[eventCount];
         for (eventIndex = 0; eventIndex < eventCount; eventIndex++) {
            this.startTimes[eventIndex] = ((Integer) startTimes.get(eventIndex)).intValue();
            this.endTimes[eventIndex] = ((Integer) endTimes.get(eventIndex)).intValue();
         }
      }
   }

   private void parseFirstPltFile() {
      String pltFileName;
      String strLine;

      pltFileName = getPltFileName(0);

      try {
         BufferedReader openPltFile = new BufferedReader(new FileReader(pltFileName));
         strLine = openPltFile.readLine().trim().toUpperCase();
         openPltFile.close();

         if (!getPltColumnValue(strLine, 4).equalsIgnoreCase("FCST01")) {
            version = "PDM-MOD";
            modelType = "NORMAL";
         } else {
            version = "PDM-FCST";
            modelType = "FORECAST";
         }
         setMessage("PDM simulation mode: " + modelType);
      } catch (IOException e) {
      }
   }

   private String getPltColumnValue(String strLine, int columnIndex) {
      //columnindex starting at 0!

      String result = "";
      int columnWidth = 11;

      result = strLine.substring(columnIndex * columnWidth, (columnIndex + 1) * columnWidth).trim().toUpperCase();

      return result;
   }

   private String getPltFileName(int eventIndex) {
      String result = "";
      DecimalFormat formatter = new DecimalFormat("00");

      result = modelFolder + "/us" +
              formatter.format(eventIndex + 1) + ".PLT";
      return result;
   }

   private int getTotalRecordCount() {
      int result = 0;
      int eventIndex;

      if (eventCount > 0) {
         for (eventIndex = 0; eventIndex < eventCount; eventIndex++) {
            result += (endTimes[eventIndex] - startTimes[eventIndex]) / timeStep + 1;
         }
      }
      return result;
   }

   public int importAllEvents() {
      int overallStatus = 1;
      int eventStatus;
      int eventIndex;

      totalRecordCount = getTotalRecordCount();

      for (eventIndex = 0; eventIndex < eventCount; eventIndex++) {
         setMessage("Importing event " + new Integer(eventIndex + 1) + " of " + eventCount + ".");
         eventStatus = importEvent(eventIndex);
         if (eventStatus == 0) {
            overallStatus = 0;
         }
      }
      setProgress(100);
      return overallStatus;
   }

   private int importEvent(int eventIndex) {
      int status = 0;
      String pltFileName;
      String strLine;
      int recordCount;
      int recordIndex;
      int progressUpdateInterval;
      int pltColumn;
      int time;
      double value;
      TimeSeriesContainer tsc = new TimeSeriesContainer();

      progressUpdateInterval = (new Double(Math.ceil(totalRecordCount / 100))).intValue();

      pltFileName = getPltFileName(eventIndex);

      recordCount = (endTimes[eventIndex] - startTimes[eventIndex]) / timeStep + 1;
      int[] times = new int[recordCount];
      double[] values = new double[recordCount];

      //set metadata
      tsc.watershed = watershed;
      tsc.location = location;
      tsc.parameter = parameter;
      tsc.version = version;
      tsc.units = units;
      tsc.type = valueType;
      tsc.fullName = "/" + watershed + "/" + location + "/" + parameter +
              "//" + timeStep + "MIN/" + version + "/";
      tsc.startTime = startTimes[eventIndex];
      tsc.endTime = endTimes[eventIndex];
      tsc.numberValues = recordCount;
      tsc.interval = timeStep;
      HecTime startTime = new HecTime();
      HecTime endTime = new HecTime();
      startTime.set(tsc.startTime);
      endTime.set(tsc.endTime);
      setMessage("Start date/time: " + startTime.dateAndTime());
      setMessage("End date/time: " + endTime.dateAndTime());
      setMessage("Number of records: " + tsc.numberValues);

      //read plt file
      try {
         BufferedReader PltFile = new BufferedReader(new FileReader(pltFileName));
         strLine = PltFile.readLine().toUpperCase();
         time = startTimes[eventIndex];
         for (recordIndex = 0; recordIndex < recordCount; recordIndex++) {
            strLine = PltFile.readLine().toUpperCase();
            if (modelType.equalsIgnoreCase("NORMAL")) {  //normal simulations in column 3, forecasts in column 5
               pltColumn = 2;
            } else {
               pltColumn = 4;
            }
            try {
               value = Double.parseDouble(getPltColumnValue(strLine, pltColumn));
               if (value < 0) {
                  value = HecMath.UNDEFINED;
               }
            } catch (NumberFormatException e) {
               value = HecMath.UNDEFINED;
            }
            times[recordIndex] = time;
            values[recordIndex] = value;

            if (((recordIndex + 1) % progressUpdateInterval) == 0) {
               setProgress(getProgress() + 1);
            }

            time += timeStep;
         }

         PltFile.close();
      } catch (IOException e) {
      }
      //set remaining (meta)data
      tsc.values = values;
      tsc.times = times;

      //write series to DSS file
      try {
         HecMath hecMath = HecMath.createInstance(tsc);
         DSSFile dssFile = DSS.open(dssFileName);
         dssFile.write(hecMath);
         setMessage("Data saved to HEC-DSS database.");
         setMessage("Data location: " + tsc.fullName);
         status = 1;
      } catch (HecMathException e) {
         setMessage("An error occurred while saving the data to the HEC-DSS database.");
      }

      return status;
   }

   public void addPropertyChangeListener(PropertyChangeListener l) {
      propertyChangeSupport.addPropertyChangeListener(l);
   }

   public void removePropertyChangeListener(PropertyChangeListener l) {
      propertyChangeSupport.removePropertyChangeListener(l);
   }

   public int getProgress() {
      return this.progress;
   }

   public void setProgress(int progress) {
      int oldProgress = this.progress;
      this.progress = progress;
      propertyChangeSupport.firePropertyChange("progress", new Integer(oldProgress), new Integer(progress));
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      String oldMessage = this.message;
      this.message = message;
      propertyChangeSupport.firePropertyChange("message", oldMessage, message);
   }
}
