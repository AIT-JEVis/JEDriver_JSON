/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.commons.parsing.jsonParsing;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import org.jevis.api.JEVisObject;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.Result;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisType;
import org.jevis.commons.DatabaseHelper;
import org.jevis.commons.JEVisTypes;
import org.jevis.commons.parsing.csvParsing.CSVDatapointParser;
import org.jevis.commons.parsing.xmlParsing.XMLParsing;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author ait-user
 */
public class JSONParsing implements DataCollectorParser {

    private Integer _dateIndex;
    private Integer _timeIndex;
    private String _dateFormat;
    private String _timeFormat;
    private String _decimalSeperator;
    private String _thousandSeperator;
    private List<JSONDatapointParser> _datapointParsers = new ArrayList<JSONDatapointParser>();
    private List<Result> _results = new ArrayList<Result>();
    
    
    
    @Override
    public List<Result> getResults() {
        return _results;
    }

    @Override
    public void parse(InputHandler ic) {

        BufferedInputStream  oj = (BufferedInputStream)ic.getRawInput();
        String jsonString = ic.getStringInput();
        Gson gson = new Gson();
      
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(oj));
        
        Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
        Map<String,String> map = gson.fromJson(jsonString, stringStringMap);

        
        Logger.getLogger(this.getClass().getName()).log(Level.ALL, "Start JSON parsing");
        
     
        if (ic.getFilePath() != null) {
            Logger.getLogger(this.getClass().getName()).log(Level.ALL, "File Path: " + ic.getFilePath());
        }
            try {
                
                for (JSONDatapointParser dpParser : _datapointParsers) {
                    
                    String timeString = map.get("Time");
                    DateTime dateTime = getDateTime(timeString);
                
                    String valId = dpParser.getValueIdentifier();
                    String valString = map.get(valId);
                    
                    Long target = dpParser.getTarget();
                    Double value = Double.parseDouble(valString);

                    if (dateTime == null) {
                        continue;
                    }
                    _results.add(new Result(target, value, dateTime));
                }
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Detect a Problem in the Parsing Process");
            }
        
        if (!_results.isEmpty()) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "LastResult (Date,Target,Value): " + _results.get(_results.size() - 1).getDate() + "," + _results.get(_results.size() - 1).getOnlineID() + "," + _results.get(_results.size() - 1).getValue());
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Cant parse or cant find any parsable data");
        }
        
    }

    @Override
    public void initialize(JEVisObject equipmentObject) {

           
        try {
            JEVisClass parser = equipmentObject.getDataSource().getJEVisClass("JSON Parser");
            JEVisObject parserObject = equipmentObject.getChildren(parser, true).get(0);
            JEVisClass jeClass = parserObject.getJEVisClass();

            JEVisType dateFormatType = jeClass.getType(JEVisTypes.Parser.DATE_FORMAT);
            JEVisType timeFormatType = jeClass.getType(JEVisTypes.Parser.TIME_FORMAT);
            JEVisType decimalSeperatorType = jeClass.getType(JEVisTypes.Parser.DECIMAL_SEPERATOR);
            JEVisType thousandSeperatorType = jeClass.getType(JEVisTypes.Parser.THOUSAND_SEPERATOR);

            _dateFormat = DatabaseHelper.getObjectAsString(parserObject, dateFormatType);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "DateFormat: " + _dateFormat);
            _timeFormat = DatabaseHelper.getObjectAsString(parserObject, timeFormatType);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "TimeFormat: " + _timeFormat);
            _decimalSeperator = DatabaseHelper.getObjectAsString(parserObject, decimalSeperatorType);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "DecimalSeperator: " + _decimalSeperator);
            _thousandSeperator = DatabaseHelper.getObjectAsString(parserObject, thousandSeperatorType);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "ThousandSeperator: " + _thousandSeperator);

          
        } catch (JEVisException ex) {
            Logger.getLogger(XMLParsing.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void addDataPointParser(Long datapointID, String target, String mappingIdentifier, String valueIdentifier) {
        _datapointParsers.add(new JSONDatapointParser(datapointID, target, mappingIdentifier, valueIdentifier, _decimalSeperator, _thousandSeperator));    
    }
    
    
    private DateTime getDateTime(String dateTime) {
        String input = dateTime;
        try {
            
            String pattern = _dateFormat;
            DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
            return fmt.parseDateTime(input);
        } catch (Exception ex) {
            ex.printStackTrace();
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "Date not parsable: " + input);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "DateFormat: " + _dateFormat);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "DateIndex: " + _dateIndex);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "TimeFormat: " + _timeFormat);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "TimeIndex: " + _timeIndex);
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.WARN, "Exception: " + ex);
        }

        if (_dateFormat == null) {
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "No Datetime found");
            return null;
        } else {
            org.apache.log4j.Logger.getLogger(this.getClass().getName()).log(org.apache.log4j.Level.ALL, "Current Datetime");
            return new DateTime();
        }

    }
}
