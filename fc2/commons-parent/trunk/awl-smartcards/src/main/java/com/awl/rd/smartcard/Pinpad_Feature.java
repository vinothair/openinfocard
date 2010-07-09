/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.awl.rd.smartcard;

/**
 *
 * @author Cburdin
 */
public class Pinpad_Feature {

    final static public int VERIFY_PIN = 0;
    final static public int MODIFY_PIN = 1;
    
    final static private int ENTETE_VERIFY_PIN_LENGTH = 19;
    final static private int ENTETE_MODIFY_PIN_LENGTH = 0;

    // Ensemble des codes de fonctionnalite definis dans la norme CCID
    final static public byte FEATURE_VERIFY_PIN_START = 0x01;
    final static public byte FEATURE_VERIFY_PIN_FINISH = 0x02;
    final static public byte FEATURE_MODIFY_PIN_START = 0x03;
    final static public byte FEATURE_MODIFY_PIN_FINISH = 0x04;
    final static public byte FEATURE_GET_KEY_PRESSED = 0x05;
    final static public byte FEATURE_VERIFY_PIN_DIRECT = 0x06;
    final static public byte FEATURE_MODIFY_PIN_DIRECT = 0x07;
    final static public byte FEATURE_MCT_READER_DIRECT = 0x08;
    final static public byte FEATURE_MCT_UNIVERSAL = 0x09;
    final static public byte FEATURE_IFD_PIN_PROPERTIES = 0x0A;
    final static public byte FEATURE_ABORT = 0x0B;
    final static public byte FEATURE_SET_SPE_MESSAGE = 0x0C;
    final static public byte FEATURE_VERIFY_PIN_DIRECT_APP_ID = 0x0D;
    final static public byte FEATURE_MODIFY_PIN_DIRECT_APP_ID = 0x0E;
    final static public byte FEATURE_WRITE_DISPLAY = 0x0F;
    final static public byte FEATURE_GET_KEY = 0x10;
    final static public byte FEATURE_IFD_DISPLAY_PROPERTIES = 0x11;

    // Choix du mode
    private int mode = VERIFY_PIN;

    // Champ commun au Verify PIN et au Modify PIN
    /** timeout in seconds (00 means use default timeout */
    private byte bTimeOut = 0;
    /** timeout in seconds after first key stroke */
    private byte bTimeOut2 = 0;
    /** formating option */
    private byte bmFormatString = 0;
    /**bits 7-4 bit size of PIN length in APDU, bits 3-0 PIN block size in bytes after justification and formatting */
    private byte bmPINBlockString;
    /** bits 7-5 RFU, bit 4 set if system units are bytes clear if system units are bits, bits 3-0 PIN length position in system units */
    private byte bmPINLengthFormat;
    /** XXYY, where XX is minimum PIN size in digits, YY is maximum */
    private short wPINMaxExtraDigit;
    /** Conditions under which PIN entry should be considered complete */
    private byte bEntryValidationCondition;
    /** Number of messages to display for PIN verification */
    private byte bNumberMessage;
    /** Language for messages */
    private short wLangId;
    /** T=1 I-block prologue field to use (fill with 00) */
    private byte[] bTeoPrologue = new byte[3];
    /** length of Data to be sent to the ICC */
    private int ulDataLength;
    /** Data to send to the ICC */
    private byte[] abData;
    // Champs pour le Verify PIN
    /** Message index (should be 00) */
    private byte bMsgIndex;

    // Champs pour le Modify PIN
    /** Insertion position offset in bytes for the current PIN */
    private byte bInsertionOffsetOld;
    /** Insertion position offset in bytes for the new PIN */
    private byte bInsertionOffsetNew;
    /** Flags governing need for confirmation of new PIN */
    private byte bConfirmPIN;
    /** Index of 1st prompting message */
    private byte bMsgIndex1;
    /** Index of 2d prompting message */
    private byte bMsgIndex2;
    /** Index of 3d prompting message */
    private byte bMsgIndex3;

    public void setMode(int md) {
        mode = md;
    }

    public void setTimeOut(byte data) {
        bTimeOut = data;
    }

    public void setTimeOut2(byte data) {
        bTimeOut2 = data;
    }

