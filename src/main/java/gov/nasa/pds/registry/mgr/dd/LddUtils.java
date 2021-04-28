package gov.nasa.pds.registry.mgr.dd;

import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.opencsv.CSVReader;

import gov.nasa.pds.registry.common.util.CloseUtils;
import gov.nasa.pds.registry.mgr.util.Logger;

/**
 * Simple methods to work with PDS LDD JSON files (data dictionary files).
 *  
 * @author karpenko
 */
public class LddUtils
{
    private static final DateFormat LDD_DateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    
    /**
     * Convert LDD date, e.g., "Wed Dec 23 10:16:28 EST 2020" 
     * to ISO Instant format, e.g., "2020-12-23T15:16:28Z".
     * @param lddDate LDD date from PDS LDD JSON file.
     * @return ISO Instant formatted date
     */
    public static String lddDateToIsoInstant(String lddDate) throws Exception
    {
        Date dt = LDD_DateFormat.parse(lddDate);
        return DateTimeFormatter.ISO_INSTANT.format(dt.toInstant());
    }
    
    
    /**
     * Load LDD list from a configuration file
     * @param file LDD configuration file
     * @return A map, where key = <LDD namespace>, value = <LddInfo object>
     * @throws Exception
     */
    public static Map<String, LddInfo> loadLddList(File file) throws Exception
    {
        Logger.info("Loading LDD list from " + file.getAbsolutePath());
        
        CSVReader rd = null;
        
        try
        {
            rd = new CSVReader(new FileReader(file));
            
            Map<String, LddInfo> map = new TreeMap<>();
            
            String[] values;
            int lineNum = 0;
            while((values = rd.readNext()) != null)
            {
                lineNum++;
                
                if(values.length != 4)
                {
                    Logger.warn("Line " + lineNum + ": Was expecting 4 values, but found " + values.length);
                    continue;
                }
                
                // Namespace
                if(values[0] == null || values[0].isBlank()) 
                {
                    Logger.warn("Line " + lineNum +  ": Missing namespace.");
                    continue;
                }                
                String lddNs = values[0].trim();
                
                // URL
                if(values[1] == null || values[1].isBlank()) 
                {
                    Logger.warn("Line " + lineNum +  ": Missing LDD URL.");
                    continue;
                }
                String lddUrl = values[1].trim();
                
                // Date
                if(values[3] == null || values[3].isBlank()) 
                {
                    Logger.warn("Line " + lineNum +  ": Missing LDD date.");
                    continue;
                }

                String lddStrDate = values[3].trim();
                Instant lddDate = null;
                try
                {
                    lddDate = Instant.parse(lddStrDate);
                }
                catch(Throwable ex)
                {
                    Logger.warn("Line " + lineNum +  ": Could not parse LDD date (ISO Instant): " + lddStrDate);
                    continue;
                }
                
                LddInfo info = new LddInfo(lddNs, lddUrl, lddDate);
                map.put(lddNs, info);
            }
            
            return map;
        }
        finally
        {
            CloseUtils.close(rd);
        }
    }
    
}