package com.echosmart.flashlabs.database;

import android.content.Context;
import android.util.Xml;

import com.echosmart.flashlabs.data.model.XGecuChipDevice;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser XmlPullParser ultra-rápido para procesar la base de datos oficial XGecu infoic.xml
 * desde la carpeta assets sin agotar la memoria RAM del teléfono.
 */
public class XmlChipParser {

    public static List<XGecuChipDevice> parseDatabaseFromAssets(Context context, String filename, String searchQuery, int maxResults) {
        List<XGecuChipDevice> results = new ArrayList<>();
        String queryLower = searchQuery != null ? searchQuery.toLowerCase().trim() : "";

        try (InputStream is = context.getAssets().open(filename)) {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, "UTF-8");

            int eventType = parser.getEventType();
            String currentManufacturer = "Generic";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tagName = parser.getName();
                    if ("manufacturer".equalsIgnoreCase(tagName)) {
                        currentManufacturer = parser.getAttributeValue(null, "name");
                        if (currentManufacturer == null) currentManufacturer = "Generic";
                    } else if ("ic".equalsIgnoreCase(tagName)) {
                        String name = parser.getAttributeValue(null, "name");
                        if (name != null) {
                            boolean matches = queryLower.isEmpty() || 
                                              name.toLowerCase().contains(queryLower) || 
                                              currentManufacturer.toLowerCase().contains(queryLower);

                            if (matches) {
                                int type = parseHexOrDec(parser.getAttributeValue(null, "type"));
                                int protocolId = parseHexOrDec(parser.getAttributeValue(null, "protocol_id"));
                                int variant = parseHexOrDec(parser.getAttributeValue(null, "variant"));
                                int readBuf = parseHexOrDec(parser.getAttributeValue(null, "read_buffer_size"));
                                int writeBuf = parseHexOrDec(parser.getAttributeValue(null, "write_buffer_size"));
                                long codeMem = parseHexOrDecLong(parser.getAttributeValue(null, "code_memory_size"));
                                int dataMem = parseHexOrDec(parser.getAttributeValue(null, "data_memory_size"));
                                int dataMem2 = parseHexOrDec(parser.getAttributeValue(null, "data_memory2_size"));
                                int pageSize = parseHexOrDec(parser.getAttributeValue(null, "page_size"));
                                int pagesPerBlock = parseHexOrDec(parser.getAttributeValue(null, "pages_per_block"));
                                long chipId = parseHexOrDecLong(parser.getAttributeValue(null, "chip_id"));
                                int voltages = parseHexOrDec(parser.getAttributeValue(null, "voltages"));
                                int pulseDelay = parseHexOrDec(parser.getAttributeValue(null, "pulse_delay"));
                                long flags = parseHexOrDecLong(parser.getAttributeValue(null, "flags"));
                                int chipInfo = parseHexOrDec(parser.getAttributeValue(null, "chip_info"));
                                long pinMap = parseHexOrDecLong(parser.getAttributeValue(null, "pin_map"));
                                long pkgDetails = parseHexOrDecLong(parser.getAttributeValue(null, "package_details"));
                                String configStr = parser.getAttributeValue(null, "config");

                                XGecuChipDevice device = new XGecuChipDevice(
                                        name, currentManufacturer, type, protocolId, variant,
                                        readBuf, writeBuf, codeMem, dataMem, dataMem2,
                                        pageSize, pagesPerBlock, chipId, voltages, pulseDelay,
                                        flags, chipInfo, pinMap, pkgDetails, configStr != null ? configStr : "NULL"
                                );
                                results.add(device);

                                if (maxResults > 0 && results.size() >= maxResults) {
                                    break;
                                }
                            }
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    private static int parseHexOrDec(String val) {
        if (val == null || val.isEmpty() || "NULL".equalsIgnoreCase(val)) return 0;
        try {
            if (val.startsWith("0x") || val.startsWith("0X")) {
                return (int) Long.parseLong(val.substring(2), 16);
            }
            return Integer.parseInt(val);
        } catch (Exception e) {
            return 0;
        }
    }

    private static long parseHexOrDecLong(String val) {
        if (val == null || val.isEmpty() || "NULL".equalsIgnoreCase(val)) return 0L;
        try {
            if (val.startsWith("0x") || val.startsWith("0X")) {
                return Long.parseLong(val.substring(2), 16);
            }
            return Long.parseLong(val);
        } catch (Exception e) {
            return 0L;
        }
    }
}
