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
import hec.io.TimeSeriesContainer;

public class MetaDataParser {

   private TimeSeriesContainer timeSeriesContainer;
   private HecTime startTime;
   private HecTime endTime;

   public MetaDataParser(TimeSeriesContainer timeSeriesContainer, HecTime startTime) {
      this.timeSeriesContainer = timeSeriesContainer;
      this.startTime = startTime;
//      this.endTime = endTime;
   }

   public String parseMetaDataString(String metaDataString) {
      String result;

      result = metaDataString;

      result = result.replaceAll("\\[NEWLINE\\]", getNewLineChar());
      result = result.replaceAll("\\%n", getNewLineChar());
      result = result.replaceAll("\\[WATERSHED\\]", getWatershed());
      result = result.replaceAll("\\[CATCHMENT\\]", getWatershed());
      result = result.replaceAll("\\[LOCATION\\]", getLocation());
      result = result.replaceAll("\\[PARAMETER\\]", getParameter());
      result = result.replaceAll("\\[ISISNODE\\]", getIsisNode());
      result = result.replaceAll("\\[RECORDS\\]", getRecordCount());
      result = result.replaceAll("\\[STARTTIME\\]", getStartTime());
      result = result.replaceAll("\\[STARTDATE\\]", getStartDate());

      return result;
   }

   private String getNewLineChar() {
      return System.getProperty("line.separator");
   }

   private String getWatershed() {
      return timeSeriesContainer.watershed;
   }

   private String getLocation() {
      return timeSeriesContainer.location;
   }

   private String getParameter() {
      return timeSeriesContainer.parameter;
   }
   
   private String getIsisNode() {
      String result = "?????";
      String parameter = timeSeriesContainer.parameter.toUpperCase();
      
      if (parameter.startsWith("FLOW") || parameter.startsWith("DIS"))  {
         result = "QTBDY";
      } else if (parameter.startsWith("STAGE") || parameter.startsWith("LEV")) {
         result = "HTBDY";
      }
     
      return result;
   }

   private String getRecordCount() {
      String result;
      
      result = Integer.toString(timeSeriesContainer.numberValues);
      result = "         " + result;
      result = result.substring(result.length()-10);
      
      return result;
   }

   private String getStartTime() {
      return startTime.dateAndTime(114); //formatted as 01JUN07, 24:00
   }

   private String getStartDate() {
      return startTime.date(114); //formatted as 01JUN07
   }
//   private String getEndTime() {
//      return endTime.date(114);
//   }
}
