package me.cychen.rts.util;

import me.cychen.rts.event.EventContainer;
import me.cychen.util.file.FileHandler;

import java.io.IOException;

/**
 * Created by jjs on 2/13/17.
 */
public class LogHandler extends FileHandler{
    private LogParser logParser;

    public LogHandler() {
    }

    public int initializeLogParser() throws IOException {
        if (this.isFileReaderOpened() == false) {
            throw new IOException("Log file is not opened.");
        }

        // The version has to be at the first line. (except commented lines.)
        int versionNumber = 0;
        String line;
        line = readNextUncommentedLine();

        if (line.trim().toLowerCase().equalsIgnoreCase("@LogFormat")) {
            line = readNextUncommentedLine().trim().toLowerCase();
            //todo: need handle when the obtained version number is not a number.
            versionNumber = Integer.valueOf(line);
        } else {
            throw new IOException("Cannot find log format number at the first line.");
        }

        // We've got version number, now initialize the logParser.
        switch (versionNumber) {
            case V11LogParser.PARSER_VERSION:
                logParser = new V11LogParser();
                break;
            default:
                throw new IOException("Cannot find log format number at the first line.");
                //break;
        }

        return logParser.getParserVersion();
    }

    public String readNextUncommentedLine() throws IOException {
        if (this.isFileReaderOpened() == false) {
            throw new IOException("Log file is not opened.");
        }

        String line;
        while (true) {
            line = fileReader.readLine();
            if (line.trim().substring(0, 1).equalsIgnoreCase("#")) // Comment line
                continue;
            else
                return line;
        }
    }

    public EventContainer loadDemoLog()
    {
        String demoLogFilePath = "./log/demoLog1.txt";
        if (parseLog(demoLogFilePath) == true) {
            return logParser.getEventContainer();
        } else {
            return null;
        }

//        this.openFile(demoLogFilePath);
//        if (loadLog(this.openFile(demoLogFilePath)) == false)
//            throw new InvalidParameterException("Demo log file is incorrect.");
//        else
//            return eventContainer;
    }

    public Boolean parseLog(String inLogFilePath) {
        try {
            this.openFile(inLogFilePath);
            initializeLogParser();
            logParser.parseLog(this.fileReader);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }






}