    public void setFormatString(byte data) {
        bmFormatString = data;
    }

    public void setPINBlockString(byte data) {
        bmPINBlockString = data;
    }

    public void setPINLengthFormat(byte data) {
        bmPINLengthFormat = data;
    }

    public void setPINMaxExtraDigit(short data) {
        wPINMaxExtraDigit = data;
    }

    public void setEntryValidationCondition(byte data) {
        bEntryValidationCondition = data;
    }

    public void setNumberMessage(byte data) {
        bNumberMessage = data;
    }

    public void setLangId(short data) {
        wLangId = data;
    }

    public void setTeoPrologue(byte[] data) {
        System.arraycopy(data, 0, bTeoPrologue, 0, 3);
    }

    public void setDataLength(int data) {
        ulDataLength = data;
    }

    public void setData(byte[] data) {
        abData = new byte[ulDataLength];
        System.arraycopy(data, 0, abData, 0, ulDataLength);
    }

    public void setMsgIndex(byte data) {
        bMsgIndex = data;
    }

    public void setInsertionOffsetOld(byte data) {
        bInsertionOffsetOld = data;
    }

    public void setInsertionOffsetNew(byte data) {
        bInsertionOffsetNew = data;
    }

    public void setConfirmPIN(byte data) {
        bConfirmPIN = data;
    }

    public void setMsgIndex1(byte data) {
        bMsgIndex1 = data;
    }

    public void setMsgIndex2(byte data) {
        bMsgIndex2 = data;
    }

    public void setMsgIndex3(byte data) {
        bMsgIndex3 = data;
    }

    public byte[] generateControl() {
        byte[] data = null;
        switch (mode) {
            case MODIFY_PIN:
                break;
            case VERIFY_PIN:
                data = new byte[ENTETE_VERIFY_PIN_LENGTH + ulDataLength];
                data[0] = bTimeOut;
                data[1] = bTimeOut2;
                data[2] = bmFormatString;
                data[3] = bmPINBlockString;
                data[4] = bmPINLengthFormat;
                data[5] = (byte) (wPINMaxExtraDigit & 0x00FF);
                data[6] = (byte) (wPINMaxExtraDigit >> 8);
                data[7] = bEntryValidationCondition;
                data[8] = bNumberMessage;
                data[9] = (byte) (wLangId & 0x00FF);
                data[10] = (byte) (wLangId >> 8);
                data[11] = bMsgIndex;
                System.arraycopy(bTeoPrologue, 0, data, 12, 3);
                System.arraycopy(IntToByte(ulDataLength), 0, data, 15, 4);
                System.arraycopy(abData, 0, data, 19, ulDataLength);
                break;
            default:
        }

        return data;
    }

    public int CTL_CODE(int code) {
        String os_name = System.getProperty("os.name").toLowerCase();
        if (os_name.indexOf("windows") > -1) {
            // cf. WinIOCTL.h
            return (0x31 << 16 | (code) << 2);
        }
        // cf. reader.h
        return 0x42000000 + (code);
    }

    private byte[] IntToByte(int value) {
        byte[] data = new byte[4];
        for (int i = 0; i < 4; i++) {
            data[3 - i] = (byte) ((value >> (8 * (3 - i))) & 0x000000FF);
        }
        return data;
    }

    public int GetControlFeature(int feature, byte[] data) throws Exception {
        int dwOffsetTag = 0;
        int dwTagStatus = 0;

        do {
            if (data[dwOffsetTag] != feature) {
                dwOffsetTag++;
                dwOffsetTag += data[dwOffsetTag] + 1;
            } else {
                dwTagStatus = 1;
            }
        } while ((dwOffsetTag < data.length) && (dwTagStatus != 1));
        dwOffsetTag += 2;

        if (dwTagStatus != 0) {
            int info = 0;
            for (int i = 0; i < 4; i++) {
                System.out.print(String.format("%02X ", data[dwOffsetTag + i]));
                info += (int) ((data[dwOffsetTag + i] & 0x000000FF) << (8 * (3 - i)));
            }
            System.out.println();
            return info;
        } else {
            throw new Exception("No feature found");
        }
    }
}
