/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.samples.signature;

import com.adobe.pdfjt.core.exceptions.PDFSignatureException;
import com.adobe.pdfjt.services.digsig.spi.TimeStampProvider;

import java.io.InputStream;

/**
 * 
 */
public class MyTimeStampProvider implements TimeStampProvider {

    /* (non-Javadoc)
     * @see com.adobe.pdfjt.services.digsig.spi.TimeStampProvider#getDigestAlgorithm()
     */
    @Override
    public String getDigestAlgorithm() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.adobe.pdfjt.services.digsig.spi.TimeStampProvider#getTSAURL()
     */
    @Override
    public String getTSAURL() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.adobe.pdfjt.services.digsig.spi.TimeStampProvider#getTimestampToken()
     */
    @Override
    public byte[] getTimestampToken() throws PDFSignatureException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.adobe.pdfjt.services.digsig.spi.TimeStampProvider#getTimestampTokenSize()
     */
    @Override
    public int getTimestampTokenSize() throws PDFSignatureException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see com.adobe.pdfjt.services.digsig.spi.TimeStampProvider#setDataToTimestamp(byte[])
     */
    @Override
    public void setDataToTimestamp(byte[] arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.adobe.pdfjt.services.digsig.spi.TimeStampProvider#setDataToTimestamp(java.io.InputStream)
     */
    @Override
    public void setDataToTimestamp(InputStream arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.adobe.pdfjt.services.digsig.spi.TimeStampProvider#setDigestAlgorithm(java.lang.String)
     */
    @Override
    public void setDigestAlgorithm(String arg0) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.adobe.pdfjt.services.digsig.spi.TimeStampProvider#setTSAURL(java.lang.String)
     */
    @Override
    public void setTSAURL(String arg0) {
        // TODO Auto-generated method stub

    }

}
