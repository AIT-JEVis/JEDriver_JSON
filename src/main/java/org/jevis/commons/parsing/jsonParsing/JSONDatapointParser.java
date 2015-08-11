/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.commons.parsing.jsonParsing;

import org.jevis.commons.parsing.GeneralMappingParser;
import org.jevis.commons.parsing.inputHandler.InputHandler;

/**
 *
 * @author ait-user
 */
public class JSONDatapointParser implements GeneralMappingParser {

    private boolean _isInFile;
    private Long _datapoint;
    private final Long _target;
    private final String _mappingIdentifier;
    private final String _valueIdentifier;
    private final String _decimalSep;
    private final String _thousandSep;
    
    
    public JSONDatapointParser(Long datapointID, String target, String mappingIdentifier, String valueIdentifier, String decimalSeperator, String thousandSeperator) {
        _datapoint = datapointID;
        _target = Long.parseLong(target);
        _mappingIdentifier = mappingIdentifier;
        _valueIdentifier = valueIdentifier;
        _decimalSep = decimalSeperator;
        _thousandSep = thousandSeperator;
    }
    
    
    
    
    
    public boolean isInFile() {
        return _isInFile;
    }

    public Long getDatapoint() {
        return _datapoint;
    }

    public String getMappingValue() {
        return _mappingIdentifier;
    }

    public boolean isMappingFailing() {
        return true; //no mapping needed
    }

    public boolean outOfBounce() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public double getValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Long getTarget() {
        return _target;
    }

    public void parse(InputHandler ic) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isValueValid() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public String getValueIdentifier(){
        return _valueIdentifier;
    }
    
}
