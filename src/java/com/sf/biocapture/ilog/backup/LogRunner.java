/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ilog.backup;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ilog.ILog;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Marcel
 * @since Aug 13, 2017 - 5:01:27 PM
 */
public class LogRunner extends BsClazz implements Runnable {

    private ILog log;

    private File backupFile;

    @Override
    public void run() {
        backupLog();
    }

    public LogRunner(ILog log, File backupFile) {
        this.log = log;
        this.backupFile = backupFile;
    }

    private void backupLog() {
        if (backupFile != null) {
            writeFlie(log.getRequestPayload(), backupFile.getAbsolutePath() + File.separator + "request-payload.xml");
            writeFlie(log.getResponsePayload(), backupFile.getAbsolutePath() + File.separator + "response-payload.xml");
        }
    }

    private void writeFlie(String xmlString, String fileName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            StreamResult result = new StreamResult(new File(fileName));
            transformer.transform(source, result);
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException ex) {
            logger.error("", ex);
        }
    }
}
