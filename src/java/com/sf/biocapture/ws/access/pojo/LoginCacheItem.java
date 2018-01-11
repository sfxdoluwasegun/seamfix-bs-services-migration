/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.access.pojo;

import com.sf.biocapture.app.BsClazz;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Marcel
 * @since Jun 20, 2017 - 3:52:45 PM
 */
public class LoginCacheItem extends BsClazz implements Serializable {

    private static final String KEY = ">";
    public static final String datePatern = "yyyyMMddHHmmss";

    private String username;
    private int attempts;
    private Date loginDate;
    private Date firtAttemptDate;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public Date getFirtAttemptDate() {
        return firtAttemptDate;
    }

    public void setFirtAttemptDate(Date firtAttemptDate) {
        this.firtAttemptDate = firtAttemptDate;
    }

    public static String to(LoginCacheItem cacheItem) {
        if (cacheItem == null) {
            return "";
        }

        String date = "", firtAttemptDate = "";
        SimpleDateFormat sdf = new SimpleDateFormat(datePatern);
        if (cacheItem.getLoginDate() != null) {
            date = sdf.format(cacheItem.getLoginDate());
        }
        if (cacheItem.getFirtAttemptDate() != null) {
            firtAttemptDate = sdf.format(cacheItem.getFirtAttemptDate());
        }
        StringBuilder response = new StringBuilder();
        response.append(cacheItem.getUsername()).append(KEY).append(cacheItem.getAttempts()).append(KEY).append(date).append(KEY).append(firtAttemptDate);
        return response.toString();
    }

    public LoginCacheItem() {
    }

    public LoginCacheItem(String cacheItemStr) {
        if (cacheItemStr != null) {
            String[] items = cacheItemStr.split(KEY);
            this.username = items[0];
            this.attempts = Integer.valueOf(items[1]);
            String date = items[2];
            if (date != null && !date.isEmpty()) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(datePatern);
                    this.loginDate = sdf.parse(date);
                } catch (ParseException ex) {
                    logger.error("", ex);
                }
            }

        }
    }

}
